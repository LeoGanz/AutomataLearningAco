package ganz.leonard.automatalearning;

import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.automata.general.DeterministicState;
import ganz.leonard.automatalearning.automata.probability.FeedbackAutomaton;
import ganz.leonard.automatalearning.automata.probability.ProbabilityState;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProbToDetConverterTest {

  @Test
  void testWithKnownAutomaton() {
    ProbabilityState<Character> fst = new ProbabilityState<>(0, false);
    ProbabilityState<Character> snd = new ProbabilityState<>(1, false);
    ProbabilityState<Character> third = new ProbabilityState<>(2, true);
    Collection<ProbabilityState<Character>> states = Set.of(fst, snd, third);

    fst.initTransitionsTo(states);
    snd.initTransitionsTo(states);

    fst.getTransitionTo(snd).positivePheromoneFeedback('a');
    fst.getTransitionTo(third).positivePheromoneFeedback('b');
    snd.getTransitionTo(fst).positivePheromoneFeedback('a');
    snd.getTransitionTo(third).positivePheromoneFeedback('b');

    FeedbackAutomaton<Character> feedbackAutomaton = new FeedbackAutomaton<>(states, fst);

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
