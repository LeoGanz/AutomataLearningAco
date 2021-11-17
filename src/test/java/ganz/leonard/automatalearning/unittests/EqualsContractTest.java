package ganz.leonard.automatalearning.unittests;

import ganz.leonard.automatalearning.automata.general.Automaton;
import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.automata.general.DeterministicState;
import ganz.leonard.automatalearning.automata.general.State;
import ganz.leonard.automatalearning.automata.probability.FeedbackAutomaton;
import ganz.leonard.automatalearning.automata.probability.PheromoneTransition;
import ganz.leonard.automatalearning.automata.probability.ProbToDetConverter;
import ganz.leonard.automatalearning.automata.probability.ProbabilityState;
import ganz.leonard.automatalearning.language.Alternative;
import ganz.leonard.automatalearning.language.Language;
import ganz.leonard.automatalearning.language.Repetition;
import ganz.leonard.automatalearning.language.Sequence;
import ganz.leonard.automatalearning.language.Symbol;
import ganz.leonard.automatalearning.learning.AutomataLearningOptionsBuilder;
import nl.jqno.equalsverifier.ConfiguredEqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EqualsContractTest {

  private static ConfiguredEqualsVerifier ev;

  @BeforeAll
  static void setup() {
    ev = EqualsVerifier.configure().usingGetClass();
  }

  @Test
  void generalAutomaton() {
    ev.forClass(Automaton.class)
        .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED) // problems with recursive generics
        .withRedefinedSubclass(DeterministicFiniteAutomaton.class)
        .verify();
  }

  @Test
  void deterministicFiniteAutomaton() {
    ev.forClass(DeterministicFiniteAutomaton.class)
        .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
        .withRedefinedSuperclass()
        .withGenericPrefabValues(State.class, (s, t) -> new DeterministicState<>(1, false))
        .verify();
  }

  @Test
  void feedbackAutomaton() {
    // Converters not used for equals anyway
    ProbToDetConverter<Object> converter = new ProbToDetConverter<>(null);
    ProbToDetConverter<Object> converter2 = new ProbToDetConverter<>(null);

    ev.forClass(FeedbackAutomaton.class)
        .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
        .withRedefinedSuperclass()
        .withPrefabValues(ProbToDetConverter.class, converter, converter2)
        .withGenericPrefabValues(
            State.class,
            (s, t) ->
                new ProbabilityState<>(1, false, AutomataLearningOptionsBuilder.builder().build()))
        .verify();
  }

  @Test
  void pheromoneTransition() {
    ev.forClass(PheromoneTransition.class).verify();
  }

  @Test
  void language() {
    ev.forClasses(Alternative.class, Language.class, Symbol.class, Repetition.class, Sequence.class)
        .verify();
  }
}
