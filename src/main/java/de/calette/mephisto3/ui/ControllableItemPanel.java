package de.calette.mephisto3.ui;

import javafx.scene.Node;

/**
 * Common interface for panes that are used as controllable items.
 */
public interface ControllableItemPanel<T> {

  void deselect();

  void select();


  /**
   * Returns the factor used during selection scaling.
   */
  double getScaleFactor();

  /**
   * Returns the component to scale, which may differ from this component.
   */
  Node getScalingNode();

  /**
   * Delegation used for the node's user data.
   *
   * @return
   */
  T getUserData();

}
