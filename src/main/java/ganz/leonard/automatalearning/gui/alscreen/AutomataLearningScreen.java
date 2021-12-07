package ganz.leonard.automatalearning.gui.alscreen;

import ganz.leonard.automatalearning.gui.GuiController;
import ganz.leonard.automatalearning.gui.RenderManager;
import ganz.leonard.automatalearning.gui.alscreen.inputwords.InputDisplay;
import ganz.leonard.automatalearning.gui.alscreen.legend.ColorLegend;
import ganz.leonard.automatalearning.gui.util.GuiUtil;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class AutomataLearningScreen extends JPanel {

  public <T> AutomataLearningScreen(
      GuiController controller, AutomataLearning<T> model, RenderManager<T> renderManager) {
    setLayout(new BorderLayout());

    JComponent north = Box.createVerticalBox();
    north.add(new SimulationControls(controller));
    north.add(new JSeparator());
    add(north, BorderLayout.NORTH);

    add(GuiUtil.pad(GuiUtil.center(new GraphComponent<>(renderManager))), BorderLayout.CENTER);

    JComponent south = Box.createVerticalBox();
    south.add(new JSeparator());
    south.add(new SimulationInfo<>(renderManager, model));
    south.add(new JSeparator());
    south.add(new ResultsSegment<>(renderManager));
    add(south, BorderLayout.SOUTH);

    JComponent west = Box.createHorizontalBox();
    west.add(new InputDisplay<>(controller, renderManager, model));
    west.add(new JSeparator(SwingConstants.VERTICAL));
    add(west, BorderLayout.WEST);

    JComponent east = Box.createHorizontalBox();
    east.add(new JSeparator(SwingConstants.VERTICAL));
    east.add(new ColorLegend(renderManager.getGradient()));
    add(east, BorderLayout.EAST);
  }
}
