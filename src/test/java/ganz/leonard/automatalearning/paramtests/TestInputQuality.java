package ganz.leonard.automatalearning.paramtests;

import static ganz.leonard.automatalearning.paramtests.TestingUtil.MEASUREMENTS_DIR;
import static ganz.leonard.automatalearning.paramtests.TestingUtil.calcAverageMatch;
import static ganz.leonard.automatalearning.paramtests.TestingUtil.getOptionsBuilder;
import static ganz.leonard.automatalearning.paramtests.TestingUtil.loadInput;
import static ganz.leonard.automatalearning.paramtests.TestingUtil.runTest;

import ganz.leonard.automatalearning.learning.InputWord;
import ganz.leonard.automatalearning.learning.IntermediateResult;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.junit.jupiter.api.RepeatedTest;

public class TestInputQuality {

  public static final String BALANCING_SCORES_CSV = "BalancingScores.csv";
  public static final String INPUT_QUALITY_CSV = "InputQuality.csv";
  private static final String FILE_A_STAR_B_REFERENCE = "Test-AstarB_reference.txt";
  private static final String FILE_A_STAR_B_POSITIVES = "Test-AstarB_positives.txt";
  private static final String FILE_A_STAR_B_NEGATIVES = "Test-AstarB_negatives.txt";
  private static final String FILE_A_STAR_B_FEW = "Test-AstarB_few.txt";
  private static final String FILE_A_STAR_B_SHORT = "Test-AstarB_short.txt";
  private static final String FILE_A_STAR_B_LONG = "Test-AstarB_long.txt";
  private static final int TEST_ITERATIONS = 21;

  @RepeatedTest(TEST_ITERATIONS)
  void testAstarB_OtherInputQuality() throws IOException, URISyntaxException {
    List<InputWord<Object>> referenceWords = loadInput(FILE_A_STAR_B_REFERENCE);
    List<IntermediateResult<Object>> resultsFew =
        runTest(
            loadInput(FILE_A_STAR_B_FEW),
            "AstarB_few",
            getOptionsBuilder().balanceInput(true).build());
    List<IntermediateResult<Object>> resultsShort =
        runTest(
            loadInput(FILE_A_STAR_B_SHORT),
            "AstarB_short",
            getOptionsBuilder().balanceInput(true).build());
    List<IntermediateResult<Object>> resultsLong =
        runTest(
            loadInput(FILE_A_STAR_B_LONG),
            "AstarB_long",
            getOptionsBuilder().balanceInput(true).build());
    List<IntermediateResult<Object>> resultsReference =
        runTest(referenceWords, "AstarB_reference", getOptionsBuilder().balanceInput(true).build());

    ScoreName scoreFew = new ScoreName(calcAverageMatch(resultsFew, referenceWords), "Few Words");
    ScoreName scoreShort =
        new ScoreName(calcAverageMatch(resultsShort, referenceWords), "Short Words");
    ScoreName scoreLong =
        new ScoreName(calcAverageMatch(resultsLong, referenceWords), "Long Words");
    ScoreName scoreRef =
        new ScoreName(calcAverageMatch(resultsReference, referenceWords), "Reference");
    outputScores(INPUT_QUALITY_CSV, scoreLong, scoreFew, scoreShort, scoreRef);
  }

  @RepeatedTest(TEST_ITERATIONS)
  void testAstarB_Balance() throws IOException, URISyntaxException {
    List<InputWord<Object>> referenceWords = loadInput(FILE_A_STAR_B_REFERENCE);
    List<IntermediateResult<Object>> results_neg =
        runTest(
            loadInput(FILE_A_STAR_B_NEGATIVES),
            "AstarB_negatives",
            getOptionsBuilder().balanceInput(false).build());
    List<IntermediateResult<Object>> results_neg_balanced =
        runTest(
            loadInput(FILE_A_STAR_B_NEGATIVES),
            "AstarB_negatives_balanced",
            getOptionsBuilder().balanceInput(true).build());
    List<IntermediateResult<Object>> results_positives =
        runTest(
            loadInput(FILE_A_STAR_B_POSITIVES),
            "AstarB_positives",
            getOptionsBuilder().balanceInput(false).build());
    List<IntermediateResult<Object>> results_reference =
        runTest(
            referenceWords, "AstarB_reference", getOptionsBuilder().balanceInput(false).build());

    ScoreName score_neg =
        new ScoreName(calcAverageMatch(results_neg, referenceWords), "Mainly Negatives");
    ScoreName score_neg_bal =
        new ScoreName(
            calcAverageMatch(results_neg_balanced, referenceWords), "Mainly Negatives - Balanced");
    ScoreName score_pos =
        new ScoreName(calcAverageMatch(results_positives, referenceWords), "Positives Only");
    ScoreName score_ref =
        new ScoreName(calcAverageMatch(results_reference, referenceWords), "Reference");
    outputScores(BALANCING_SCORES_CSV, score_pos, score_neg, score_neg_bal, score_ref);
  }

  private void outputScores(String filename, ScoreName... scores) throws IOException {
    // Der CSV writer hat gestreikt, da geht von Hand schreiben schneller^^
    Path csvFile = Paths.get(MEASUREMENTS_DIR, filename);
    Files.createDirectories(csvFile.getParent());
    boolean isFirstTime = !Files.exists(csvFile);
    BufferedWriter writer =
        Files.newBufferedWriter(
            csvFile,
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND,
            StandardOpenOption.WRITE);
    if (isFirstTime) {
      writeRow(writer, scores, true);
    }
    writeRow(writer, scores, false);
    writer.close();
    printInfo(scores);
  }

  private void printInfo(ScoreName[] scores) {
    StringBuilder sb =
        new StringBuilder("Comparison of average match against a set of reference words: \n");
    for (ScoreName score : scores) {
      sb.append("Input with ");
      sb.append(score.scoreName);
      sb.append(": ");
      sb.append(score.scoreVal);
      sb.append("\n");
    }
    System.out.println(sb);
  }

  private void writeRow(BufferedWriter writer, ScoreName[] scores, boolean isHeader)
      throws IOException {
    for (int i = 0; i < scores.length; i++) {
      if (i != 0) {
        writer.write(",");
      }
      writer.write("\"");
      if (isHeader) {
        writer.write(scores[i].scoreName);
      } else {
        writer.write(String.valueOf(scores[i].scoreVal));
      }
      writer.write("\"");
    }
    writer.newLine();
  }

  private static record ScoreName(double scoreVal, String scoreName) {}
}
