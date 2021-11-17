package ganz.leonard.automatalearning.unittests;

import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.automata.general.DeterministicState;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeterministicFiniteAutomatonTest {

  private DeterministicState<Character> fst;
  private DeterministicState<Character> snd;
  private DeterministicFiniteAutomaton<Character> dfa;

  @BeforeEach
  void setup() {
    // model a*b

    fst = new DeterministicState<>(1, false);
    snd = new DeterministicState<>(2, true);
    fst.addTransitions(Map.of('a', fst, 'b', snd));
    dfa = new DeterministicFiniteAutomaton<>(Set.of(fst, snd), fst);
  }

  @Test
  void basicDfaBehavior() {
    Assertions.assertEquals(fst, dfa.getCurrentState());
    Assertions.assertFalse(dfa.canHold());
    Assertions.assertTrue(dfa.takeLetter('a'));
    Assertions.assertEquals(fst, dfa.getCurrentState());
    Assertions.assertFalse(dfa.canHold());
    Assertions.assertTrue(dfa.takeLetter('a'));
    Assertions.assertEquals(fst, dfa.getCurrentState());
    Assertions.assertFalse(dfa.canHold());
    Assertions.assertTrue(dfa.takeLetter('b'));
    Assertions.assertEquals(snd, dfa.getCurrentState());
    Assertions.assertTrue(dfa.canHold());
    Assertions.assertFalse(dfa.takeLetter('b'));
    Assertions.assertEquals(snd, dfa.getCurrentState());
    Assertions.assertTrue(dfa.canHold());
    Assertions.assertFalse(dfa.takeLetter('a'));
    Assertions.assertEquals(snd, dfa.getCurrentState());
    Assertions.assertTrue(dfa.canHold());

    dfa.goToStart();
    Assertions.assertEquals(fst, dfa.getCurrentState());
    Assertions.assertFalse(dfa.canHold());
    Assertions.assertTrue(dfa.takeLetter('b'));
    Assertions.assertEquals(snd, dfa.getCurrentState());
    Assertions.assertTrue(dfa.canHold());
  }
}
