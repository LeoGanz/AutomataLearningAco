package ganz.leonard.automatalearning.learning;

import ganz.leonard.automatalearning.automata.probability.FeedbackAutomaton;
import ganz.leonard.automatalearning.automata.probability.PheromoneTransition;
import ganz.leonard.automatalearning.automata.probability.ProbabilityState;
import java.util.List;
import javafx.util.Pair;

public class TalkingAnt<T> extends Ant<T> {
  private static int nextId = 0;
  private final int id;
  private final List<T> word;
  private final FeedbackAutomaton<T> automaton;

  public TalkingAnt(List<T> word, boolean inLanguage, FeedbackAutomaton<T> automaton) {
    super(word, inLanguage, automaton);
    this.word = word;
    this.automaton = automaton;
    id = nextId++;
  }

  @Override
  public void buildSolution() {
    super.buildSolution();
    StringBuilder pathString = new StringBuilder("Ant ").append(id).append(": 0");
    double totalProb = 1;
    List<Pair<PheromoneTransition<T>, T>> path = getSolutionPath();
    for (Pair<PheromoneTransition<T>, T> pheromoneTransitionTPair : path) {
      Pair<ProbabilityState<T>, ProbabilityState<T>> endpoints =
          automaton.getEndpointsOf(pheromoneTransitionTPair.getKey());
      pathString.append("->").append(endpoints.getValue().getId());
      totalProb *=
          endpoints
              .getKey()
              .collectTransitionProbabilities(pheromoneTransitionTPair.getValue())
              .get(endpoints.getValue());
    }
    double defaultTotalProb = Math.pow(1.0 / (automaton.getAllStates().size() - 1), path.size());
    System.out.println("Ant " + id + ": " + word);
    System.out.println(pathString);
    System.out.println("Ant " + id + " successful: " + (isSolutionCorrect()));
    System.out.println(
        "Ant " + id + " totalProb ; defaultTotalProb: " + totalProb + " ; " + defaultTotalProb);
  }
}
