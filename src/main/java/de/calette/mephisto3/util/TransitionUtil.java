package de.calette.mephisto3.util;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
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

  public static final int FADER_DEFAULT = 200;

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
    return createInFader(node, 1000);
  }

  public static FadeTransition createInFader(Node node, long duration) {
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(duration), node);
    fadeTransition.setFromValue(0);
    fadeTransition.setToValue(1);
    fadeTransition.setAutoReverse(false);
    fadeTransition.setInterpolator(Interpolator.EASE_BOTH);
    return fadeTransition;
  }

  public static FadeTransition createOutFader(Node node, long duration) {
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(duration), node);
    fadeTransition.setFromValue(1);
    fadeTransition.setToValue(0);
    fadeTransition.setAutoReverse(false);
    fadeTransition.setInterpolator(Interpolator.EASE_BOTH);
    return fadeTransition;
  }

  public static FadeTransition createOutFader(Node node) {
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(FADER_DEFAULT), node);
    fadeTransition.setFromValue(1);
    fadeTransition.setToValue(0);
    fadeTransition.setAutoReverse(false);
    fadeTransition.setInterpolator(Interpolator.EASE_BOTH);
    return fadeTransition;
  }

  public static ScaleTransition createScaler(Node node, long duration, double factor) {
    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(duration), node);
    scaleTransition.setToX(factor);
    scaleTransition.setToY(factor);
    scaleTransition.setAutoReverse(false);
    scaleTransition.setInterpolator(Interpolator.EASE_BOTH);
    return scaleTransition;
  }

  /**
   * Creates a blink out effect without playing it
   */
  public static FadeTransition createBlink(Node node) {
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(80), node);
    fadeTransition.setFromValue(0.1);
    fadeTransition.setCycleCount(3);
    fadeTransition.setToValue(1);
    fadeTransition.setAutoReverse(true);
    fadeTransition.setInterpolator(Interpolator.EASE_BOTH);
    return fadeTransition;
  }

}
