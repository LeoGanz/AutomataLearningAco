package ganz.leonard.al_ants.automata;

import java.util.Collection;

public class Automaton<T> {
  private State<T> start;
  private State<T> current;
  private Collection<State<T>> allStates;

  public Automaton(State<T> current) {
    this.current = current;
  }
}
