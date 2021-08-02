package ganz.leonard.al_ants.automata.probability;

import java.util.HashMap;
import java.util.Map;

/**
 * Rules for a transition between two states. Defines probability of transiting for every letter of
 * the alphabet.
 *
 * @param <T> type used as alphabet
 */
public class PheromoneTransition<T> {
  private static final double DEFAULT_PHEROMONE = 1;
  private final Map<T, Double> pheromones;

  public PheromoneTransition() {
    this.pheromones = new HashMap<>();
  }

  public double getRawProbabilityFor(T letter) {
    if (!pheromones.containsKey(letter)) {
      pheromones.put(letter, DEFAULT_PHEROMONE);
    }
    // calculate probability as some function of pheromone levels
    return 0.5;
  }

  public void positivePheromoneFeedback() {
    // update pheromones according to some formula
  }
}
