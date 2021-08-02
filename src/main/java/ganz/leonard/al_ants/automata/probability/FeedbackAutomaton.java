package ganz.leonard.al_ants.automata.probability;

import ganz.leonard.al_ants.automata.general.Automaton;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * An automaton that tracks its path of used {@link PheromoneTransition}s and provides means of
 * giving positive feedback to all the transitions of the path.
 *
 * <p>For a general description see {@link Automaton}.
 *
 * @param <T> type used as alphabet
 */
public class FeedbackAutomaton<T> extends Automaton<ProbabilityState<T>, T> {

  private final List<PheromoneTransition<T>> currentPath;

  public FeedbackAutomaton(Collection<ProbabilityState<T>> allStates, ProbabilityState<T> start) {
    super(allStates, start);
    currentPath = new LinkedList<>();
  }

  @Override
  public void takeLetter(T letter) {
    ProbabilityState<T> from = getCurrentState();
    super.takeLetter(letter);
    ProbabilityState<T> to = getCurrentState();

    currentPath.add(from.getTransitionTo(to));
  }

  @Override
  public void goToStart() {
    super.goToStart();
    currentPath.clear();
  }

  public void positiveFeedback() {
    currentPath.forEach(PheromoneTransition::positivePheromoneFeedback);
  }
}
