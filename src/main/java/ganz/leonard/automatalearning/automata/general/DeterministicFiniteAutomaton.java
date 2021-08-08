package ganz.leonard.automatalearning.automata.general;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DeterministicFiniteAutomaton<T> extends Automaton<DeterministicState<T>, T> {
  public DeterministicFiniteAutomaton(
      Collection<DeterministicState<T>> allStates, DeterministicState<T> start) {
    super(allStates, start);
  }

  public DeterministicFiniteAutomaton(
      Map<Integer, DeterministicState<T>> allStatesWithId, DeterministicState<T> start) {
    super(allStatesWithId, start);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    DeterministicFiniteAutomaton<?> that = (DeterministicFiniteAutomaton<?>) o;
    if (getAllStates() != null && that.getAllStates() != null) {
      return getAllStates().values().stream()
          .allMatch(
              s ->
                  that.getAllStates().get(s.getId()) != null
                      && Objects.equals(
                          s.getOutgoingTransitions(),
                          that.getAllStates().get(s.getId()).getOutgoingTransitions()));
    }
    return getAllStates() == null && that.getAllStates() == null;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result *= 31;
    if (getAllStates() != null) {
      for (DeterministicState<T> s : getAllStates().values()) {
        result += s.getOutgoingTransitions() != null ? s.getOutgoingTransitions().hashCode() : 0;
      }
    }
    return result;
  }

  @Override
  public String toString() {
    Map<DeterministicState<T>, Map<T, DeterministicState<T>>> trans =
        getAllStates().values().stream()
            .collect(
                Collectors.toMap(Function.identity(), DeterministicState::getOutgoingTransitions));
    return "DFA(states: " + getAllStates().values() + ", transitions: " + trans + ")";
  }
}
