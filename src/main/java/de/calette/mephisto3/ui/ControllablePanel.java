package de.calette.mephisto3.ui;

import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.control.ServiceState;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;

/**
 * Abstract superclass for all center panels
 * that are controlled via the rotary encoder.
 */
public abstract class ControllablePanel extends StackPane {

  private Transition inFader;
  private Transition outFader;

  public ControllablePanel() {
    setOpacity(0);
    setMinWidth(Mephisto3.WIDTH);
    getStyleClass().add("controllable-panel");
    setAlignment(Pos.TOP_LEFT);

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
    serviceStateChanged(serviceState);
  }

  public void rotatedRight(ServiceState serviceState) {
    if(serviceState.getModels().isEmpty()) {
      return;
    }
    serviceStateChanged(serviceState);
  }

  /**
   * Maybe implemented by subclasses if push event is used.
   *
   * @param serviceState the current selection state.
   */
  public void pushed(ServiceState serviceState) {
  }

  protected void serviceStateChanged(ServiceState serviceState) {

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
