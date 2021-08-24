package ganz.leonard.automatalearning.automata.probability;

import ganz.leonard.automatalearning.automata.general.BasicState;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * State of an automaton. Can be an accepting state or not.
 *
 * @param <T> type used as alphabet
 */
public class ProbabilityState<T> extends BasicState<ProbabilityState<T>, T> {
  private final Map<ProbabilityState<T>, PheromoneTransition<T>> outgoingTransitions;

  public ProbabilityState(int id, boolean isAccepting) {
    super(id, isAccepting);
    outgoingTransitions = new HashMap<>();
  }

  public void addTransitionsTo(Collection<ProbabilityState<T>> states) {
    states.stream()
        .filter(state -> state != this) // allow?
        .filter(state -> !outgoingTransitions.containsKey(state))
        .forEach(state -> outgoingTransitions.put(state, new PheromoneTransition<>()));
  }

  public void initTransitionsLikeIn(
      ProbabilityState<T> original, Map<Integer, ProbabilityState<T>> withTheseStates) {
    outgoingTransitions.clear();
    original.outgoingTransitions.forEach(
        (origTarget, origTrans) ->
            outgoingTransitions.put(
                withTheseStates.get(origTarget.getId()), new PheromoneTransition<>(origTrans)));
  }

  @Override
  public ProbabilityState<T> transit(T letter) {
    Map<ProbabilityState<T>, Double> probabilities = getNormalizedTransitionProbabilities(letter);
    return ProbabilityUtil.sample(probabilities);
  }

  public Map<ProbabilityState<T>, Double> getNormalizedTransitionProbabilities(T letter) {
    return ProbabilityUtil.normalizeProbabilities(
        outgoingTransitions.entrySet().stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey, entry -> entry.getValue().getRawProbabilityFor(letter))));
  }

  public Set<T> getUsedLetters() {
    return outgoingTransitions.values().stream()
        .flatMap(transition -> transition.getKnownLetters().stream())
        // .distinct() already distinct through set
        .collect(Collectors.toSet());
  }

  public PheromoneTransition<T> getTransitionTo(ProbabilityState<T> state) {
    return outgoingTransitions.get(state);
  }

  public Map<ProbabilityState<T>, PheromoneTransition<T>> getOutgoingTransitions() {
    return outgoingTransitions;
  }
}
