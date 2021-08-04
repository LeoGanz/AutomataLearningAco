package ganz.leonard.automatalearning.automata.general;

import java.util.Collection;

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
  private final Collection<S> allStates;
  private final S start;
  private S current;

  public Automaton(Collection<S> allStates, S start) {
    this.allStates = allStates;
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

  public Collection<S> getAllStates() {
    return allStates;
  }

  public boolean canHold() {
    return current.isAccepting();
  }
}
