package ganz.leonard.automatalearning.automata.probability;

import ganz.leonard.automatalearning.util.StringifyableFunction;
import java.util.function.BiFunction;

public class PheromoneFunction implements BiFunction<Double, Double, Double> {

  // For completely custom pheromone functions:
  // create a subclass and overwrite apply(Double, Double) and stringRep() possibly ignoring fields

  private final StringifyableFunction<Double, Double> sigmoid; // e.g. x -> x / (1 + Math.abs(x));
  private final double howFarTheUpdateFactorCanBeFromOne;
  private final double minRemainingProbability;

  public PheromoneFunction(
      StringifyableFunction<Double, Double> sigmoid,
      double howFarTheUpdateFactorCanBeFromOne,
      double minRemainingProbability) {
    this.sigmoid =
        sigmoid.wrapWithPredicate(
            val -> val > -1 && val < 1,
            () ->
                new IllegalStateException(
                    "Provided sigmoid function must return values between -1 and 1"));
    this.howFarTheUpdateFactorCanBeFromOne = howFarTheUpdateFactorCanBeFromOne;
    this.minRemainingProbability = minRemainingProbability;
  }

  @Override
  public Double apply(Double pheromoneValue, Double previousProbability) {
    // examples are for howFarTheUpdateFactorCanBeFromOne = 0.1
    double factor = sigmoid.apply(pheromoneValue); // in [-1 ;1]
    factor *= howFarTheUpdateFactorCanBeFromOne; // in [-.1; .1]
    factor += 1; // in [0.9;1.1]
    return Math.max(minRemainingProbability, factor * previousProbability);
  }

  public String stringRep() {
    return "pheromones, prevProb -> "
        + "max(minRemainingProbability, sigmoid(pheromones) * howFarTheUpdateFactorCanBeFromOne + 1)";
  }

  @Override
  public String toString() {
    return stringRep();
  }
}
