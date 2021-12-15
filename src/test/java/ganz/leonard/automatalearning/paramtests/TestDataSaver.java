package ganz.leonard.automatalearning.paramtests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ganz.leonard.automatalearning.learning.AutomataLearningOptions;
import ganz.leonard.automatalearning.util.PairStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.csveed.api.CsvClientImpl;

public class TestDataSaver implements Closeable {

  private final String testName;
  private final Gson gson;
  private int subTestId = 0;
  private Set<DataSaverSubtest> subtests;

  public TestDataSaver(String testName) {
    this.testName = testName;
    subtests = new HashSet<>();
    gson = new GsonBuilder().setPrettyPrinting().create();
  }

  public DataSaverSubtest beginOnlySubtest(AutomataLearningOptions options) throws IOException {
    if (subTestId != 0) {
      throw new IllegalStateException("This is not the only subtest!");
    }

    DataSaverSubtest subtest = new DataSaverSubtest(Optional.empty(), options);
    subtests.add(subtest);
    return subtest;
  }

  public DataSaverSubtest beginSubtest(
      AutomataLearningOptions options, Map<String, String> testParamName_valueName)
      throws IOException {

    StringBuilder sb = new StringBuilder("subtest-");
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
    DataSaverSubtest subtest = new DataSaverSubtest(Optional.of(sb.toString()), options);
    subtests.add(subtest);
    return subtest;
  }

  @Override
  public void close() {
    subtests.forEach(DataSaverSubtest::close);
  }

  public static class DataSaverIteration implements Closeable {
    private final CsvClientImpl<DataRow> csvClient;
    private final Writer writer;

    public DataSaverIteration(Path csvFile) throws IOException {
      Files.createDirectories(csvFile.getParent());
      writer = Files.newBufferedWriter(csvFile, StandardCharsets.UTF_8);
      csvClient = new CsvClientImpl<>(writer, DataRow.class);
      csvClient.setConverter("dfa", new DfaConverter());
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
    private final Set<DataSaverIteration> iterations;
    private Path folder;

    public DataSaverSubtest(Optional<String> subTestName, AutomataLearningOptions options)
        throws IOException {
      iterations = new HashSet<>();
      folder = Paths.get("measurements", testName);
      subTestName.ifPresent(s -> folder = folder.resolve(s));
      String optionsJson = gson.toJson(options);
      Files.writeString(folder.resolve(OPTIONS_FILE_NAME), optionsJson, StandardCharsets.UTF_8);
    }

    public DataSaverIteration beginIteration(int iteration) throws IOException {
      Path csvFile = folder.resolve(iteration + ".csv");
      DataSaverIteration dataSaverIteration = new DataSaverIteration(csvFile);
      iterations.add(dataSaverIteration);
      return dataSaverIteration;
    }

    @Override
    public void close() {
      iterations.forEach(DataSaverIteration::close);
    }
  }
}
