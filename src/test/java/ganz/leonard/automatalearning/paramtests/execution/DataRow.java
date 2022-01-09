package ganz.leonard.automatalearning.paramtests.execution;

import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.automata.tools.DfaToRegexConverter;
import ganz.leonard.automatalearning.learning.IntermediateResult;
import java.io.IOException;

// can't use records unfortunately as csveed library isn't compatible yet
public class DataRow<T> {
  private int colony;
  private double score;
  private DeterministicFiniteAutomaton<T> dfa;
  private String regex;

  public DataRow(int colony, double score, DeterministicFiniteAutomaton<T> dfa, String regex) {
    this.colony = colony;
    this.score = score;
    this.dfa = dfa;
    this.regex = regex;
  }

  public static <T> DataRow<T> fromIntermediateResult(IntermediateResult<T> data, int colony, boolean ignoreRegex) {
    String regex = "ignored";
    if (!ignoreRegex){
      try {
        regex = DfaToRegexConverter.convert(data.automaton());
      } catch (IOException | InterruptedException e) {
        regex = "Transformation failed";
      }
    }
    return new DataRow<>(colony, data.score(), data.automaton(), regex);
  }

  public int getColony() {
    return colony;
  }

  public void setColony(int colony) {
    this.colony = colony;
  }

  public double getScore() {
    return score;
  }

  public void setScore(double score) {
    this.score = score;
  }

  public DeterministicFiniteAutomaton<T> getDfa() {
    return dfa;
  }

  public void setDfa(DeterministicFiniteAutomaton<T> dfa) {
    this.dfa = dfa;
  }

  public String getRegex() {
    return regex;
  }

  public void setRegex(String regex) {
    this.regex = regex;
  }
}
