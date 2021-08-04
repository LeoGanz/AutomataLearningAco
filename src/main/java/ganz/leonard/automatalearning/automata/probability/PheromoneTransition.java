package ganz.leonard.automatalearning.automata.probability;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Rules for a transition between two states. Defines probability of transiting for every letter of
 * the alphabet.
 *
 * @param <T> type used as alphabet
 */
public class PheromoneTransition<T> {
  private static final double INIT_PHEROMONE = 1;
  private final Map<T, Double> pheromones;

  public PheromoneTransition() {
    this.pheromones = new HashMap<>();
    // used for lazy init of pheromones; tracks decays
    pheromones.put(null, INIT_PHEROMONE);
  }

  public double getRawProbabilityFor(T letter) {
    Objects.requireNonNull(letter);
    if (!pheromones.containsKey(letter)) {
      pheromones.put(letter, pheromones.get(null));
    }
    // calculate probability as some function of pheromone levels
    return 0.5;
  }

  public void positivePheromoneFeedback(T letter) {
    Objects.requireNonNull(letter);
    // update pheromones according to some formula
  }

  public void decay() {
    // negative feedback / pheromone decay for all transitions
  }
}
