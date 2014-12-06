package de.calette.mephisto3.util;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 */
public class ComponentUtil {
  /**
   * Creates an image canvas with the given width and height.
   */
  public static Canvas createImageCanvas(String url, double width, double height) {
    ImageView img = new ImageView(new Image(url, width, height, false, true));
    final Canvas canvas = new Canvas(width, height);
    final GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.drawImage(img.getImage(), 0, 0);

    return canvas;
  }

  public static Label createLabel(Object label, String cssClass, Pane parent) {
    Label l = new Label(String.valueOf(label));
    l.getStyleClass().add(cssClass);
    parent.getChildren().add(l);
    return l;
  }
}
