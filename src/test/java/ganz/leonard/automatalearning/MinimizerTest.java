package ganz.leonard.automatalearning;

import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.automata.general.DeterministicState;
import ganz.leonard.automatalearning.automata.tools.Minimizer;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MinimizerTest {

  private static DeterministicFiniteAutomaton<Character> dfa;
  private static DeterministicFiniteAutomaton<Character> minimalDfa;

  @BeforeAll
  static void setUp() {
    buildInefficientDfa();
    buildMinimalDfa();
  }

  private static void buildInefficientDfa() {
    // model a*b inefficiently
    DeterministicState<Character> fst = new DeterministicState<>(1, false);
    DeterministicState<Character> snd = new DeterministicState<>(2, true);
    DeterministicState<Character> uselessAs = new DeterministicState<>(3, false);
    fst.addTransitions(Map.of('a', uselessAs, 'b', snd));
    uselessAs.addTransitions(Map.of('a', uselessAs, 'b', snd));
    dfa = new DeterministicFiniteAutomaton<>(Set.of(fst, snd, uselessAs), fst);
  }

  private static void buildMinimalDfa() {
    // model minimal automaton for a*b
    DeterministicState<Character> fst = new DeterministicState<>(0, false);
    DeterministicState<Character> snd = new DeterministicState<>(1, true);
    fst.addTransitions(Map.of('a', fst, 'b', snd));
    minimalDfa = new DeterministicFiniteAutomaton<>(Set.of(fst, snd), fst);
  }

  @Test
  void testPythonMinimizer() throws IOException, InterruptedException {
    DeterministicFiniteAutomaton<Character> miniGenerated =
        new Minimizer<Character>().minimize(dfa);
    System.out.println(miniGenerated);
    Assertions.assertEquals(minimalDfa, miniGenerated);
  }
}
