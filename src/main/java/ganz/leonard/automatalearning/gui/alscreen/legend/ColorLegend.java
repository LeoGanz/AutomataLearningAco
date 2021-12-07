package ganz.leonard.automatalearning.gui.alscreen.legend;

import ganz.leonard.automatalearning.gui.util.LinearColorGradient;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class ColorLegend extends JPanel {

  private static final int PADDING = 15;

  public ColorLegend(LinearColorGradient gradient) {
    setLayout(new MigLayout("insets " + PADDING));
    add(new JLabel("Color Legend"), "center, span, wrap");
    GradientBar gradientBar = new GradientBar(gradient, true);
    add(gradientBar, "center,span, grow, push, wrap");
    add(new JLabel("<html>Percentage of Ants<br>choosing a transition"), "span, wrap 20");

    add(new JLabel("<html>Color of tran-<br>sitions in dfa: "), "pushx 5");
    JPanel rectangle = new JPanel();
    Dimension rectDim = new Dimension(25, 25);
    rectangle.setMaximumSize(rectDim);
    rectangle.setPreferredSize(rectDim);
    rectangle.setBackground(Color.RED);
    add(rectangle, "wrap");
  }
}
