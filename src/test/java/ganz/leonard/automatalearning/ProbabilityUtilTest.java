package ganz.leonard.automatalearning;

import ganz.leonard.automatalearning.language.RngUtil;
import java.util.LinkedList;
import java.util.List;

public class ProbabilityUtilTest {
  public static void main(String[] args) {
    int lower = 0;
    int upper = 10;
    List<Integer> samples = new LinkedList<>();
    for (int i = 0; i < 100; i++) {
      int sample = RngUtil.sampleLinearDist(lower, upper);
      samples.add(sample);
      System.out.println(sample);
    }
    System.out.println(samples.stream().noneMatch(i -> i < lower || i >= upper));
  }
}