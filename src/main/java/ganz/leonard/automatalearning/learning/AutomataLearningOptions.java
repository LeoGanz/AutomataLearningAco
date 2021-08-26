package ganz.leonard.automatalearning.learning;

import io.soabase.recordbuilder.core.RecordBuilder;

// execute 'gradle run' to generate class AutomataLearningOptionsBuilder
@RecordBuilder
public record AutomataLearningOptions(int acceptingStates,
                                      int notAcceptingStates,
                                      int initialPheromones,
                                      double positiveFeedbackFactor,
                                      double negativeFeedbackFactor,
                                      int inputSamples)
    implements AutomataLearningOptionsBuilder.With {

  public static final int DEF_ACCEPTING_STATES = 2;
  public static final int DEF_NOT_ACCEPTING_STATES = 2;
  public static final int DEF_INITIAL_PHEROMONES = 1;
  public static final double DEF_POSITIVE_FEEDBACK_FACTOR = 1.2;
  public static final double DEF_NEGATIVE_FEEDBACK_FACTOR = .5;
  public static final int DEF_INPUT_SAMPLES = 20;

  public AutomataLearningOptions {
    if (acceptingStates <= 0) {
      acceptingStates = DEF_ACCEPTING_STATES;
    }
    if (notAcceptingStates < 0) {
      // zero is allowed. for simple languages like a* one accepting state is enough
      notAcceptingStates = DEF_NOT_ACCEPTING_STATES;
    }
    if (initialPheromones <= 0) {
      initialPheromones = DEF_INITIAL_PHEROMONES;
    }
    if (positiveFeedbackFactor < 1) {
      positiveFeedbackFactor = DEF_POSITIVE_FEEDBACK_FACTOR;
    }
    if (negativeFeedbackFactor <= 0) {
      negativeFeedbackFactor = DEF_NEGATIVE_FEEDBACK_FACTOR;
    }
    if (inputSamples <= 0) {
      inputSamples = DEF_INPUT_SAMPLES;
    }
  }
}
