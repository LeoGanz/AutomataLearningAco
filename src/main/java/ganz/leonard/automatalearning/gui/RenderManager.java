package ganz.leonard.automatalearning.gui;

import ganz.leonard.automatalearning.automata.probability.FeedbackAutomaton;
import ganz.leonard.automatalearning.gui.alscreen.GraphRenderer;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.imgscalr.Scalr;

public class RenderManager<T> implements PropertyChangeListener {

  public static final String IMAGE_UPDATE_KEY = "RenderingUpdate";
  public static final String APPLIED_WORDS_UPDATE_KEY = "AppliedWordsUpdate";
  public static final String INPUT_WORDS_UPDATE_KEY = "InputWordsUpdate";
  public static final String QUEUE_DONE_KEY = "QueueDone";
  public static final int IMAGE_HEIGHT = 500;
  public static final int MAX_IMAGE_WIDTH = 650;

  private final AutomataLearning<T> model;
  private final Queue<CompletableFuture<BufferedImage>> renderingQueue;
  private final PropertyChangeSupport pcs;

  public RenderManager(AutomataLearning<T> model) {
    this.model = model;
    model.addPropertyChangeListener(this);
    renderingQueue = new ConcurrentLinkedQueue<>();
    pcs = new PropertyChangeSupport(this);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    constructNewFrame();
  }

  public void constructNewFrame() {
    FeedbackAutomaton<T> automaton = model.getUnlinkedAutomaton();
    int nrApplied = model.getNrAppliedWords();
    int nrTotal = model.getNrInputWords();

    CompletableFuture<BufferedImage> nextFrame =
        CompletableFuture.supplyAsync(
            () -> {
              BufferedImage img = GraphRenderer.automatonToImg(automaton, IMAGE_HEIGHT);
              if (img.getWidth() > MAX_IMAGE_WIDTH) {
                img = Scalr.resize(img, MAX_IMAGE_WIDTH);
              }
              return img;
            });
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
            notifyListeners(img, nrApplied, nrTotal);
            renderingQueue.notifyAll();
          }
        });
    renderingQueue.add(nextFrame);
  }

  public void addPropertyChangeListener(PropertyChangeListener changeListener) {
    pcs.addPropertyChangeListener(changeListener);
  }

  private void notifyListeners(BufferedImage img, int nrApplied, int nrTotal) {
    pcs.firePropertyChange(IMAGE_UPDATE_KEY, null, img);
    pcs.firePropertyChange(APPLIED_WORDS_UPDATE_KEY, null, nrApplied);
    pcs.firePropertyChange(INPUT_WORDS_UPDATE_KEY, null, nrTotal);
    if (renderingQueue.isEmpty()) {
      pcs.firePropertyChange(QUEUE_DONE_KEY, null, null);
    }
  }
}
