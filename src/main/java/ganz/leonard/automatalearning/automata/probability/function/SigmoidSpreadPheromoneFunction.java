package ganz.leonard.automatalearning.automata.probability.function;

import ganz.leonard.automatalearning.util.StringifyableFunction;

public record SigmoidSpreadPheromoneFunction(
    StringifyableFunction<Double, Double> sigmoid,
    double spread,
    double minRemainingProbability)
    implements PheromoneFunction {

  public SigmoidSpreadPheromoneFunction(
      StringifyableFunction<Double, Double> sigmoid,
      double spread,
      double minRemainingProbability) {
    this.sigmoid =
        sigmoid.wrapWithPredicate(
            val -> val > -1 && val < 1,
            () ->
                new IllegalStateException(
                    "Provided sigmoid function must return values between -1 and 1"));
    this.spread = spread;
    this.minRemainingProbability = minRemainingProbability;
  }

  @Override
  public Double apply(Double pheromoneValue, Double previousProbability) {
    // examples are for spread = 0.1
    double factor = sigmoid.apply(pheromoneValue); // in [-1 ;1]
    factor *= spread; // in [-.1; .1]
    factor += 1; // in [0.9;1.1]
    return Math.max(minRemainingProbability, factor * previousProbability);
  }

  public String stringRep() {
    return "pheromones, prevProb -> " +
        "max(minRemainingProbability, (sigmoid(pheromones) * spread + 1) * prevProb)";
  }

  @Override
  public String toString() {
    return stringRep();
  }
}
