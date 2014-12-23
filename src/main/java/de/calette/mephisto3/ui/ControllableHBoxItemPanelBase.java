package de.calette.mephisto3.ui;

import de.calette.mephisto3.util.TransitionUtil;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

/**
 * Class to be extended from nodes that are used for ControllableSelectorPanels.
 */
public abstract class ControllableHBoxItemPanelBase<T> extends HBox implements ControllableItemPanel {

  private ControllableSelectorPanel parentControl;
  private T model;

  public ControllableHBoxItemPanelBase(double margins, ControllableSelectorPanel parentControl, T model) {
    super(margins);
    this.model = model;
    this.setUserData(model);
    this.parentControl = parentControl;
  }

  public void deselect() {
    TransitionUtil.createScaler(getScalingNode(), 1.0).play();
  }

  public void select() {
    TransitionUtil.createScaler(getScalingNode(), getScaleFactor()).play();
  }


  /**
   * Returns the component to scale, which may differ from this component.
   */
  public Node getScalingNode() {
    return this;
  }

  protected ControllableSelectorPanel getParentControlPanel() {
    return parentControl;
  }

  protected T getModel() {
    return model;
  }
}
