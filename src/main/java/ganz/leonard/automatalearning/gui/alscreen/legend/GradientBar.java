package ganz.leonard.automatalearning.gui.alscreen.legend;

import ganz.leonard.automatalearning.util.Util;
import ganz.leonard.automatalearning.gui.util.LinearColorGradient;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

class GradientBar extends JPanel {

  public static final int NUMBER_SCALE_STEPS = 4;
  public static final double BAR_PORTION_OF_WIDTH = 1.0 / 3;
  public static final int STRING_LENGTH_OF_PERCENTAGES = 3;
  private final LinearColorGradient gradient;

  public GradientBar(LinearColorGradient gradient) {
    this.gradient = gradient;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setFont(new Font("monospaced", g2.getFont().getStyle(), g2.getFont().getSize()));
    int verticalFontOffset = g2.getFont().getSize();
    int barWidth = (int) (getWidth() * BAR_PORTION_OF_WIDTH);
    int barHeight = getHeight() - verticalFontOffset;
    g2.setPaint(gradient.getGradientPaint(getHeight(), true));
    g2.fillRect(0, verticalFontOffset / 2, barWidth, barHeight);

    g2.setPaint(Color.BLACK);

    for (int i = 0; i < NUMBER_SCALE_STEPS; i++) {
      int offset = i == 0 ? verticalFontOffset : verticalFontOffset / 2;
      g2.drawString(
          "- " + Util.padLeft(i * 100 / NUMBER_SCALE_STEPS + "%", STRING_LENGTH_OF_PERCENTAGES),
          barWidth,
          i * getHeight() / NUMBER_SCALE_STEPS + offset);
    }
    g2.drawString("- 100%", barWidth, getHeight() - verticalFontOffset / 4);
  }
}
