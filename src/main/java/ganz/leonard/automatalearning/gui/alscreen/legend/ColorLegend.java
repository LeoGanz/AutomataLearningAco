package ganz.leonard.automatalearning.gui.alscreen.legend;

import ganz.leonard.automatalearning.gui.util.LinearColorGradient;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class ColorLegend extends JPanel {

  private static final int PADDING = 15;

  public ColorLegend(LinearColorGradient gradient) {
    setLayout(new MigLayout("insets " + PADDING));
    add(new JLabel("Color Legend"), "center, wrap");
    GradientBar gradientBar = new GradientBar(gradient, true);
    add(gradientBar, "center, grow, push, wrap");
    add(new JLabel("<html>Percentage of Ants<br>choosing a transition"), "wrap");
  }
}
