package ganz.leonard.automatalearning.paramtests;

import static ganz.leonard.automatalearning.paramtests.TestingUtil.getOptionsBuilder;
import static ganz.leonard.automatalearning.paramtests.TestingUtil.loadInput;
import static ganz.leonard.automatalearning.paramtests.TestingUtil.runTest;

import ganz.leonard.automatalearning.automata.probability.PheromoneFunction;
import ganz.leonard.automatalearning.learning.AutomataLearningOptions;
import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;

public class TestDifferentLanguages {

  private static final String FILE_A_STAR_B = "Test-AstarB_reference.txt";
  private static final String FILE_A_PLUS_B = "Test-AplusB.txt";
  private static final String FILE_ABC_STAR = "Test-(abc)Star.txt";
  private static final String FILE_AB_STAR_C = "Test-ABstarC.txt";
  private static final String FILE_A_STAR_B_STAR = "Test-AstarBstar.txt";
  private static final String FILE_EVEN_A = "Test-EvenAs.txt";
  private static final String FILE_A_OR_ABC_STAR = "Test-aOr(abc)Star.txt";

  @Test
  void testAstarB() throws IOException, URISyntaxException {
    runTest(loadInput(FILE_A_STAR_B), "Lang-AstarB", getOptionsBuilder().build());
  }

  @Test
  void testAplusB() throws IOException, URISyntaxException {
    runTest(loadInput(FILE_A_PLUS_B), "Lang-AplusB", getOptionsBuilder().build());
  }

  @Test
  void testEvenAs() throws IOException, URISyntaxException {
    runTest(
        loadInput(FILE_EVEN_A),
        "Lang-EvenAs",
        getOptionsBuilder().acceptingStates(3).notAcceptingStates(3).build());
  }

  @Test
  void testABstarC() throws IOException, URISyntaxException {
    runTest(
        loadInput(FILE_AB_STAR_C),
        "Lang-ABstarC",
        getOptionsBuilder().acceptingStates(2).notAcceptingStates(2).build());
  }

  @Test
  void testAstarBstar() throws IOException, URISyntaxException {
    runTest(loadInput(FILE_A_STAR_B_STAR), "Lang-AstarBstar", getOptionsBuilder().build());
  }

  @Test
  void testAOrABC_star() throws IOException, URISyntaxException {
    runTest(
        loadInput(FILE_A_OR_ABC_STAR),
        "Lang-aOr(abc)Star",
        getOptionsBuilder().acceptingStates(2).notAcceptingStates(2).build());
  }

  @Test
  void testABC_star() throws IOException, URISyntaxException {
    runTest(
        loadInput(FILE_ABC_STAR),
        "Lang-ABC_star",
        getOptionsBuilder()
            .acceptingStates(1)
            .notAcceptingStates(2)
            .pheromoneFunction(
                new PheromoneFunction(AutomataLearningOptions.DEF_SIGMOID, 0.08, 0.016))
            .build(),
        true);
  }
}
