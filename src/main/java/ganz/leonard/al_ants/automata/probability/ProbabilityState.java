package ganz.leonard.al_ants.automata.probability;

import ganz.leonard.al_ants.automata.general.BasicState;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * State of an automaton. Can be an accepting state or not.
 *
 * @param <T> type used as alphabet
 */
public class ProbabilityState<T> extends BasicState<ProbabilityState<T>, T> {
  private final Map<ProbabilityState<T>, PheromoneTransition<T>> outgoingTransitions;

  public ProbabilityState(boolean isAccepting) {
    super(isAccepting);
    outgoingTransitions = new HashMap<>();
  }

  public void initTransitionsTo(Collection<ProbabilityState<T>> states) {
    states.stream()
        .filter(state -> state != this)
        .filter(state -> !outgoingTransitions.containsKey(state))
        .forEach(state -> outgoingTransitions.put(state, new PheromoneTransition<>()));
  }

  @Override
  public ProbabilityState<T> transit(T letter) {
    Map<ProbabilityState<T>, Double> probabilities =
        ProbabilityUtil.normalizeProbabilities(
            outgoingTransitions.entrySet().stream()
                .collect(
                    Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getRawProbabilityFor(letter))));

    return ProbabilityUtil.sample(probabilities);
  }

  public PheromoneTransition<T> getTransitionTo(ProbabilityState<T> state) {
    return outgoingTransitions.get(state);
  }

  public Map<ProbabilityState<T>, PheromoneTransition<T>> getAllOutgoingTransitions() {
    return outgoingTransitions;
  }
}
