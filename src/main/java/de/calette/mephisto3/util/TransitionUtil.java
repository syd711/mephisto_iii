package de.calette.mephisto3.util;

import javafx.animation.FadeTransition;
import javafx.animation.FadeTransitionBuilder;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Common UI helpers.
 */
public class TransitionUtil {

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

  /**
   * Creates a fade out effect without playing it
   */
  public static FadeTransition createInFader(Node node) {
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000), node);
    fadeTransition.setFromValue(0);
    fadeTransition.setToValue(1);
    fadeTransition.setAutoReverse(false);
    return fadeTransition;
  }
}
