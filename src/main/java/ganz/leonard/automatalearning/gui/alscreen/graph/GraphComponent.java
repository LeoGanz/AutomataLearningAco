package ganz.leonard.automatalearning.gui.alscreen.graph;

import ganz.leonard.automatalearning.gui.RenderManager;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class GraphComponent<T> extends JPanel implements PropertyChangeListener {

  private BufferedImage img;

  public GraphComponent(RenderManager<T> renderManager) {
    renderManager.addPropertyChangeListener(this);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (img != null) {
      g.drawImage(img, 0, 0, this);
    }
  }

  @Override
  public Dimension getPreferredSize() {
    if (img != null) {
      return new Dimension(img.getWidth(), img.getHeight());
    } else {
      return new Dimension(RenderManager.MAX_IMAGE_WIDTH , RenderManager.IMAGE_HEIGHT);
    }
  }

  @Override
  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  @Override
  public Dimension getMaximumSize() {
    // without providing a maximum, the panel becomes larger than the image so the image is not
    // centered anymore
    return getPreferredSize();
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getPropertyName().equals(RenderManager.IMAGE_UPDATE_KEY)) {
      Object update = evt.getNewValue();
      if (!(update instanceof BufferedImage bufferedImage)) {
        throw new IllegalArgumentException("Change event contains unexpected data");
      }
      SwingUtilities.invokeLater(() -> updateWithImg(bufferedImage));
    } else if (evt.getPropertyName().equals(RenderManager.REBUILD_GUI_KEY)) {
      SwingUtilities.invokeLater(this::repaint);
    }
  }



  private void updateWithImg(BufferedImage img) {
    // Number of calls to this method not necessarily equals the number of repaints as swing might
    // drop repaints if they are too close together
    this.img = img;
    revalidate();
    repaint();
  }
}
