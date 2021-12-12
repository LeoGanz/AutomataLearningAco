package ganz.leonard.automatalearning.unittests;

import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.automata.general.DeterministicState;
import ganz.leonard.automatalearning.automata.probability.FeedbackAutomaton;
import ganz.leonard.automatalearning.automata.probability.ProbabilityState;
import ganz.leonard.automatalearning.learning.AutomataLearningOptions;
import ganz.leonard.automatalearning.learning.AutomataLearningOptionsBuilder;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProbToDetConverterTest {

  @Test
  void testWithKnownAutomaton() {
    AutomataLearningOptions options =
        AutomataLearningOptionsBuilder.builder().feedback(1).build();
    ProbabilityState<Character> fst = new ProbabilityState<>(0, false, options);
    ProbabilityState<Character> snd = new ProbabilityState<>(1, false, options);
    ProbabilityState<Character> third = new ProbabilityState<>(2, true, options);
    Collection<ProbabilityState<Character>> states = Set.of(fst, snd, third);

    fst.addTransitionsTo(states);
    snd.addTransitionsTo(states);

    IntStream.range(0, 10)
        .forEach(
            __ -> {
              fst.getTransitionTo(snd).pheromoneFeedback('a', true);
              fst.getTransitionTo(third).pheromoneFeedback('b', true);
              snd.getTransitionTo(fst).pheromoneFeedback('a', true);
              snd.getTransitionTo(third).pheromoneFeedback('b', true);
              fst.updateProbabilities('a');
              fst.updateProbabilities('b');
              snd.updateProbabilities('a');
              snd.updateProbabilities('b');
            });

    FeedbackAutomaton<Character> feedbackAutomaton = new FeedbackAutomaton<>(states, fst, options);

    DeterministicState<Character> fstDet = new DeterministicState<>(0, false);
    DeterministicState<Character> sndDet = new DeterministicState<>(1, false);
    DeterministicState<Character> thirdDet = new DeterministicState<>(2, true);

    fstDet.addTransitions(Map.of('a', sndDet, 'b', thirdDet));
    sndDet.addTransitions(Map.of('a', fstDet, 'b', thirdDet));

    DeterministicFiniteAutomaton<Character> dfa =
        new DeterministicFiniteAutomaton<>(Set.of(fstDet, sndDet, thirdDet), fstDet);

    Assertions.assertEquals(dfa, feedbackAutomaton.buildMostLikelyDfa());
  }
}
