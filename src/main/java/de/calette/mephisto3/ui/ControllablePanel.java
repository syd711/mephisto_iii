package de.calette.mephisto3.ui;

import callete.api.services.ServiceModel;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.control.ServiceState;
import de.calette.mephisto3.util.TransitionQueue;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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


  private Transition inFader;
  private Transition outFader;


  private int scrollWidth = SCROLL_WIDTH;

  public ControllablePanel(List<? extends ServiceModel> models) {
    super(10);
    setOpacity(0);
    this.models = models;
    transitionQueue = new TransitionQueue(this);

    scrollTransition = TransitionUtil.createTranslateTransition(this, SCROLL_DURATION, 0);
    scrollTransition.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        ServiceController.getInstance().setControlEnabled(true);
      }
    });


    this.outFader = TransitionUtil.createOutFader(this);
    this.inFader = TransitionUtil.createInFader(this);
    this.inFader.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        ServiceController.getInstance().setControlEnabled(true);
      }
    });
  }

  public void rotatedLeft(ServiceState serviceState) {
    if(serviceState.getModels().isEmpty()) {
      return;
    }
    ServiceController.getInstance().setControlEnabled(false);
    int offset = scrollWidth;
    if(serviceState.getServiceIndex() == serviceState.getModels().size()-1) {
      offset = -(serviceState.getModels().size()-1)*scrollWidth;
    }
    scrollTransition.setByX(offset);
    transitionQueue.addTransition(scrollTransition);
    transitionQueue.play();
  }

  public void rotatedRight(ServiceState serviceState) {
    if(serviceState.getModels().isEmpty()) {
      return;
    }
    ServiceController.getInstance().setControlEnabled(false);
    int offset = -scrollWidth;
    if(serviceState.getServiceIndex() == 0) {
      offset = ((serviceState.getModels().size()-1)*scrollWidth);
    }
    scrollTransition.setByX(offset);
    transitionQueue.addTransition(scrollTransition);
    transitionQueue.play();
  }

  public void pushed() {

  }

  public void showPanel() {
    this.inFader.play();
  }

  public void hidePanel() {
    if(this.getOpacity() != 0) {
      this.outFader.play();
    }
  }
}
