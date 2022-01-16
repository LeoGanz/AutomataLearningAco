package ganz.leonard.automatalearning.paramtests;

import ganz.leonard.automatalearning.InputProvider;
import ganz.leonard.automatalearning.automata.probability.function.SigmoidSpreadPheromoneFunction;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import ganz.leonard.automatalearning.learning.AutomataLearningOptions;
import ganz.leonard.automatalearning.learning.AutomataLearningOptionsBuilder;
import ganz.leonard.automatalearning.learning.InputWord;
import ganz.leonard.automatalearning.learning.IntermediateResult;
import ganz.leonard.automatalearning.paramtests.execution.TestDataSaver;
import ganz.leonard.automatalearning.paramtests.execution.TestRunner;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class TestingUtil {
  public static final int NR_ITERATIONS = 10;
  public static final int NR_COLONIES = 2000;
  public static final String MEASUREMENTS_DIR = "measurements";
  public static final String INPUT_PACKAGE = "ganz/leonard/automatalearning/input/";

  public static List<InputWord<Object>> loadInput(String filename)
      throws IOException, URISyntaxException {
    Path path = Paths.get(ClassLoader.getSystemResource(INPUT_PACKAGE + "/" + filename).toURI());
    return InputProvider.readFromFile(path);
  }

  public static AutomataLearningOptionsBuilder getOptionsBuilder() {
    return AutomataLearningOptionsBuilder.builder()
        .acceptingStates(2)
        .notAcceptingStates(1)
        .colonySize(16)
        .decayFactor(0.8)
        .feedback(0.5)
        .balanceInput(true)
        .pheromoneFunction(
            new SigmoidSpreadPheromoneFunction(AutomataLearningOptions.DEF_SIGMOID, 0.14, 0.016));
  }

  public static double calcAverageMatch(
      List<IntermediateResult<Object>> intermediateResults,
      List<InputWord<Object>> referenceWords) {
    return intermediateResults.stream()
        .map(IntermediateResult::automaton)
        .map(dfa -> AutomataLearning.calcMatchingInputScore(dfa, referenceWords))
        .mapToDouble(Double::doubleValue)
        .average()
        .orElse(-1);
  }

  public static List<IntermediateResult<Object>> runTest(
      List<InputWord<Object>> input, String testName, AutomataLearningOptions options)
      throws IOException {
    return runTest(input, testName, options, true);
  }

  public static List<IntermediateResult<Object>> runTest(
      List<InputWord<Object>> input,
      String testName,
      AutomataLearningOptions options,
      boolean ignoreRegex)
      throws IOException {
    AtomicReference<List<IntermediateResult<Object>>> results = new AtomicReference<>();
    TestDataSaver dataSaver = new TestDataSaver(testName);
    CompletableFuture<List<IntermediateResult<Object>>> stats =
        TestRunner.test(
            options,
            input,
            dataSaver.beginOnlySubtest(options, input),
            NR_ITERATIONS,
            NR_COLONIES,
            false,
            ignoreRegex);
    stats
        .thenAccept(
            res -> {
              results.set(res);
              dataSaver.close();
            })
        .join();
    return results.get();
  }
}
