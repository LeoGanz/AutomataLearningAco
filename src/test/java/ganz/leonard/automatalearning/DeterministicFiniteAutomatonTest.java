package ganz.leonard.automatalearning;

import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.automata.general.DeterministicState;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DeterministicFiniteAutomatonTest {

  @Test
  void testBasicDfa() {
    // model a*b

    DeterministicState<Character> fst = new DeterministicState<>(false);
    DeterministicState<Character> snd = new DeterministicState<>(true);
    fst.initTransitions(Map.of('a', fst, 'b', snd));
    DeterministicFiniteAutomaton<Character> dfa =
        new DeterministicFiniteAutomaton<>(Set.of(fst, snd), fst);

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
