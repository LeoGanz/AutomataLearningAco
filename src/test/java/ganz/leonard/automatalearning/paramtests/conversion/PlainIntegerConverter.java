package ganz.leonard.automatalearning.paramtests.conversion;

import org.csveed.bean.conversion.AbstractConverter;

public class PlainIntegerConverter extends AbstractConverter<Integer> {
  public PlainIntegerConverter() {
    super(Integer.class);
  }

  @Override
  public Integer fromString(String text) {
    return Integer.parseInt(text);
  }

  @Override
  public String toString(Integer value) {
    return String.valueOf(value);
  }
}
