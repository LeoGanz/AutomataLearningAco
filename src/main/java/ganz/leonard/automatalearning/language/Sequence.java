package ganz.leonard.automatalearning.language;

import java.util.List;

public record Sequence<T>(Expression<T> fst, Expression<T> snd)  implements Expression<T> {

  @Override
  public String toString() {
    return "(" + fst + snd + ")";
  }

  @Override
  public List<T> accept(ExpressionVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
