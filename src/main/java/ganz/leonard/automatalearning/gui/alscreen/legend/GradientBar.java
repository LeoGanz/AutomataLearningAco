package ganz.leonard.automatalearning.gui.alscreen.legend;

import ganz.leonard.automatalearning.Util;
import ganz.leonard.automatalearning.gui.util.LinearColorGradient;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

class GradientBar extends JPanel {

  private final LinearColorGradient gradient;
  private final boolean vertical;

  public GradientBar(LinearColorGradient gradient, boolean vertical) {
    this.gradient = gradient;
    this.vertical = vertical;
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setFont(new Font("monospaced", g2.getFont().getStyle(), g2.getFont().getSize()));
    int yFontOffset = g2.getFont().getSize();
    int barWidth = getWidth() / 4;
    int barHeight = getHeight() - yFontOffset;
    g2.setPaint(gradient.getGradientPaint(getHeight(), vertical));
    g2.fillRect(0, yFontOffset / 2, barWidth, barHeight);

    g2.setPaint(Color.BLACK);
    int steps = 4;
    for (int i = 0; i < steps; i++) {
      int offset = i == 0 ? yFontOffset : yFontOffset / 2;
      g2.drawString(
          "- " + Util.padLeft(i * 100 / steps + "%", 3), barWidth, i * getHeight() / steps + offset);
    }
    g2.drawString("- 100%", barWidth, getHeight() - yFontOffset / 4);
  }
}
