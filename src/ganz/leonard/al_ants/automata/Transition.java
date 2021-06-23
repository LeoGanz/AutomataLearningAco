package ganz.leonard.al_ants.automata;

import java.util.Map;

/**
 * Rules for a transition between two states. Defines probability of transiting for every letter of
 * the alphabet.
 *
 * @param <T> type used as alphabet
 */
public class Transition<T> {
  private Map<T, Double> pheromones;

  public Transition(Map<T, Double> rules) {
    this.pheromones = rules;
  }

  double getRawProbabilityFor(T letter) {
    // calculate Prob
    return 0.5;
  }
}
