package ganz.leonard.automatalearning.automata.general;

/**
 * Definition of what functionality a state of an {@link Automaton} has to provide at the minimum.
 *
 * @param <S> Type of the State-Subclass (Simulation of self-types)
 * @param <T> type used as alphabet
 */
// Simulation of self-types inspired by https://www.sitepoint.com/self-types-with-javas-generics/
public interface State<S extends State<S, T>, T> {

  boolean isAccepting();

  /**
   * Calculates the state to visit next.
   *
   * @param letter some letter of the alphabet that defines which transition will be taken
   * @return the successor state
   */
  S transit(T letter);
}
