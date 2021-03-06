package ganz.leonard.automatalearning.automata.probability;

import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.automata.general.DeterministicState;
import ganz.leonard.automatalearning.util.PairStream;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public class ProbToDetConverter<T> {
  private static final double DEF_MIN_PROBABILITY = 0.5;
  private final FeedbackAutomaton<T> automaton;
  private DeterministicFiniteAutomaton<T> dfa;
  private double minProbability = DEF_MIN_PROBABILITY;

  public ProbToDetConverter(FeedbackAutomaton<T> automaton) {
    this.automaton = automaton;
  }

  private void constructTransitionsFor(ProbabilityState<T> probState) {
    probState.getUsedLetters().forEach(letter -> constructSingleTransition(probState, letter));
  }

  private void constructSingleTransition(ProbabilityState<T> probState, T letter) {
    Map<ProbabilityState<T>, Double> probabilities =
        probState.collectTransitionProbabilities(letter);
    Optional<Map.Entry<ProbabilityState<T>, Double>> successor =
        probabilities.entrySet().stream().max(Comparator.comparingDouble(Map.Entry::getValue));
    if (successor.isPresent()) {
      if (successor.get().getValue() > minProbability) {
        DeterministicState<T> successorDfa =
            dfa.getAllStates().get(successor.get().getKey().getId());
        dfa.getAllStates().get(probState.getId()).addTransitions(Map.of(letter, successorDfa));
      }
    }
  }

  /**
   * Construct an {@link DeterministicFiniteAutomaton} with the same state structure and choose the
   * most likely transitions.
   *
   * @return the newly constructed automaton
   */
  public DeterministicFiniteAutomaton<T> convert() {
    buildAutomatonSkeleton();
    automaton.getAllStates().values().forEach(this::constructTransitionsFor);
    return dfa;
  }

  private void buildAutomatonSkeleton() {
    Map<Integer, DeterministicState<T>> newStates =
        PairStream.from(automaton.getAllStates())
            .mapValue(
                probState -> new DeterministicState<T>(probState.getId(), probState.isAccepting()))
            .toMap();
    DeterministicState<T> start = newStates.get(automaton.getStartState().getId());
    dfa = new DeterministicFiniteAutomaton<>(newStates, start);
  }

  public double getMinProbability() {
    return minProbability;
  }

  public void setMinProbability(double minProbability) {
    this.minProbability = minProbability;
  }
}
