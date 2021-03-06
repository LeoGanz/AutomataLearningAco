package ganz.leonard.automatalearning.learning;

import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;

public record IntermediateResult<T>(
    int nrAppliedColonies,
    int nrAppliedWords,
    DeterministicFiniteAutomaton<T> automaton,
    double score
) {
}
