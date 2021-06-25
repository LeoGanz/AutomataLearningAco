package ganz.leonard.al_ants.language;

public record Leaf<T>(T elem) implements Expression<T> {
  @Override
  public String toString() {
    return elem.toString();
  }
}
