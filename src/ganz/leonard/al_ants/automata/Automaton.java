package ganz.leonard.al_ants.automata;

import java.util.Collection;

/**
 * Mutable State Machine. While the States of a Automaton are immutable, this type may be changed by
 * invoking Methods.
 *
 * @param <T> type used as alphabet
 * @see #takeLetter(T) Method to step through the automaton
 */
public class Automaton<T> {

  private final Collection<State<T>> allStates;
  private final State<T> start;
  private State<T> current;

  public Automaton(Collection<State<T>> allStates, State<T> start) {
    this.allStates = allStates;
    this.start = start;
    this.current = start;
  }

  public void takeLetter(T letter) {
    current = current.transit(letter);
  }

  public State<T> getStartState() {
    return start;
  }

  public State<T> getCurrentState() {
    return current;
  }

  public Collection<State<T>> getAllStates() {
    return allStates;
  }

  public boolean canHold() {
    return current.isAccepting();
  }
}
