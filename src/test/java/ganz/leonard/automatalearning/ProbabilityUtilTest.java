package ganz.leonard.automatalearning;

import ganz.leonard.automatalearning.language.RngUtil;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ProbabilityUtilTest {

  @Test
  void testSampling() {
    int lower = 0;
    int upper = 10;
    List<Integer> samples = new LinkedList<>();
    for (int i = 0; i < 100; i++) {
      int sample = RngUtil.sampleLinearDist(lower, upper);
      samples.add(sample);
    }
    Assertions.assertTrue(samples.stream().noneMatch(i -> i < lower || i >= upper));
  }
}
