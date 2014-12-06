package de.calette.mephisto3.ui;

import de.calette.mephisto3.util.TransitionUtil;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

/**
 * Class to be extended from nodes that are used for ControllableSelectorPanels.
 */
public abstract class ControllableVBoxItemPanelBase extends VBox implements ControllableItemPanel {
  private ControllableSelectorPanel parentControl;

  public ControllableVBoxItemPanelBase(double margins, ControllableSelectorPanel parentControl, Object model) {
    super(margins);
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
}
