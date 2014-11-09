package de.calette.mephisto3.ui;

import callete.api.services.ServiceModel;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.control.ServiceState;
import de.calette.mephisto3.util.TransitionQueue;
import javafx.animation.TranslateTransition;
import javafx.scene.CacheHint;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.List;

/**
 * Abstract superclass for all center panels
 * that are controlled via the rotary encoder.
 */
public abstract class ControllablePanel extends HBox {
  public static final int SCROLL_DURATION = 200;
  private static final int OFFSET = 10;
  private static final int SCROLL_WIDTH = Mephisto3.WIDTH+OFFSET;
  protected List<? extends ServiceModel> models;
  private TransitionQueue transitionQueue;
  private TranslateTransition scrollTransition;


  public ControllablePanel(List<? extends ServiceModel> models) {
    super(10);
    setOpacity(0);
    this.models = models;
    transitionQueue = new TransitionQueue(this);
    setCache(true);
    setCacheHint(CacheHint.SPEED);

    scrollTransition = new TranslateTransition(Duration.millis(SCROLL_DURATION), this);
    scrollTransition.setAutoReverse(false);
  }

  public void rotatedLeft(ServiceState serviceState) {
    int offset = SCROLL_WIDTH;
    if(serviceState.getServiceIndex() == serviceState.getModels().size()-1) {
      offset = -((serviceState.getModels().size()-1)*SCROLL_WIDTH);
    }
    scrollTransition.setByX(offset);
    transitionQueue.addTransition(scrollTransition);
    transitionQueue.play();
  }

  public void rotatedRight(ServiceState serviceState) {
    int offset = -SCROLL_WIDTH;
    if(serviceState.getServiceIndex() == 0) {
      offset = (serviceState.getModels().size()-1)*SCROLL_WIDTH;
    }
    scrollTransition.setByX(offset);
    transitionQueue.addTransition(scrollTransition);
    transitionQueue.play();
  }


  public void pushed() {

  }

  abstract public void showPanel();

  abstract public void hidePanel();
}
