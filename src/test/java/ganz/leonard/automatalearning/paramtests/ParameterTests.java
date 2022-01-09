package ganz.leonard.automatalearning.paramtests;

import ganz.leonard.automatalearning.InputProvider;
import ganz.leonard.automatalearning.automata.probability.PheromoneFunction;
import ganz.leonard.automatalearning.learning.AutomataLearningOptions;
import ganz.leonard.automatalearning.learning.AutomataLearningOptionsBuilder;
import ganz.leonard.automatalearning.learning.InputWord;
import ganz.leonard.automatalearning.learning.IntermediateResult;
import ganz.leonard.automatalearning.paramtests.execution.TestDataSaver;
import ganz.leonard.automatalearning.paramtests.execution.TestRunner;
import ganz.leonard.automatalearning.util.StringifyableFunction;
import ganz.leonard.automatalearning.util.Util;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ParameterTests {

  private static final int NR_COLONIES_SMALL = 500;
  private static final int NR_COLONIES_MEDIUM = 1000;
  private static final int NR_COLONIES_LARGE = 2000;
  private static final int MAX_STATES = 4;
  private static final int MAX_COLONY_SIZE = 100;
  private static final double MAX_FEEDBACK = 3;
  private static final double MAX_INITIAL_PHEROMONE = 10;
  private static final String FILENAME = "Test-AstarB-ParamOptim.txt";

  private static List<InputWord<Object>> input;

  @BeforeAll
  static void setUp() throws IOException, URISyntaxException {
    Path path = Paths.get(ClassLoader.getSystemResource(TestingUtil.INPUT_PACKAGE + "/" + FILENAME).toURI());
    input = InputProvider.readFromFile(path);
  }

  private static AutomataLearningOptionsBuilder getOptionsBuilder() {
    return AutomataLearningOptionsBuilder.builder()
        .acceptingStates(2)
        .notAcceptingStates(1)
        .colonySize(16)
        .decayFactor(0.8)
        .feedback(0.5)
        .balanceInput(true)
        .pheromoneFunction(new PheromoneFunction(getSigmoid(), 0.16, 0.016));
  }

  private static StringifyableFunction<Double, Double> getSigmoid() {
    return AutomataLearningOptions.DEF_SIGMOID;
  }

  @Test
  void testDefaultOptions() throws IOException {
    TestDataSaver dataSaver = new TestDataSaver("DefaultOptions");
    AutomataLearningOptions options = getOptionsBuilder().build();
    CompletableFuture<List<IntermediateResult<Object>>> stats =
        TestRunner.test(
            options,
            input,
            dataSaver.beginOnlySubtest(options, input),
            NR_COLONIES_LARGE,
            false,
            false);
    stats.thenAccept(ignored -> dataSaver.close()).join();
  }

  @Test
  void testInputBalancer() throws IOException {
    TestDataSaver dataSaver = new TestDataSaver("NoInputBalancing");
    AutomataLearningOptions options = getOptionsBuilder().balanceInput(false).build();
    CompletableFuture<List<IntermediateResult<Object>>> stats =
        TestRunner.test(
            options,
            input,
            dataSaver.beginOnlySubtest(options, input),
            NR_COLONIES_LARGE,
            false,
            true);
    stats.thenAccept(ignored -> dataSaver.close()).join();
  }

  @Test
  void testNumberOfStates() throws IOException {
    TestDataSaver dataSaver = new TestDataSaver("StateNumbers");
    for (int acceptingStates = 1; acceptingStates < MAX_STATES; acceptingStates++) {
      for (int notAccStates = 0; notAccStates < MAX_STATES; notAccStates++) {
        AutomataLearningOptions options =
            getOptionsBuilder()
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
        CompletableFuture<List<IntermediateResult<Object>>> stats =
            TestRunner.test(options, input, dataSaverSubtest, NR_COLONIES_LARGE, false, true);
        stats.join();
      }
    }
    dataSaver.close();
  }

  @Test
  void testColonySize() throws IOException {
    TestDataSaver dataSaver = new TestDataSaver("ColonySize");
    for (int colonySize = 1; colonySize < MAX_COLONY_SIZE; colonySize++) {
      AutomataLearningOptions options = getOptionsBuilder().colonySize(colonySize).build();
      TestDataSaver.DataSaverSubtest dataSaverSubtest =
          dataSaver.beginSubtest(options, input, Map.of("ColonySize", String.valueOf(colonySize)));
      TestRunner.test(options, input, dataSaverSubtest, NR_COLONIES_SMALL).join();
    }
    dataSaver.close();
  }

  @Test
  void testFeedback() throws IOException {
    TestDataSaver dataSaver = new TestDataSaver("FeedbackAmount");
    for (double feedback = 0.1; feedback < MAX_FEEDBACK; feedback += .1) {
      AutomataLearningOptions options = getOptionsBuilder().feedback(feedback).build();
      TestDataSaver.DataSaverSubtest dataSaverSubtest =
          dataSaver.beginSubtest(
              options, input, Map.of("Feedback", Util.formatDouble(feedback, 2)));
      TestRunner.test(options, input, dataSaverSubtest, NR_COLONIES_MEDIUM).join();
    }
    dataSaver.close();
  }

  @Test
  void testInitialPheromone() throws IOException {
    TestDataSaver dataSaver = new TestDataSaver("InitialPheromone");
    for (int initial = -10; initial <= MAX_INITIAL_PHEROMONE; initial += 1) {
      AutomataLearningOptions options = getOptionsBuilder().initialPheromones(initial).build();
      TestDataSaver.DataSaverSubtest dataSaverSubtest =
          dataSaver.beginSubtest(options, input, Map.of("InitialPheromone", "" + initial));
      TestRunner.test(options, input, dataSaverSubtest, NR_COLONIES_MEDIUM).join();
    }
    dataSaver.close();
  }

  @Test
  void testDecay() throws IOException {
    TestDataSaver dataSaver = new TestDataSaver("DecayFactor");
    for (double decayFactor = 0; decayFactor < 1.01; decayFactor += .05) {
      AutomataLearningOptions options = getOptionsBuilder().decayFactor(decayFactor).build();
      TestDataSaver.DataSaverSubtest dataSaverSubtest =
          dataSaver.beginSubtest(
              options, input, Map.of("decayFactor", Util.formatDouble(decayFactor, 2)));
      TestRunner.test(options, input, dataSaverSubtest, NR_COLONIES_MEDIUM).join();
    }
    dataSaver.close();
  }

  @Test
  void testSpreadOfLearnFunctionFactor() throws IOException {
    TestDataSaver dataSaver = new TestDataSaver("SpreadOfLearnFunctionFactor");
    for (double spread = 0; spread < 1.01; spread += spread < .3 ? .02 : .05) {
      PheromoneFunction prevPheromoneFunction = getOptionsBuilder().pheromoneFunction();
      PheromoneFunction pheromoneFunction =
          new PheromoneFunction(
              prevPheromoneFunction.sigmoid(),
              spread,
              prevPheromoneFunction.minRemainingProbability());
      AutomataLearningOptions options =
          getOptionsBuilder().pheromoneFunction(pheromoneFunction).build();
      TestDataSaver.DataSaverSubtest dataSaverSubtest =
          dataSaver.beginSubtest(options, input, Map.of("spread", Util.formatDouble(spread, 2)));
      TestRunner.test(options, input, dataSaverSubtest, NR_COLONIES_MEDIUM).join();
    }
    dataSaver.close();
  }

  @Test
  void testMinRemainingProbability() throws IOException {
    TestDataSaver dataSaver = new TestDataSaver("MinRemainingProbability");
    for (double minRemainingProb = 0;
        minRemainingProb < 0.10000001;
        minRemainingProb += minRemainingProb < .05 ? .002 : .01) {
      PheromoneFunction prevPheromoneFunction = getOptionsBuilder().pheromoneFunction();
      PheromoneFunction pheromoneFunction =
          new PheromoneFunction(
              prevPheromoneFunction.sigmoid(), prevPheromoneFunction.spread(), minRemainingProb);
      AutomataLearningOptions options =
          getOptionsBuilder().pheromoneFunction(pheromoneFunction).build();
      TestDataSaver.DataSaverSubtest dataSaverSubtest =
          dataSaver.beginSubtest(
              options, input, Map.of("minRemainingProb", Util.formatDouble(minRemainingProb, 4)));
      TestRunner.test(options, input, dataSaverSubtest, NR_COLONIES_MEDIUM).join();
    }
    dataSaver.close();
  }
}
