package ganz.leonard.automatalearning.learning;

import io.soabase.recordbuilder.core.RecordBuilder;

// execute 'gradle run' to generate class AutomataLearningOptionsBuilder
@RecordBuilder
public record AutomataLearningOptions(int acceptingStates,
                                      int notAcceptingStates,
                                      int initialPheromones,
                                      double feedback,
                                      double decayFactor,
                                      int inputSamples,
                                      int colonySize)
    implements AutomataLearningOptionsBuilder.With {

  public static final int DEF_ACCEPTING_STATES = 2;
  public static final int DEF_NOT_ACCEPTING_STATES = 2;
  public static final int DEF_INITIAL_PHEROMONES = 0;
  public static final double DEF_FEEDBACK = 1;
  public static final double DEF_DECAY_FACTOR = .8;
  public static final int DEF_INPUT_SAMPLES = 20;
  public static final int DEF_COLONY_SIZE = 1;

  public AutomataLearningOptions {
    if (acceptingStates <= 0) {
      acceptingStates = DEF_ACCEPTING_STATES;
    }
    if (notAcceptingStates < 0) {
      // zero is allowed. for simple languages like a* one accepting state is enough
      notAcceptingStates = DEF_NOT_ACCEPTING_STATES;
    }
    if (decayFactor <= 0) {
      decayFactor = DEF_DECAY_FACTOR;
    }
    if (inputSamples <= 0) {
      inputSamples = DEF_INPUT_SAMPLES;
    }
    // colony size == -1 indicates automatic size
  }
}
