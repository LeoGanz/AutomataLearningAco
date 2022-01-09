package ganz.leonard.automatalearning.paramtests.execution;

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

public class TestRunner {

  private static final int DEF_ITERATIONS = 5;

  /** @return average Score */
  public static CompletableFuture<List<IntermediateResult<Object>>> test(
      AutomataLearningOptions options,
      List<InputWord<Object>> input,
      TestDataSaver.DataSaverSubtest dataSaver,
      int colonies) {
    return test(options, input, dataSaver, colonies, true, true);
  }

  /** @return average Score */
  public static CompletableFuture<List<IntermediateResult<Object>>> test(
      AutomataLearningOptions options,
      List<InputWord<Object>> input,
      TestDataSaver.DataSaverSubtest dataSaver,
      int colonies,
      boolean ignoreDfa,
      boolean ignoreRegex) {
    return test(options, input, dataSaver, DEF_ITERATIONS, colonies, ignoreDfa, ignoreRegex);
  }

  /** @return average Score */
  public static CompletableFuture<List<IntermediateResult<Object>>> test(
      AutomataLearningOptions options,
      List<InputWord<Object>> input,
      TestDataSaver.DataSaverSubtest dataSaver,
      int iterations,
      int colonies,
      boolean ignoreDfa,
      boolean ignoreRegex) {
    return CompletableFuture.supplyAsync(
        () -> execTest(options, input, dataSaver, iterations, colonies, ignoreDfa, ignoreRegex));
  }

  private static List<IntermediateResult<Object>> execTest(
      AutomataLearningOptions options,
      List<InputWord<Object>> input,
      TestDataSaver.DataSaverSubtest dataSaver,
      int iterations,
      int colonies,
      boolean ignoreDfa,
      boolean ignoreRegex) {
    System.out.println(
        "Starting statistics for test language " + input + " with options: " + options + "' ...");
    Set<CompletableFuture<IntermediateResult<Object>>> learners = new HashSet<>(iterations);
    for (int i = 0; i < iterations; i++) {
      int finalI = i;
      learners.add(
          CompletableFuture.supplyAsync(
              () -> {
                try {
                  TestDataSaver.DataSaverIteration dataSaverIteration =
                      dataSaver.beginIteration(finalI, ignoreDfa, ignoreRegex);
                  AutomataLearning<Object> al = new AutomataLearning<>(options, input);
                  dataSaverIteration.writeRow(
                      DataRow.fromIntermediateResult(
                          al.getIntermediateResult(), al.getNrAppliedColonies(), ignoreRegex));
                  for (int colony = 0; colony < colonies; colony++) {
                    al.runColonies(1);
                    dataSaverIteration.writeRow(
                        DataRow.fromIntermediateResult(
                            al.getIntermediateResult(), al.getNrAppliedColonies(), ignoreRegex));
                  }
                  dataSaverIteration.close();
                  return al.getBestResult();
                } catch (IOException e) {
                  throw new UncheckedIOException(e);
                }
              }));
    }
    List<IntermediateResult<Object>> results =
        learners.stream().map(CompletableFuture::join).toList();

    List<Double> bestScores = results.stream().map(IntermediateResult::score).toList();
    double worstScore = bestScores.stream().mapToDouble(Double::doubleValue).min().orElse(-1);
    double avgScore = bestScores.stream().mapToDouble(Double::doubleValue).average().orElse(-1);
    double bestScore = bestScores.stream().mapToDouble(Double::doubleValue).max().orElse(-1);
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
          new Statistics(worstScore, avgScore, bestScore, bestScores, avgColonies, avgWords));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return results;
  }
}
