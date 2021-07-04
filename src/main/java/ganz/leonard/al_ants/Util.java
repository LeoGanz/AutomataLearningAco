package ganz.leonard.al_ants;

public class Util {
  public static int nrOfDigits(int i) {
    if (i == 0) {
      return 1;
    }
    if (i < 0) {
      return nrOfDigits(-i) + 1;
    }
    return (int) Math.ceil(Math.log10(i+1));
  }

}
