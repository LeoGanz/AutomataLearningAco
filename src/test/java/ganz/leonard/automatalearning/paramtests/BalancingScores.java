package ganz.leonard.automatalearning.paramtests;

// CSVeed is unfortunately not compatible with records somehow
public final class BalancingScores {
  private double scorePos;
  private double scoreNeg;
  private double scoreNegBal;
  private double scoreRef;

  public BalancingScores(
      double scorePos, double scoreNeg, double scoreNegBal, double scoreRef) {
    this.scorePos = scorePos;
    this.scoreNeg = scoreNeg;
    this.scoreNegBal = scoreNegBal;
    this.scoreRef = scoreRef;
  }

  public double scorePos() {
    return scorePos;
  }

  public double scoreNeg() {
    return scoreNeg;
  }

  public double scoreNegBal() {
    return scoreNegBal;
  }

  public double scoreRef() {
    return scoreRef;
  }

  public void setScorePos(double scorePos) {
    this.scorePos = scorePos;
  }

  public void setScoreNeg(double scoreNeg) {
    this.scoreNeg = scoreNeg;
  }

  public void setScoreNegBal(double scoreNegBal) {
    this.scoreNegBal = scoreNegBal;
  }

  public void setScoreRef(double scoreRef) {
    this.scoreRef = scoreRef;
  }
}
