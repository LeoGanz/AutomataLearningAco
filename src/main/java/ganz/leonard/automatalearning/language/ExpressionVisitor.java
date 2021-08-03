package ganz.leonard.automatalearning.language;

import java.util.List;

public interface ExpressionVisitor<T> {
  List<T> visit(Sequence<T> seq);

  List<T> visit(Alternative<T> seq);

  List<T> visit(Repetition<T> seq);

  List<T> visit(Leaf<T> seq);
}
