package ganz.leonard.automatalearning.learning;

import java.util.List;

public record InputWord<T>(List<T> word, boolean inLang) {
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(inLang ? "+" : "-");
    word.forEach(sb::append);
    return sb.toString();
  }
}
