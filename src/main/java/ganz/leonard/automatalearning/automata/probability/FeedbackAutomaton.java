package ganz.leonard.automatalearning.automata.probability;

import ganz.leonard.automatalearning.automata.general.Automaton;
import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.learning.AutomataLearningOptions;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.util.Pair;

/**
 * An automaton that tracks its path of used {@link PheromoneTransition}s and provides means of
 * giving positive feedback to all the transitions of the path.
 *
 * <p>For a general description see {@link Automaton}.
 *
 * @param <T> type used as alphabet
 */
public class FeedbackAutomaton<T> extends Automaton<ProbabilityState<T>, T> {

  // cannot use maps as duplicates are possible
  private final List<Pair<PheromoneTransition<T>, T>> currentPath;
  private final ProbToDetConverter<T> converter;
  private final AutomataLearningOptions options;

  public FeedbackAutomaton(
      Collection<ProbabilityState<T>> allStates,
      ProbabilityState<T> start,
      AutomataLearningOptions options) {
    super(allStates, start);
    if (!allStates.stream().map(ProbabilityState::getOptions).allMatch(options::equals)) {
      throw new IllegalArgumentException("Options of states and automaton have to be the same");
    }
    this.options = options;
    currentPath = new LinkedList<>();
    converter = new ProbToDetConverter<>(this);
  }

  public static <T> FeedbackAutomaton<T> copyFeedbackAutomaton(FeedbackAutomaton<T> original) {
    Set<ProbabilityState<T>> newStates =
        original.getAllStates().values().stream()
            .map(old -> new ProbabilityState<T>(old.getId(), old.isAccepting(), original.options))
            .collect(Collectors.toSet());
    Optional<ProbabilityState<T>> newStart =
        newStates.stream()
            .filter(state -> state.getId() == original.getStartState().getId())
            .findFirst();
    if (newStart.isEmpty()) {
      throw new IllegalArgumentException(
          "Could not find start state with proper id in provided automaton");
    }
    FeedbackAutomaton<T> newAutomaton =
        new FeedbackAutomaton<>(newStates, newStart.get(), original.options);
    newStates.forEach(
        state ->
            state.initTransitionsLikeIn(
                original.getAllStates().get(state.getId()), newAutomaton.getAllStates()));
    return newAutomaton;
  }

  @Override
  public boolean takeLetter(T letter) {
    ProbabilityState<T> from = getCurrentState();
    if (!super.takeLetter(letter)) {
      return false;
    }
    ProbabilityState<T> to = getCurrentState();

    currentPath.add(new Pair<>(from.getTransitionTo(to), letter));
    return true;
  }

  @Override
  public void goToStart() {
    super.goToStart();
    currentPath.clear();
  }

  public void positiveFeedback() {
    currentPath.forEach(pair -> pair.getKey().positivePheromoneFeedback(pair.getValue()));
  }

  public void decay() {
    // negative feedback / pheromone decay for all transitions
    getAllStates().values().stream()
        .flatMap(state -> state.getOutgoingTransitions().values().stream())
        .forEach(PheromoneTransition::decay);
  }

  public Set<T> getKnownAlphabet() {
    return getAllStates().values().stream()
        .flatMap(state -> state.getUsedLetters().stream())
        .collect(Collectors.toSet());
  }

  public DeterministicFiniteAutomaton<T> buildMostLikelyDfa() {
    return converter.convert();
  }

  public AutomataLearningOptions getOptions() {
    return options;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    FeedbackAutomaton<?> that = (FeedbackAutomaton<?>) o;

    if (!Objects.equals(currentPath, that.currentPath)) {
      return false;
    }

    if (!Objects.equals(options, that.options)) {
      return false;
    }

    if (getAllStates() != null && that.getAllStates() != null) {
      return getAllStates().values().stream()
          .allMatch(
              s ->
                  that.getAllStates().get(s.getId()) != null
                      && Objects.equals(
                          s.getOutgoingTransitions(),
                          that.getAllStates().get(s.getId()).getOutgoingTransitions()));
    }
    return getAllStates() == null && that.getAllStates() == null;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (options != null ? options.hashCode() : 0);
    result = 31 * result + (currentPath != null ? currentPath.hashCode() : 0);
    if (getAllStates() != null) {
      for (ProbabilityState<T> s : getAllStates().values()) {
        result += s.getOutgoingTransitions() != null ? s.getOutgoingTransitions().hashCode() : 0;
      }
    }
    return result;
  }
}
