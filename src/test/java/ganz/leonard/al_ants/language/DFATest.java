package ganz.leonard.al_ants.language;

import ganz.leonard.al_ants.automata.general.DFA;
import ganz.leonard.al_ants.automata.general.DeterministicState;
import java.util.Set;

public class DFATest {
  public static void main(String[] args) {
    // model a*b

    DeterministicState<Character> fst = new DeterministicState<>(false);
    DeterministicState<Character> snd = new DeterministicState<>(true);
    DFA<Character> aStarB = new DFA<>(Set.of(fst, snd), fst);

    aStarB.canHold();
  }
}
