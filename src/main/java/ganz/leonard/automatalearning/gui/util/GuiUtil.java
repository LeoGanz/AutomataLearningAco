package ganz.leonard.automatalearning.gui.util;

import java.awt.Insets;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

public class GuiUtil {
  private static final int PADDING = 15;
  private static final Insets DEFAULT_INSETS = new Insets(PADDING, PADDING, PADDING, PADDING);

  private GuiUtil() {}

  public static JComponent centerHorizontally(final JComponent component) {
    JComponent center = new Box(BoxLayout.LINE_AXIS);
    center.add(Box.createHorizontalGlue());
    center.add(component);
    center.add(Box.createHorizontalGlue());
    return center;
  }

  public static JComponent pad(final JComponent component) {
    if (component instanceof AbstractButton) {
      ((AbstractButton) component).setMargin(DEFAULT_INSETS);
    } else {
      component.setBorder(
          BorderFactory.createCompoundBorder(
              new EmptyBorder(DEFAULT_INSETS), component.getBorder()));
    }
    return component;
  }

  public static void executeOnSwingWorker(Runnable toExecute) {
    executeOnSwingWorker(toExecute, null);
  }

  public static void executeOnSwingWorker(Runnable toExecute, Runnable onDone) {
    new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() {
        if (toExecute != null) {
          toExecute.run();
        }
        return null;
      }

      @Override
      protected void done() {
        try {
          // keeps exceptions in background thread from vanishing
          get();
        } catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
          throw new RuntimeException("No exception treatment possible here!");
        }
        if (onDone != null) {
          onDone.run();
        }
      }
    }.execute();
  }
}
