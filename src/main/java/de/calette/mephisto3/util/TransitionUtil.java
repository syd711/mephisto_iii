package de.calette.mephisto3.util;

import javafx.animation.FadeTransition;
import javafx.animation.FadeTransitionBuilder;
import javafx.animation.ScaleTransition;
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
   * Creates a fade in effect without playing it
   */
  public static FadeTransition createInFader(Node node) {
    return createInFader(node, 2000);
  }

  public static FadeTransition createInFader(Node node, long duration) {
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(duration), node);
    fadeTransition.setFromValue(0);
    fadeTransition.setToValue(1);
    fadeTransition.setAutoReverse(false);
    return fadeTransition;
  }

  public static FadeTransition createOutFader(Node node, long duration) {
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(duration), node);
    fadeTransition.setFromValue(1);
    fadeTransition.setToValue(0);
    fadeTransition.setAutoReverse(false);
    return fadeTransition;
  }

  public static ScaleTransition createScaler(Node node, long duration) {
    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(duration), node);
    scaleTransition.setToX(1.3);
    scaleTransition.setToY(1.3);
    scaleTransition.setAutoReverse(false);
    return scaleTransition;
  }

}
