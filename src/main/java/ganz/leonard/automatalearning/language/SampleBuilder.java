package ganz.leonard.automatalearning.language;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SampleBuilder<T> implements ExpressionVisitor<T> {

  public static final int DEFAULT_MIN_REPETITIONS = 0;
  public static final int DEFAULT_MAX_REPETITIONS = 10;
  private final RepetitionSamplingMode mode;
  private final int minRepetitions;
  private final int maxRepetitions;

  private SampleBuilder(RepetitionSamplingMode mode, int minRepetitions, int maxRepetitions) {
    this.mode = mode;
    this.minRepetitions = minRepetitions;
    this.maxRepetitions = maxRepetitions;
  }

  public static <T> SampleBuilder<T> createExpRepSampleBuilder() {
    return new SampleBuilder<>(RepetitionSamplingMode.EXPONENTIAL, -1, -1);
  }

  public static <T> SampleBuilder<T> createLinearRepSampleBuilder() {
    return createLinearRepSampleBuilder(DEFAULT_MIN_REPETITIONS, DEFAULT_MAX_REPETITIONS);
  }

  public static <T> SampleBuilder<T> createLinearRepSampleBuilder(
      int minRepetitions, int maxRepetitions) {
    return new SampleBuilder<>(RepetitionSamplingMode.LINEAR, minRepetitions, maxRepetitions);
  }

  @Override
  public List<T> visit(Sequence<T> seq) {
    List<T> result = seq.fst().accept(this);
    result.addAll(seq.snd().accept(this));
    return result;
  }

  @Override
  public List<T> visit(Alternative<T> alt) {
    if (Math.random() < 0.5) {
      return alt.fst().accept(this);
    } else {
      return alt.snd().accept(this);
    }
  }

  @Override
  public List<T> visit(Repetition<T> rep) {
    int repetitions;
    switch (mode) {
      case LINEAR ->
          repetitions = RngUtil.sampleLinearDist(minRepetitions, maxRepetitions + 1);
      case EXPONENTIAL -> repetitions = RngUtil.sampleExpDist();
      default -> throw new IllegalStateException("Unknown mode to calculate repetitions");
    }

    List<T> inner = rep.expr().accept(this);
    return Collections.nCopies(repetitions, inner).stream()
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  @Override
  public List<T> visit(Symbol<T> symbol) {
    List<T> result = new ArrayList<>(1);
    result.add(symbol.elem());
    return result;
  }

  private enum RepetitionSamplingMode {
    EXPONENTIAL,
    LINEAR,
  }
}
