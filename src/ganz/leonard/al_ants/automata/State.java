package ganz.leonard.al_ants.automata;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class State<T> {
  private final boolean isAccepting;
  private final Map<Transition<T>, State<T>> outgoingTransitions;

  public State(boolean isAccepting) {
    this.isAccepting = isAccepting;
    outgoingTransitions = new HashMap<>();
  }

  public boolean isAccepting() {
    return isAccepting;
  }

  public State<T> transit(T letter) {
    Map<Transition<T>, Double> probabilities =
        ProbabilityUtil.normalizeProbabilities(
            outgoingTransitions.keySet().stream()
                .collect(
                    Collectors.toMap(
                        transition -> transition,
                        transition -> transition.getRawProbabilityFor(letter))));

    Transition<T> chosenTransition = ProbabilityUtil.sample(probabilities);

    return outgoingTransitions.get(chosenTransition);
  }
}
