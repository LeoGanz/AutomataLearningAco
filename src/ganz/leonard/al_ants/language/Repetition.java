package ganz.leonard.al_ants.language;

public record Repetition<T> (Expression<T> expr) implements Expression<T> {
  @Override
  public String toString() {
    return "(" + expr + ")*";
  }
}
