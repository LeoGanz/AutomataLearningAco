package ganz.leonard.al_ants.language;

import java.util.List;

public record Repetition<T> (Expression<T> expr) implements Expression<T> {

  @Override
  public String toString() {
    return "(" + expr + ")*";
  }

  @Override
  public List<T> accept(ExpressionVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
