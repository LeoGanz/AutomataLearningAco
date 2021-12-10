package ganz.leonard.automatalearning.gui.alscreen.legend;

import ganz.leonard.automatalearning.gui.GuiController;
import ganz.leonard.automatalearning.gui.RenderManager;
import ganz.leonard.automatalearning.gui.alscreen.AutomataLearningScreen;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import net.miginfocom.swing.MigLayout;

public class ProbabilityControl extends JPanel {

  private static final int PADDING = 15;

  public <T> ProbabilityControl(GuiController controller, RenderManager<T> renderManager) {
    setLayout(new MigLayout("insets " + PADDING));
    JLabel title = new JLabel("Probability Controls");
    title.setFont(title.getFont().deriveFont(AutomataLearningScreen.HEADING_FONT_SIZE));
    add(title, "center, span, wrap " + PADDING);
    add(new JLabel("<html>Percentage of Ants<br>choosing a transition"), "span, wrap");
    add(new JLabel("\u25BC"), "align 0.1al, wrap"); // arrow down
    GradientBar gradientBar = new GradientBar(renderManager.getGradient());
    add(gradientBar, "center, grow, push");
    add(new DfaProbChooser<>(controller, renderManager), "grow, wrap");
    add(new JLabel("\u25B2"), "skip, center, wrap"); // arrow up
    JLabel sliderExplainer =
        new JLabel(
            "<html><p style=\"text-align:right;\">"
                + "Minimum probability<br>to highlight transition<br>"
                + "<br>Highlighted (solid) tran-<br>sitions are part of dfa");
    sliderExplainer.setHorizontalAlignment(SwingConstants.RIGHT);
    add(sliderExplainer, "span, right, wrap");
  }
}
