package ganz.leonard.automatalearning.paramtests;

import java.util.List;

public record Statistics(
    double lowestBestScore,
    double avgBestScore,
    double highestBestScore,
    List<Double> bestScores,
    double avgColonies,
    double avgWords) {
}
