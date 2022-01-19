package ganz.leonard.automatalearning.learning;

import ganz.leonard.automatalearning.automata.probability.FeedbackAutomaton;
import ganz.leonard.automatalearning.automata.probability.PheromoneTransition;
import java.util.List;
import javafx.util.Pair;

public class Ant<T> {
  private final boolean inLanguage;
  private final FeedbackAutomaton<T> automaton;
  private final List<T> word;

  private List<Pair<PheromoneTransition<T>, T>> solutionPath;
  private boolean solutionCorrect;

  public Ant(List<T> word, boolean inLanguage, FeedbackAutomaton<T> automaton) {
    this.word = word;
    this.inLanguage = inLanguage;
    this.automaton = automaton;
  }

  public void buildSolution() {
    automaton.goToStart();
    boolean stepPossible = true;
    for (T letter : word) {
      stepPossible = automaton.takeLetter(letter);
      // possibility to add online step-by-step pheromone update
      if (!stepPossible) {
        System.out.println("An ant could not proceed");
        break;
      }
    }
    solutionPath = automaton.getCurrentPath();
    solutionCorrect = stepPossible && (automaton.canHold() == inLanguage);
  }

  public void distributePheromones() {
    if (solutionPath == null) {
      throw new IllegalStateException(
          "Ant has to construct solution before distributing pheromones");
    }
    // apply online delayed pheromone update
    automaton.feedback(solutionPath, solutionCorrect);
  }

  public List<Pair<PheromoneTransition<T>, T>> getSolutionPath() {
    if (solutionPath == null) {
      throw new IllegalStateException("Ant has to construct solution before calling this method");
    }
    return solutionPath;
  }

  public boolean isSolutionCorrect() {
    if (solutionPath == null) {
      throw new IllegalStateException("Ant has to construct solution before calling this method");
    }
    return solutionCorrect;
  }
}
