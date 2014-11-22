package de.calette.mephisto3.ui;

import de.calette.mephisto3.util.TransitionUtil;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 * Class to be extended from nodes that are used for ControllableSelectorPanels.
 */
public abstract class ControllableItemPanel extends VBox {

  public ControllableItemPanel(double margins, Object model) {
    super(margins);
    this.setUserData(model);
  }

  public void deselect() {
    TransitionUtil.createScaler(this, 1.0).play();
  }

  public void select() {
    TransitionUtil.createScaler(this, getScaleFactor()).play();
  }


  /**
   * Returns the factor used during selection scaling.
   */
  protected abstract double getScaleFactor();

  /**
   * Returns the component to scale, which may differ from this component.
   */
  protected Node getScalingNode() {
    return this;
  }
}
