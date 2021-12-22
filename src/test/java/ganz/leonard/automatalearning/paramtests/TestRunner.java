package ganz.leonard.automatalearning.paramtests;

import ganz.leonard.automatalearning.learning.AutomataLearning;
import ganz.leonard.automatalearning.learning.AutomataLearningOptions;
import ganz.leonard.automatalearning.learning.InputWord;
import ganz.leonard.automatalearning.learning.IntermediateResult;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TestRunner {

  private static final int ITERATIONS = 5;
  private static final int MAX_COLONIES = 2000;

  /** @return average Score */
  public static CompletableFuture<Double> test(
      AutomataLearningOptions options,
      List<InputWord<Object>> input,
      TestDataSaver.DataSaverSubtest dataSaver,
      boolean ignoreDfa) {
    return test(options, input, dataSaver, MAX_COLONIES, ignoreDfa);
  }

  /** @return average Score */
  public static CompletableFuture<Double> test(
      AutomataLearningOptions options,
      List<InputWord<Object>> input,
      TestDataSaver.DataSaverSubtest dataSaver,
      int colonies,
      boolean ignoreDfa) {
    return CompletableFuture.supplyAsync(
        () -> execTest(options, input, dataSaver, colonies, ignoreDfa));
  }

  private static double execTest(
      AutomataLearningOptions options,
      List<InputWord<Object>> input,
      TestDataSaver.DataSaverSubtest dataSaver,
      int colonies,
      boolean ignoreDfa) {
    System.out.println(
        "Starting statistics for test language " + input + " with options: " + options + "' ...");
    Set<CompletableFuture<IntermediateResult<Object>>> learners = new HashSet<>(ITERATIONS);
    for (int i = 0; i < ITERATIONS; i++) {
      int finalI = i;
      learners.add(
          CompletableFuture.supplyAsync(
              () -> {
                try {
                  TestDataSaver.DataSaverIteration dataSaverIteration =
                      dataSaver.beginIteration(finalI, ignoreDfa);
                  AutomataLearning<Object> al = new AutomataLearning<>(options, input);
                  dataSaverIteration.writeRow(
                      DataRow.fromIntermediateResult(
                          al.getIntermediateResult(), al.getNrAppliedColonies()));
                  for (int colony = 0; colony < colonies; colony++) {
                    al.runColonies(1);
                    dataSaverIteration.writeRow(
                        DataRow.fromIntermediateResult(
                            al.getIntermediateResult(), al.getNrAppliedColonies()));
                  }
                  dataSaverIteration.close();
                  return al.getBestResult();
                } catch (IOException e) {
                  throw new UncheckedIOException(e);
                }
              }));
    }
    Set<IntermediateResult<Object>> results =
        learners.stream().map(CompletableFuture::join).collect(Collectors.toSet());

    double worstScore = results.stream().mapToDouble(IntermediateResult::score).min().orElse(-1);
    double avgScore = results.stream().mapToDouble(IntermediateResult::score).average().orElse(-1);
    double bestScore = results.stream().mapToDouble(IntermediateResult::score).max().orElse(-1);
    double avgColonies =
        results.stream().mapToDouble(IntermediateResult::nrAppliedColonies).average().orElse(-1);
    // words can be calculated with nrColonies and colonySize only if these values are constant
    // throughout a run which might not hold in the future
    double avgWords =
        results.stream().mapToDouble(IntermediateResult::nrAppliedWords).average().orElse(-1);
    System.out.println("Average Score: " + avgScore);
    System.out.println("Average Colonies applied: " + avgColonies);
    System.out.println("Average Words applied: " + avgWords);
    try {
      dataSaver.writeStatistics(
          new Statistics(worstScore, avgScore, bestScore, avgColonies, avgWords));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return avgScore;
  }
}
