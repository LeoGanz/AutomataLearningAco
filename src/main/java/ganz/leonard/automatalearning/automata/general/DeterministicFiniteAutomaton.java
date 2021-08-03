package ganz.leonard.automatalearning.automata.general;

import java.util.Collection;

public class DeterministicFiniteAutomaton<T> extends Automaton<DeterministicState<T>, T> {
  public DeterministicFiniteAutomaton(
      Collection<DeterministicState<T>> allStates, DeterministicState<T> start) {
    super(allStates, start);
  }
}
