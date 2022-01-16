package ganz.leonard.automatalearning.automata.probability.function;

import java.util.function.BiFunction;

public interface PheromoneFunction extends BiFunction<Double, Double, Double> {

  String stringRep();
}
