package ganz.leonard.automatalearning.paramtests.conversion;

import ganz.leonard.automatalearning.automata.general.DeterministicFiniteAutomaton;
import ganz.leonard.automatalearning.automata.tools.GreeneryNotationConverter;
import org.csveed.bean.conversion.AbstractConverter;

public class DfaConverter extends AbstractConverter<DeterministicFiniteAutomaton> {

  // greenery notation is not necessarily the best, but converters already exist
  private final GreeneryNotationConverter<Object> greeneryNotationConverter;

  public DfaConverter() {
    super(DeterministicFiniteAutomaton.class);
    greeneryNotationConverter = new GreeneryNotationConverter<>();
  }

  @Override
  public DeterministicFiniteAutomaton fromString(String text) {
    if (text == null) {
      throw new IllegalArgumentException("null String cannot be converted to dfa");
    }
    return greeneryNotationConverter.fromGreeneryNotation(text);
  }

  @Override
  public String toString(DeterministicFiniteAutomaton value) {
    return greeneryNotationConverter.toGreeneryNotation(value, false, false).toString();
  }
}
