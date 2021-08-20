package ganz.leonard.automatalearning.gui.alscreen;

import ganz.leonard.automatalearning.gui.GuiController;
import ganz.leonard.automatalearning.gui.util.GuiUtil;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import java.awt.BorderLayout;
import javax.swing.JPanel;

public class AutomataLearningScreen extends JPanel {

  public <T> AutomataLearningScreen(GuiController controller, AutomataLearning<T> model) {
    setLayout(new BorderLayout());
    add(new SimulationControls<>(controller, model), BorderLayout.NORTH);
    add(GuiUtil.centerHorizontally(new GraphComponent<>(model)), BorderLayout.CENTER);
    add(new SimulationInfo<>(model), BorderLayout.SOUTH);
  }
}
