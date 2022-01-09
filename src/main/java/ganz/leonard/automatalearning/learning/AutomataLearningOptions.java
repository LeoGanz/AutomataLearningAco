package ganz.leonard.automatalearning.learning;

import ganz.leonard.automatalearning.automata.probability.PheromoneFunction;
import ganz.leonard.automatalearning.util.StringifyableFunction;
import io.soabase.recordbuilder.core.RecordBuilder;

// execute 'gradle run' to generate class AutomataLearningOptionsBuilder
@RecordBuilder
public record AutomataLearningOptions(int acceptingStates,
                                      int notAcceptingStates,
                                      int initialPheromones,
                                      double feedback,
                                      double decayFactor,
                                      int inputSamples,
                                      int colonySize,
                                      PheromoneFunction pheromoneFunction,
                                      boolean balanceInput)
    implements AutomataLearningOptionsBuilder.With {

  public static final int DEF_ACCEPTING_STATES = 2;
  public static final int DEF_NOT_ACCEPTING_STATES = 1;
  public static final int DEF_INITIAL_PHEROMONES = 0;
  public static final double DEF_FEEDBACK = .5;
  public static final double DEF_DECAY_FACTOR = .8;
  public static final int DEF_INPUT_SAMPLES = 20;
  public static final int DEF_COLONY_SIZE = 16;
  public static final double DEF_SPREAD = 0.14;
  public static final double DEF_MIN_REM_PROB = 0.016;
  public static final StringifyableFunction<Double, Double> DEF_SIGMOID =
      new StringifyableFunction<>() {
        @Override
        public Double apply(Double x) {
          return x / (1 + Math.abs(x));
        }

        @Override
        public String stringRep() {
          return "x -> x / (1 + |x|)";
        }
      };

  public AutomataLearningOptions {
    if (acceptingStates <= 0) {
      acceptingStates = DEF_ACCEPTING_STATES;
    }
    if (notAcceptingStates < 0) {
      // zero is allowed. for simple languages like a* one accepting state is enough
      notAcceptingStates = DEF_NOT_ACCEPTING_STATES;
    }
    if (decayFactor < 0) {
      decayFactor = DEF_DECAY_FACTOR;
    }
    if (inputSamples <= 0) {
      inputSamples = DEF_INPUT_SAMPLES;
    }

    // colony size == -1 indicates automatic size

    if (pheromoneFunction == null) {
      pheromoneFunction = new PheromoneFunction(DEF_SIGMOID, 0.1, 0.01);
    }
  }
}
