package ganz.leonard.automatalearning.automata.general;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Mutable State Machine. An automaton can be traversed by processing letters from a specified
 * alphabet. The letter will be processed by the current state of the automaton according to the
 * transition rules of that state. An automaton can hold if the current state is accepting.
 *
 * @param <S> type used for the states of the automaton
 * @param <T> type used as alphabet
 * @see #takeLetter(T) Method to step through the automaton
 */
public class Automaton<S extends State<S, T>, T> {
  private final Map<Integer, S> allStates;
  private final S start;
  private S current;

  public Automaton(Collection<S> allStates, S start) {
    this(allStates.stream().collect(Collectors.toMap(State::getId, Function.identity())), start);
  }

  public Automaton(Map<Integer, S> allStatesWithId, S start) {
    this.allStates = allStatesWithId;
    this.start = start;
    this.current = start;
  }

  public boolean takeLetter(T letter) {
    S next = current.transit(letter);
    if (next != null) {
      current = next;
      return true;
    }
    return false;
  }

  public void goToStart() {
    current = start;
  }

  public S getStartState() {
    return start;
  }

  public S getCurrentState() {
    return current;
  }

  public Map<Integer, S> getAllStates() {
    return allStates;
  }

  public boolean canHold() {
    return current.isAccepting();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Automaton<?, ?> automaton = (Automaton<?, ?>) o;

    if (!Objects.equals(allStates, automaton.allStates)) {
      return false;
    }
    return Objects.equals(start, automaton.start);
  }

  @Override
  public int hashCode() {
    int result = allStates != null ? allStates.hashCode() : 0;
    result = 31 * result + (start != null ? start.hashCode() : 0);
    return result;
  }
}
