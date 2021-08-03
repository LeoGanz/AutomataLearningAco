package ganz.leonard.automatalearning;

import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.automata.general.DeterministicState;
import java.util.Set;

public class DeterministicFiniteAutomatonTest {
  public static void main(String[] args) {
    // model a*b

    DeterministicState<Character> fst = new DeterministicState<>(false);
    DeterministicState<Character> snd = new DeterministicState<>(true);
    DeterministicFiniteAutomaton<Character>
        dfa = new DeterministicFiniteAutomaton<>(Set.of(fst, snd), fst);

    dfa.canHold();
  }
}
