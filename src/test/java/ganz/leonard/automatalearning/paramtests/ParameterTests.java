package ganz.leonard.automatalearning.paramtests;

import ganz.leonard.automatalearning.gui.optionsscreen.InputProvider;
import ganz.leonard.automatalearning.learning.AutomataLearningOptions;
import ganz.leonard.automatalearning.learning.AutomataLearningOptionsBuilder;
import ganz.leonard.automatalearning.learning.InputWord;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ParameterTests {

  public static final int MAX_STATES = 4;
  private static final String FILE_PREFIX = "ganz/leonard/automatalearning/input/";
  private static final String FILENAME = "AstarB.txt";
  private static final AutomataLearningOptionsBuilder DEFAULT_OPTIONS_BUILDER =
      AutomataLearningOptionsBuilder.builder()
          .feedback(1)
          .colonySize(50)
          .acceptingStates(2)
          .notAcceptingStates(2);
  private static List<InputWord<Object>> input;

  @BeforeAll
  static void setUp() throws IOException, URISyntaxException {
    Path path = Paths.get(ClassLoader.getSystemResource(FILE_PREFIX + "/" + FILENAME).toURI());
    input = InputProvider.readFromFile(path);
  }

  @Test
  void testDefaultOptions() throws IOException {
    TestDataSaver dataSaver = new TestDataSaver("DefaultOptions");
    AutomataLearningOptions options = DEFAULT_OPTIONS_BUILDER.build();
    CompletableFuture<OptionalDouble> stats =
        Statistics.calculate(options, input, dataSaver.beginOnlySubtest(options, input));
    stats.thenAccept(ignored -> dataSaver.close()).join();
  }

  @Test
  void testNumberOfStates() throws IOException {
    TestDataSaver dataSaver = new TestDataSaver("StateNumbers");
    StringBuilder sb = new StringBuilder("Testing no. of accepting and not accepting states: \n");
    for (int acceptingStates = 1; acceptingStates < MAX_STATES; acceptingStates++) {
      for (int notAccStates = 0; notAccStates < MAX_STATES; notAccStates++) {
        AutomataLearningOptions options =
            DEFAULT_OPTIONS_BUILDER
                .acceptingStates(acceptingStates)
                .notAcceptingStates(notAccStates)
                .build();
        TestDataSaver.DataSaverSubtest dataSaverSubtest =
            dataSaver.beginSubtest(
                options,
                input,
                Map.of(
                    "accepting",
                    String.valueOf(acceptingStates),
                    "notAccepting",
                    String.valueOf(notAccStates)));
        CompletableFuture<OptionalDouble> stats =
            Statistics.calculate(options, input, dataSaverSubtest);
        int finalAcceptingStates = acceptingStates;
        int finalNotAccStates = notAccStates;
        stats
            .thenAccept(
                avgScore ->
                    sb.append(finalAcceptingStates)
                        .append(" acc., ")
                        .append(finalNotAccStates)
                        .append(" not acc. -> score: ")
                        .append(avgScore.orElse(-1))
                        .append("\n"))
            .join();
      }
    }
    dataSaver.close();
    System.out.println(sb);
  }
}
