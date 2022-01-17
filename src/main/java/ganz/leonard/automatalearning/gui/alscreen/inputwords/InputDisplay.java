package ganz.leonard.automatalearning.gui.alscreen.inputwords;

import ganz.leonard.automatalearning.gui.GuiController;
import ganz.leonard.automatalearning.gui.RenderManager;
import ganz.leonard.automatalearning.gui.alscreen.AutomataLearningScreen;
import ganz.leonard.automatalearning.gui.util.GuiUtil;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class InputDisplay<T> extends JPanel {

  public static final int VERTICAL_GAP = 10;

  public InputDisplay(
      GuiController controller,
      RenderManager<T> renderManager,
      AutomataLearning<T> automataLearning) {
    setLayout(new BorderLayout(0, VERTICAL_GAP));
    JLabel title = new JLabel("Input Words");
    title.setFont(title.getFont().deriveFont(AutomataLearningScreen.HEADING_FONT_SIZE));
    title.setHorizontalAlignment(SwingConstants.CENTER);
    add(title, BorderLayout.NORTH);
    add(
        new ScrollableWordList<>(new WordList<>(controller, renderManager, automataLearning)),
        BorderLayout.CENTER);
    GuiUtil.pad(this);
  }
}
