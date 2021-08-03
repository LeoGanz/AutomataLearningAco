package ganz.leonard.automatalearning.language;

import java.util.List;

public record Language<T>(Expression<T> expression) {

  @Override
  public String toString() {
    return expression.toString();
  }

  public List<T> generateSample() {
    SampleBuilder<T> sampleBuilder = SampleBuilder.createExpRepSampleBuilder();
    return generateSample(sampleBuilder);
  }

  public List<T> generateSample(SampleBuilder<T> sampleBuilder) {
    return expression.accept(sampleBuilder);
  }
}
