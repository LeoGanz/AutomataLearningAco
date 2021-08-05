package ganz.leonard.automatalearning.automata.probability;

import ganz.leonard.automatalearning.automata.general.Automaton;
import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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

  public FeedbackAutomaton(Collection<ProbabilityState<T>> allStates, ProbabilityState<T> start) {
    super(allStates, start);
    currentPath = new LinkedList<>();
    converter = new ProbToDetConverter<>(this);
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

  public DeterministicFiniteAutomaton<T> buildMostLikelyDfa() {
    return converter.convert();
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
    result = 31 * result + (currentPath != null ? currentPath.hashCode() : 0);
    if (getAllStates() != null) {
      for (ProbabilityState<T> s : getAllStates().values()) {
        result += s.getOutgoingTransitions() != null ? s.getOutgoingTransitions().hashCode() : 0;
      }
    }
    return result;
  }
}
