package ganz.leonard.automatalearning.paramtests.execution;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ganz.leonard.automatalearning.automata.probability.function.PheromoneFunction;
import ganz.leonard.automatalearning.automata.probability.function.SigmoidSpreadPheromoneFunction;
import ganz.leonard.automatalearning.learning.AutomataLearningOptions;
import ganz.leonard.automatalearning.learning.InputWord;
import ganz.leonard.automatalearning.paramtests.TestingUtil;
import ganz.leonard.automatalearning.paramtests.conversion.DfaConverter;
import ganz.leonard.automatalearning.paramtests.conversion.PheromoneFunctionSerializer;
import ganz.leonard.automatalearning.paramtests.conversion.SigmoidSpreadPheromoneFunctionSerializer;
import ganz.leonard.automatalearning.paramtests.conversion.PlainIntegerConverter;
import ganz.leonard.automatalearning.paramtests.conversion.StringifyableFunctionSerializer;
import ganz.leonard.automatalearning.util.PairStream;
import ganz.leonard.automatalearning.util.StringifyableFunction;
import ganz.leonard.automatalearning.util.Util;
import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.csveed.api.CsvClient;
import org.csveed.api.CsvClientImpl;

public class TestDataSaver implements Closeable {

  public static final String SUBTEST_PREFIX = "subtest-";
  private final String testName;
  private final Gson gson;
  private final Set<DataSaverSubtest> subtests;
  private int subTestId = 0;

  public TestDataSaver(String testName) {
    this.testName = testName;
    subtests = new HashSet<>();
    gson =
        new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .registerTypeAdapter(
                StringifyableFunction.class, new StringifyableFunctionSerializer<>())
            .registerTypeAdapter(SigmoidSpreadPheromoneFunction.class, new SigmoidSpreadPheromoneFunctionSerializer())
            .registerTypeAdapter(PheromoneFunction.class, new PheromoneFunctionSerializer())
            .create();
  }

  public <T> DataSaverSubtest beginOnlySubtest(
      AutomataLearningOptions options, List<InputWord<T>> input) throws IOException {
    if (subTestId != 0) {
      throw new IllegalStateException("This is not the only subtest!");
    }

    DataSaverSubtest subtest = new DataSaverSubtest(Optional.empty(), options, input);
    subtests.add(subtest);
    return subtest;
  }

  public <T> DataSaverSubtest beginSubtest(
      AutomataLearningOptions options,
      List<InputWord<T>> input,
      Map<String, String> testParamName_valueName)
      throws IOException {

    StringBuilder sb = new StringBuilder(SUBTEST_PREFIX);
    sb.append(subTestId++);
    PairStream.from(testParamName_valueName)
        .sortedByKey(Comparator.naturalOrder())
        .forEach(
            (testParamName, valueName) -> {
              sb.append(" ");
              sb.append(testParamName);
              sb.append("_");
              sb.append(valueName);
            });
    DataSaverSubtest subtest = new DataSaverSubtest(Optional.of(sb.toString()), options, input);
    subtests.add(subtest);
    return subtest;
  }

  @Override
  public void close() {
    subtests.forEach(DataSaverSubtest::close);
  }

  public static class DataSaverIteration implements Closeable {
    private final CsvClient<DataRow> csvClient;
    private final Writer writer;

    private DataSaverIteration(Path csvFile, boolean ignoreDfa, boolean ignoreRegex)
        throws IOException {
      Files.createDirectories(csvFile.getParent());
      writer = Files.newBufferedWriter(csvFile, StandardCharsets.UTF_8);
      csvClient =
          new CsvClientImpl<>(writer, DataRow.class)
              .setConverter("dfa", new DfaConverter())
              .setConverter("colony", new PlainIntegerConverter());
      if (ignoreDfa) {
        csvClient.ignoreProperty("dfa");
      }
      if (ignoreRegex) {
        csvClient.ignoreProperty("regex");
      }
      csvClient.writeHeader();
    }

    public void writeRow(DataRow row) {
      csvClient.writeBean(row);
    }

    @Override
    public void close() {
      if (writer != null) {
        try {
          writer.close();
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }
    }
  }

  public class DataSaverSubtest implements Closeable {
    private static final String OPTIONS_FILE_NAME = "options.json";
    private static final String STATISTICS_FILE_NAME = "stats.json";
    private static final String INPUT_FILE_NAME = "input.txt";
    private final Set<DataSaverIteration> iterations;
    private Path folder;

    private <T> DataSaverSubtest(
        Optional<String> subTestName, AutomataLearningOptions options, List<InputWord<T>> input)
        throws IOException {
      iterations = new HashSet<>();
      folder = Paths.get(TestingUtil.MEASUREMENTS_DIR, testName);
      subTestName.ifPresent(s -> folder = folder.resolve(s.strip()));
      Util.deleteDirectoryRecursively(folder);
      Files.createDirectories(folder);
      String optionsJson = gson.toJson(options);
      Files.writeString(folder.resolve(OPTIONS_FILE_NAME), optionsJson, StandardCharsets.UTF_8);
      for (InputWord<T> word : input) {
        Files.writeString(
            folder.resolve(INPUT_FILE_NAME),
            word.toString() + "\n",
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE,
            StandardOpenOption.APPEND);
      }
    }

    public DataSaverIteration beginIteration(int iteration, boolean ignoreDfa, boolean ignoreRegex)
        throws IOException {
      Path csvFile = folder.resolve(iteration + ".csv");
      DataSaverIteration dataSaverIteration =
          new DataSaverIteration(csvFile, ignoreDfa, ignoreRegex);
      iterations.add(dataSaverIteration);
      return dataSaverIteration;
    }

    @Override
    public void close() {
      iterations.forEach(DataSaverIteration::close);
    }

    public void writeStatistics(Statistics stats) throws IOException {
      String statsJson = gson.toJson(stats);
      Files.writeString(folder.resolve(STATISTICS_FILE_NAME), statsJson, StandardCharsets.UTF_8);
    }
  }
}
