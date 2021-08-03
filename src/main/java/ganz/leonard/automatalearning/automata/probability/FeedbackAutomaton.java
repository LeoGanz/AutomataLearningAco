package ganz.leonard.automatalearning.automata.probability;

import ganz.leonard.automatalearning.automata.general.Automaton;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
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

  public FeedbackAutomaton(Collection<ProbabilityState<T>> allStates, ProbabilityState<T> start) {
    super(allStates, start);
    currentPath = new LinkedList<>();
  }

  @Override
  public void takeLetter(T letter) {
    ProbabilityState<T> from = getCurrentState();
    super.takeLetter(letter);
    ProbabilityState<T> to = getCurrentState();

    currentPath.add(new Pair<>(from.getTransitionTo(to), letter));
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
    getAllStates().stream()
        .flatMap(state -> state.getAllOutgoingTransitions().values().stream())
        .forEach(PheromoneTransition::decay);
  }
}
