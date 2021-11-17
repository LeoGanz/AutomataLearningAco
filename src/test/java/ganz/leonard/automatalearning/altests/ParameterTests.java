package ganz.leonard.automatalearning.altests;

import ganz.leonard.automatalearning.gui.optionsscreen.InputProvider;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import ganz.leonard.automatalearning.learning.AutomataLearningOptions;
import ganz.leonard.automatalearning.learning.AutomataLearningOptionsBuilder;
import ganz.leonard.automatalearning.learning.IntermediateResult;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ParameterTests {

  private static final String FILE_PREFIX = "ganz/leonard/automatalearning/input/";
  private static final String FILENAME = "AstarBstar.txt";
  private static final int ITERATIONS = 100;
  private static final AutomataLearningOptionsBuilder defaultOptionsBuilder =
      AutomataLearningOptionsBuilder.builder().acceptingStates(2).notAcceptingStates(2);
  private static Map<List<Object>, Boolean> input;

  @BeforeAll
  static void setUp() throws IOException, URISyntaxException {
    Path path = Paths.get(ClassLoader.getSystemResource(FILE_PREFIX + "/" + FILENAME).toURI());
    input = InputProvider.readFromFile(path);
  }

  OptionalDouble runTest(AutomataLearningOptions options) {
    System.out.println(
        "Starting statistics for test language in file '"
            + FILENAME
            + "with options: "
            + options
            + "' ...");
    Set<CompletableFuture<IntermediateResult<Object>>> learners = new HashSet<>(ITERATIONS);
    for (int i = 0; i < ITERATIONS; i++) {
      learners.add(
          CompletableFuture.supplyAsync(
              () -> {
                AutomataLearning<Object> al = new AutomataLearning<>(options, input);
                al.runWords(2000);
                return al.getBestResult();
              }));
    }
    Set<IntermediateResult<Object>> results =
        learners.stream().map(CompletableFuture::join).collect(Collectors.toSet());

    OptionalDouble avgScore = results.stream().mapToDouble(IntermediateResult::score).average();
    OptionalDouble avgSteps =
        results.stream().mapToDouble(IntermediateResult::nrAppliedWords).average();
    System.out.println("Average Score: " + avgScore.orElse(-1));
    System.out.println("Average Words applied: " + avgSteps.orElse(-1));
    return avgScore;
  }

  @Test
  void defaultOptions() {
    StringBuilder sb = new StringBuilder("Testing no. of accepting and not accepting states: \n");
    for (int i = 1; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        sb.append(i)
            .append(" acc., ")
            .append(j)
            .append(" not acc. -> score: ")
            .append(
                runTest(defaultOptionsBuilder.acceptingStates(i).notAcceptingStates(j).build())
                    .orElse(-1))
            .append("\n");
      }
    }
    System.out.println(sb);
  }
}
