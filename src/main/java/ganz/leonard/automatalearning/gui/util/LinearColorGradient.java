package ganz.leonard.automatalearning.gui.util;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

import java.awt.Color;
import java.awt.GradientPaint;
import java.util.function.Function;

public record LinearColorGradient(Color start, Color end) {
  private static final Color DEFAULT_START = new Color(255, 255, 120);
  private static final Color DEFAULT_END = new Color(0, 0, 75);

  public LinearColorGradient() {
    this(DEFAULT_START, DEFAULT_END);
  }

  public Color getColor(double percentage) {
    if (percentage < 0 || percentage > 1) {
      throw new IllegalArgumentException("Percentage must be between 0 and 1");
    }

    double fst = 1 - percentage;
    double snd = percentage;

    Function<Double, Integer> toRgbValSpace = (color) -> (int) min(255, max(0, round(color)));
    int red = toRgbValSpace.apply(fst * start.getRed() + snd * end.getRed());
    int green = toRgbValSpace.apply(fst * start.getGreen() + snd * end.getGreen());
    int blue = toRgbValSpace.apply(fst * start.getBlue() + snd * end.getBlue());
    return new Color(red, green, blue);
  }

  public GradientPaint getGradientPaint(int length, boolean vertical) {
    return new GradientPaint(0,0, start, vertical ? 0 : length, vertical ? length : 0, end);
  }

}
