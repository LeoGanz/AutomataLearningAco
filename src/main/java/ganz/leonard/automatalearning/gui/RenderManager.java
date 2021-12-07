package ganz.leonard.automatalearning.gui;

import ganz.leonard.automatalearning.automata.probability.FeedbackAutomaton;
import ganz.leonard.automatalearning.gui.alscreen.GraphRenderer;
import ganz.leonard.automatalearning.gui.util.LinearColorGradient;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import ganz.leonard.automatalearning.learning.UpdateImportance;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizV8Engine;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import org.imgscalr.Scalr;

public class RenderManager<T> implements PropertyChangeListener {

  public static final String IMAGE_UPDATE_KEY = "RenderingUpdate";
  public static final String APPLIED_WORDS_UPDATE_KEY = "AppliedWordsUpdate";
  public static final String INPUT_WORDS_UPDATE_KEY = "InputWordsUpdate";
  public static final String REBUILD_GUI_KEY = "Rebuild";
  public static final String REGEX_UPDATE_KEY = "RegexUpdate";
  public static final String SCORE_UPDATE_KEY = "ScoreUpdate";
  public static final int IMAGE_HEIGHT = 500;
  public static final int MAX_IMAGE_WIDTH = 650;
  private static final int MAX_QUEUE_SIZE_LOW_IMPORTANCE = 3;
  private static final int MAX_QUEUE_SIZE_MEDIUM_IMPORTANCE = 8;
  private static final long DELAY = 5000; // ms
  private static final LinearColorGradient GRADIENT = new LinearColorGradient();

  private final AutomataLearning<T> model;
  private final Queue<Frame> renderingQueue;
  private final PropertyChangeSupport pcs;
  private final Timer timer;
  private final GraphRenderer graphRenderer;
  private int nextFrameId = 0;

  public RenderManager(AutomataLearning<T> model) {
    this.model = model;
    model.addPropertyChangeListener(this);
    renderingQueue = new ConcurrentLinkedQueue<>();
    pcs = new PropertyChangeSupport(this);
    timer = new Timer(true);
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        unstuckQueue();
      }
    }, DELAY, DELAY);
    graphRenderer = new GraphRenderer(getGradient());
  }

  private void unstuckQueue() {
    pcs.firePropertyChange(REBUILD_GUI_KEY, null, null);
    Frame peek = renderingQueue.peek();
    if (peek != null) {
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          Frame sndPeek = renderingQueue.peek();
          if (sndPeek != null && peek.id == sndPeek.id) {
            // queue seems to be stuck
            System.out.println("Trying to unstuck rendering queue and engine");
            clearQueue();
            Graphviz.releaseEngine();
            try {
              Thread.sleep(200);
            } catch (InterruptedException ignored) {
              // ignore
            }
            Graphviz.useEngine(new GraphvizV8Engine());
            constructNewFrame(UpdateImportance.HIGH);
          }
        }
      }, DELAY / 2);
    }
  }

  private void clearQueue() {
    renderingQueue.forEach(frame -> frame.future.cancel(true));
    renderingQueue.clear();
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getOldValue() instanceof UpdateImportance importance) {
      constructNewFrame(importance);
    } else {
      constructNewFrame(UpdateImportance.HIGH);
    }
  }

  public void constructNewFrame(UpdateImportance importance) {
    if ((importance == UpdateImportance.LOW
        && renderingQueue.size() > MAX_QUEUE_SIZE_LOW_IMPORTANCE
      ) || (importance == UpdateImportance.MEDIUM
        && renderingQueue.size() > MAX_QUEUE_SIZE_MEDIUM_IMPORTANCE
      )) {
      return; // drop frame as queue is too full
    }

    FeedbackAutomaton<T> automaton = model.getUnlinkedAutomaton();
    int nrApplied = model.getNrAppliedWords();
    int nrTotal = model.getNrInputWords();
    String regex = model.getLanguageRegex();
    double score = model.getIntermediateResult().score();

    CompletableFuture<BufferedImage> futureImg =
        CompletableFuture.supplyAsync(
            () -> {
              BufferedImage img = graphRenderer.automatonToImg(automaton,  IMAGE_HEIGHT);
              if (img.getWidth() > MAX_IMAGE_WIDTH) {
                img = Scalr.resize(img, MAX_IMAGE_WIDTH);
              }
              return img;
            }).orTimeout(DELAY, TimeUnit.MILLISECONDS);
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
            notifyListeners(img, nrApplied, nrTotal, regex, score);
            renderingQueue.notifyAll();
          }
        });
    renderingQueue.add(nextFrame);
  }

  public LinearColorGradient getGradient() {
    return GRADIENT;
  }

  public void addPropertyChangeListener(PropertyChangeListener changeListener) {
    pcs.addPropertyChangeListener(changeListener);
  }

  private void notifyListeners(BufferedImage img, int nrApplied, int nrTotal,
                               String regex, double score) {
    pcs.firePropertyChange(IMAGE_UPDATE_KEY, null, img);
    pcs.firePropertyChange(APPLIED_WORDS_UPDATE_KEY, null, nrApplied);
    pcs.firePropertyChange(INPUT_WORDS_UPDATE_KEY, null, nrTotal);
    pcs.firePropertyChange(REGEX_UPDATE_KEY, null, regex);
    pcs.firePropertyChange(SCORE_UPDATE_KEY, null, score);
  }

  public void stop() {
    timer.cancel();
    clearQueue();
  }

  private static record Frame(
      CompletableFuture<BufferedImage> future,
      int id
  ) {
  }
}
