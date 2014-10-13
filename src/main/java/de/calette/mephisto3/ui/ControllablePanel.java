package de.calette.mephisto3.ui;

import callete.api.services.ServiceModel;
import de.calette.mephisto3.Mephisto3;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.List;

/**
 * Abstract superclass for all center panels
 * that are controlled via the rotary encoder.
 */
public abstract class ControllablePanel extends HBox {
  protected List<? extends ServiceModel> models;

  public ControllablePanel(List<? extends ServiceModel> models) {
    super(10);
    this.models = models;
  }

  public void rotatedLeft() {
    int offset = Mephisto3.WIDTH-40;

    TranslateTransition tt = new TranslateTransition(Duration.millis(500), this);
    tt.setByX(offset);
    tt.setAutoReverse(false);
    tt.play();
  }

  public void rotatedRight() {
    int offset = -Mephisto3.WIDTH+40;
    TranslateTransition tt = new TranslateTransition(Duration.millis(500), this);
    tt.setByX(offset);
    tt.setAutoReverse(false);
    tt.play();
  }

  public void pushed() {

  }
}
