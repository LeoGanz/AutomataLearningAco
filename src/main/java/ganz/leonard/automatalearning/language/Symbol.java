package ganz.leonard.automatalearning.language;

import java.util.List;

public record Symbol<T>(T elem) implements Expression<T> {

  @Override
  public String toString() {
    return elem.toString();
  }

  @Override
  public List<T> accept(ExpressionVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
