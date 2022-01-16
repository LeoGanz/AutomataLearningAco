package ganz.leonard.automatalearning.automata.probability.function;

// ideas for this update function are from Stephan Barth
public class SqrtPheromoneFunction implements PheromoneFunction {
  @Override
  public String stringRep() {
    return "pheromones, prevProb -> 1.1^(sgn(pheromones) * sqrt(|pheromones|)) * prevProb";
  }

  @Override
  public Double apply(Double pheromones, Double prevProb) {
    return Math.pow(1.1, Math.signum(pheromones) * Math.sqrt(Math.abs(pheromones))) * prevProb;
  }

  @Override
  public String toString() {
    return stringRep();
  }
}
