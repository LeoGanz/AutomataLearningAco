package ganz.leonard.automatalearning.gui;

import ganz.leonard.automatalearning.automata.general.Automaton;
import ganz.leonard.automatalearning.automata.general.State;
import ganz.leonard.automatalearning.gui.alscreen.graph.GraphRenderer;
import ganz.leonard.automatalearning.gui.util.LinearColorGradient;
import ganz.leonard.automatalearning.learning.AutomataLearning;
import ganz.leonard.automatalearning.learning.IntermediateResult;
import ganz.leonard.automatalearning.learning.UpdateImportance;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.engine.GraphvizV8Engine;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Map;
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
  public static final String NEXT_WORD_UPDATE_KEY = "NextWordUpdate";
  public static final String MIN_DFA_PROB_UPDATE = "MinDfaProbUpdate";
  public static final String SHOW_BEST_UPDATE_KEY = "ShowBestUpdate";
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
  private IntermediateResult<T> bestDfa;
  private Mode mode = Mode.RENDER_CURRENT;

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

  public void constructNewFrame(UpdateImportance importance) {
    if ((importance == UpdateImportance.LOW
        && renderingQueue.size() > MAX_QUEUE_SIZE_LOW_IMPORTANCE
      ) || (importance == UpdateImportance.MEDIUM
        && renderingQueue.size() > MAX_QUEUE_SIZE_MEDIUM_IMPORTANCE
      )) {
      return; // drop frame as queue is too full
    }

    if (bestDfa == null || model.getBestResult().score() > bestDfa.score()) {
      bestDfa = model.getBestResult();
      if (mode == Mode.DROP_FRAME_UNLESS_BETTER) {
        mode = Mode.RENDER_BEST;
      }
    }
    if (mode == Mode.DROP_FRAME_UNLESS_BETTER) {
      return; // drop frame as no improvement was made
    }

    Automaton<? extends State<?, T>, T> automaton = mode == Mode.RENDER_CURRENT
        ? model.getUnlinkedAutomaton()
        : model.getBestResult().automaton();
    int nrApplied = mode == Mode.RENDER_CURRENT
        ? model.getNrAppliedWords()
        : model.getBestResult().nrAppliedWords();
    String regex = mode == Mode.RENDER_CURRENT
        ? model.getLanguageRegex()
        : model.getLanguageRegex(model.getBestResult().automaton());
    double score = mode == Mode.RENDER_CURRENT
        ? model.getIntermediateResult().score()
        : model.getBestResult().score();
    Map.Entry<List<T>, Boolean> nextWord = model.peekNextInputWord();
    int nrTotal = model.getNrInputWords();
    double minDfaProb = model.getMinDfaProb();

    if (mode == Mode.RENDER_BEST) {
      mode = Mode.DROP_FRAME_UNLESS_BETTER;
      // best dfa does not need to be rerendered on model changes if no better one is found
    }

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
            notifyListeners(img, nextWord, nrApplied, nrTotal, regex, score, minDfaProb);
            renderingQueue.notifyAll();
          }
        });
    renderingQueue.add(nextFrame);
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

  public boolean isShowBestAutomatonOnly() {
    return mode == Mode.RENDER_BEST || mode == Mode.DROP_FRAME_UNLESS_BETTER;
  }

  public void setShowBestAutomatonOnly(boolean selected) {
    mode = selected ? Mode.RENDER_BEST : Mode.RENDER_CURRENT;
    pcs.firePropertyChange(SHOW_BEST_UPDATE_KEY, null, isShowBestAutomatonOnly());
    constructNewFrame(UpdateImportance.HIGH);
  }

  private void clearQueue() {
    renderingQueue.forEach(frame -> frame.future.cancel(true));
    renderingQueue.clear();
  }

  public void stop() {
    timer.cancel();
    clearQueue();
  }

  public LinearColorGradient getGradient() {
    return GRADIENT;
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (evt.getOldValue() instanceof UpdateImportance importance) {
      constructNewFrame(importance);
    } else {
      constructNewFrame(UpdateImportance.HIGH);
    }
  }

  public void addPropertyChangeListener(PropertyChangeListener changeListener) {
    pcs.addPropertyChangeListener(changeListener);
  }

  private void notifyListeners(BufferedImage img,
                               Map.Entry<List<T>, Boolean> nextWord,
                               int nrApplied, int nrTotal,
                               String regex, double score, double minDfaProb) {
    pcs.firePropertyChange(IMAGE_UPDATE_KEY, null, img);
    pcs.firePropertyChange(NEXT_WORD_UPDATE_KEY, null, nextWord);
    pcs.firePropertyChange(APPLIED_WORDS_UPDATE_KEY, null, nrApplied);
    pcs.firePropertyChange(INPUT_WORDS_UPDATE_KEY, null, nrTotal);
    pcs.firePropertyChange(REGEX_UPDATE_KEY, null, regex);
    pcs.firePropertyChange(SCORE_UPDATE_KEY, null, score);
    pcs.firePropertyChange(MIN_DFA_PROB_UPDATE, null, minDfaProb);
  }

  private enum Mode {
    RENDER_CURRENT,
    RENDER_BEST,
    DROP_FRAME_UNLESS_BETTER,
  }

  private static record Frame(
      CompletableFuture<BufferedImage> future,
      int id
  ) {
  }
}
