package ganz.leonard.automatalearning.gui;

import ganz.leonard.automatalearning.automata.probability.FeedbackAutomaton;
import ganz.leonard.automatalearning.gui.alscreen.GraphRenderer;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import ganz.leonard.automatalearning.learning.UpdateImportance;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import org.imgscalr.Scalr;

public class RenderManager<T> implements PropertyChangeListener {

  public static final String IMAGE_UPDATE_KEY = "RenderingUpdate";
  public static final String APPLIED_WORDS_UPDATE_KEY = "AppliedWordsUpdate";
  public static final String INPUT_WORDS_UPDATE_KEY = "InputWordsUpdate";
  public static final String QUEUE_DONE_KEY = "QueueDone";
  public static final int IMAGE_HEIGHT = 500;
  public static final int MAX_IMAGE_WIDTH = 650;
  private static final int MAX_QUEUE_SIZE_LOW_IMPORTANCE = 3;
  private static final int MAX_QUEUE_SIZE_MEDIUM_IMPORTANCE = 8;

  private final AutomataLearning<T> model;
  private final Queue<Frame> renderingQueue;
  private final PropertyChangeSupport pcs;
int framesRem = 0;
int imgCtr = 0;
private int nextFrameId = 0;

  public RenderManager(AutomataLearning<T> model) {
    this.model = model;
    model.addPropertyChangeListener(this);
    renderingQueue = new ConcurrentLinkedQueue<>();
    pcs = new PropertyChangeSupport(this);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getOldValue() instanceof UpdateImportance importance) {
      constructNewFrame(importance);
    } else {
      constructNewFrame(UpdateImportance.HIGH);
    }
  }

  public synchronized void constructNewFrame(UpdateImportance importance) {
    if ((importance == UpdateImportance.LOW
        && renderingQueue.size() > MAX_QUEUE_SIZE_LOW_IMPORTANCE)
        || (importance == UpdateImportance.MEDIUM
        && renderingQueue.size() > MAX_QUEUE_SIZE_MEDIUM_IMPORTANCE)) {
      return; // drop frame as queue is too full
    }

    FeedbackAutomaton<T> automaton = model.getUnlinkedAutomaton();
    int nrApplied = model.getNrAppliedWords();
    int nrTotal = model.getNrInputWords();

    CompletableFuture<BufferedImage> futureImg =
        CompletableFuture.supplyAsync(
            () -> {
              System.out.println("calc img");
              BufferedImage img = GraphRenderer.automatonToImg(automaton, IMAGE_HEIGHT);
              if (img.getWidth() > MAX_IMAGE_WIDTH) {
                img = Scalr.resize(img, MAX_IMAGE_WIDTH);
              }
              System.out.println("img done "+ imgCtr++);
              return img;
            }).orTimeout(2, TimeUnit.SECONDS);
    Frame nextFrame = new Frame(futureImg, nextFrameId++);
    futureImg.thenAccept(
        img -> {
          synchronized (renderingQueue) {
            while ((renderingQueue.peek() != null ? renderingQueue.peek().id : -1) < nextFrame.id) {
              try {
                renderingQueue.wait();
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
            renderingQueue.poll();
            System.out.println("removed frame " + framesRem++);
            notifyListeners(img, nrApplied, nrTotal);
            renderingQueue.notifyAll();
          }
        });
    renderingQueue.add(nextFrame);
    System.out.println("added frame " + (nextFrameId -1));
  }

  public void addPropertyChangeListener(PropertyChangeListener changeListener) {
    pcs.addPropertyChangeListener(changeListener);
  }

  private void notifyListeners(BufferedImage img, int nrApplied, int nrTotal) {
    System.out.println("update + " + renderingQueue.size());
    pcs.firePropertyChange(IMAGE_UPDATE_KEY, null, img);
    pcs.firePropertyChange(APPLIED_WORDS_UPDATE_KEY, null, nrApplied);
    pcs.firePropertyChange(INPUT_WORDS_UPDATE_KEY, null, nrTotal);
    if (renderingQueue.isEmpty()) {
      pcs.firePropertyChange(QUEUE_DONE_KEY, null, null);
    }
  }
  
  private static record Frame (
      CompletableFuture<BufferedImage> future,
      int id
    ){
  }
}
