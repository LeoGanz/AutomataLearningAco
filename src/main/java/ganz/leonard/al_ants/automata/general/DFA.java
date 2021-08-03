package ganz.leonard.al_ants.automata.general;

import java.util.Collection;

public class DFA<T> extends Automaton<DeterministicState<T>, T>{
  public DFA(Collection<DeterministicState<T>> allStates, DeterministicState<T> start) {
    super(allStates, start);
  }
}
