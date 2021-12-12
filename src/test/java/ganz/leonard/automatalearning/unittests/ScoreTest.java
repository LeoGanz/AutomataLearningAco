package ganz.leonard.automatalearning.unittests;

import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.automata.general.DeterministicState;
import ganz.leonard.automatalearning.gui.optionsscreen.InputProvider;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import ganz.leonard.automatalearning.learning.AutomataLearningOptionsBuilder;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ScoreTest {

  private static final String INPUT_FILE =
      "ganz/leonard/automatalearning/input/AstarB_ScoreTest.txt";
  private static DeterministicFiniteAutomaton<Object> dfa;

  @BeforeAll
  static void setUp() {
    buildInefficientDfa();
  }

  private static void buildInefficientDfa() {
    // model a*b inefficiently
    DeterministicState<Object> fst = new DeterministicState<>(1, false);
    DeterministicState<Object> snd = new DeterministicState<>(2, true);
    DeterministicState<Object> as = new DeterministicState<>(3, false);
    fst.addTransitions(Map.of('a', as, 'b', snd));
    as.addTransitions(Map.of('a', as, 'b', snd));
    dfa = new DeterministicFiniteAutomaton<>(Set.of(fst, snd, as), fst);
  }

  @Test
  void testAutomatonAcceptance() throws URISyntaxException, IOException {
    Map<List<Object>, Boolean> input = getInput();
    input.forEach((word, inLang) -> Assertions.assertEquals(inLang, dfa.accepts(word)));
  }

  @Test
  void testFullScore() throws URISyntaxException, IOException {
    Map<List<Object>, Boolean> input = getInput();
    double expectedScore = 1;

    testScore(input, expectedScore);
  }

  @Test
  void testPartialScore() throws URISyntaxException, IOException {
    Map<List<Object>, Boolean> input = getInput();
    long nrCorrect = input.size();
    input.put(List.of('a', 'a'), true);
    double expectedScore = (double) nrCorrect / input.size();

    testScore(input, expectedScore);
  }

  void testScore(Map<List<Object>, Boolean> input, double expectedScore) {
    AutomataLearning<Object> al =
        new AutomataLearning<>(AutomataLearningOptionsBuilder.builder().build(), input);

    double actualScore = al.calcMatchingInputScore(dfa);
    Assertions.assertEquals(expectedScore, actualScore);
  }

  private Map<List<Object>, Boolean> getInput() throws IOException, URISyntaxException {
    return InputProvider.readFromFile(Paths.get(ClassLoader.getSystemResource(INPUT_FILE).toURI()));
  }
}
