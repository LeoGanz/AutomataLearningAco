package ganz.leonard.automatalearning.gui.alscreen;

import ganz.leonard.automatalearning.automata.probability.FeedbackAutomaton;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class GraphComponent<T> extends JPanel implements PropertyChangeListener {

  private static final int IMAGE_HEIGHT = 500;
  private static final int FRAMES_TO_KEEP = 3;
  private final AutomataLearning<T> model;
  private final LinkedList<CompletableFuture<BufferedImage>> renderingQueue;
  private BufferedImage img;

  public GraphComponent(AutomataLearning<T> model) {
    this.model = model;
    model.addPropertyChangeListener(this);
    renderingQueue = new LinkedList<>();
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
      return super.getPreferredSize();
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
    update(); // switching to EDT is handled by update()
  }

  private void update() {
    FeedbackAutomaton<T> automaton = model.getUnlinkedAutomaton();

    CompletableFuture<BufferedImage> nextFrame =
        CompletableFuture.supplyAsync(() -> GraphRenderer.automatonToImg(automaton, IMAGE_HEIGHT));
    nextFrame.thenAccept(
        img -> {
          synchronized (renderingQueue) {
            while (renderingQueue.peek() != nextFrame) {
              try {
                renderingQueue.wait();
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
            renderingQueue.poll();
            SwingUtilities.invokeLater(() -> updateWithImg(img));
            renderingQueue.notifyAll();
          }
        });
    renderingQueue.add(nextFrame);

    //    cleanRenderingQueue();
  }

  private void cleanRenderingQueue() {
    // keep rendering queue small
    synchronized (renderingQueue) {
      int size = renderingQueue.size();
      if (size > FRAMES_TO_KEEP) {
        IntStream.range(0, size)
            .filter(
                i ->
                    (i == renderingQueue.size() - 1)
                        || (i > 0 && i % Math.round(size / (double) FRAMES_TO_KEEP) == 0))
            .forEach(i -> renderingQueue.get(i).cancel(true));

        renderingQueue.removeAll(
            renderingQueue.stream()
                .filter(CompletableFuture::isCancelled)
                .collect(Collectors.toSet()));
      }
    }
  }

  private synchronized void updateWithImg(BufferedImage img) {
    // Number of calls to this method not necessarily equals the number of repaints as swing might
    // drop repaints if they are to close together
    this.img = img;
    revalidate();
    repaint();
  }
}
