package ganz.leonard.automatalearning.gui.alscreen.legend;

import ganz.leonard.automatalearning.gui.GuiController;
import ganz.leonard.automatalearning.gui.RenderManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class DfaProbChooser<T> extends JSlider implements PropertyChangeListener {

  public DfaProbChooser(GuiController controller, RenderManager<T> renderManager) {
    super(SwingConstants.VERTICAL);
    renderManager.addPropertyChangeListener(this);
    setInverted(true);
    addChangeListener(
        e -> {
          if (!getValueIsAdjusting()) {
            controller.requestsMinDfaProbChange(getValue() / 100.0);
          }
        });
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (RenderManager.MIN_DFA_PROB_UPDATE.equals(evt.getPropertyName())) {
      SwingUtilities.invokeLater(() -> updateValue((double) evt.getNewValue()));
    }
  }

  private void updateValue(double newValue) {
    setValue((int) (newValue * 100));
  }
}
