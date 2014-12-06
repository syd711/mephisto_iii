package de.calette.mephisto3.ui;

import de.calette.mephisto3.resources.menu.MenuResourceLoader;
import de.calette.mephisto3.util.ComponentUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;

/**
 * Used to display a back button
 */
public class BackButtonBox extends ControllableVBoxItemPanelBase {

  private double scaleFactor = 1.05;
  private Canvas backButton;

  public BackButtonBox(int width, int backTopPadding) {
    super(10, null, null);
    setMinWidth(width);
    setPadding(new Insets(backTopPadding, 0, 0, 0));
    setAlignment(Pos.BASELINE_RIGHT);
    scaleFactor = 1.2;
    //well, the image is not larger than 100px
    if (width > 100) {
      width = 100;
    }
    backButton = ComponentUtil.createImageCanvas(MenuResourceLoader.getResource("back.png"), width, width);
    getChildren().add(backButton);
  }

  @Override
  public Node getScalingNode() {
    return backButton;
  }

  @Override
  public double getScaleFactor() {
    return scaleFactor;
  }
}
