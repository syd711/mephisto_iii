package de.calette.mephisto3.ui;

import callete.api.services.ServiceModel;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.control.ServiceState;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.*;

/**
 * Abstract superclass for all center panels
 * that are controlled via the rotary encoder.
 */
public abstract class ControllablePanel extends HBox {
  private static final int SCROLL_DURATION = 200;
  private static final int OFFSET = 10;
  protected List<? extends ServiceModel> models;


  private List<Transition> transitionQueue = Collections.synchronizedList(new ArrayList<Transition>());
  private final SequentialTransition sequentialTransition = new SequentialTransition(this);
  private boolean running = false;

  public ControllablePanel(List<? extends ServiceModel> models) {
    super(10);
    this.models = models;
  }

  public void rotatedLeft(ServiceState serviceState) {
    int offset = (Mephisto3.WIDTH+OFFSET);
    if(serviceState.getServiceIndex() == serviceState.getModels().size()-1) {
      offset = -((serviceState.getModels().size()-1)*(Mephisto3.WIDTH+OFFSET));
    }
    transitionQueue.add(createTranslateTransition(offset));
    playTransitions();
  }

  public void rotatedRight(ServiceState serviceState) {
    int offset = -(Mephisto3.WIDTH+OFFSET);
    if(serviceState.getServiceIndex() == 0) {
      offset = (serviceState.getModels().size()-1)*(Mephisto3.WIDTH+OFFSET);
    }
    transitionQueue.add(createTranslateTransition(offset));
    playTransitions();
  }


  public void pushed() {

  }

  // ------------------- Helper ------------------------------

  private TranslateTransition createTranslateTransition(int offset) {
    TranslateTransition tt = new TranslateTransition(Duration.millis(SCROLL_DURATION), this);
    tt.setByX(offset);
    tt.setAutoReverse(false);
    return tt;
  }

  /**
   * Synchronized playback of the translate transitions.
   */
  private void playTransitions() {
    if(running) {
      return;
    }
    running = true;
    sequentialTransition.getChildren().clear();
    sequentialTransition.getChildren().addAll(transitionQueue.remove(0));
    sequentialTransition.setAutoReverse(false);
    sequentialTransition.play();
    sequentialTransition.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        running = false;
        if(!transitionQueue.isEmpty()) {
          playTransitions();
        }
      }
    });
  }
}
