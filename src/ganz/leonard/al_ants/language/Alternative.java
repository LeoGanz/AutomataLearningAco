package ganz.leonard.al_ants.language;

public record Alternative<T>(Expression<T> fst, Expression<T> snd) implements Expression<T> {
  @Override
  public String toString() {
    return "(" + fst + "|" + snd + ")";
  }
}
