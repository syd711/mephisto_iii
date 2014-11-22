package de.calette.mephisto3.util;

import de.calette.mephisto3.ui.ControllablePanel;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Utility class for creating transitions with default values.
 */
public class TransitionUtil {

  public static final int FADER_DEFAULT = 200;

  /**
   * Creates a fade in effect without playing it
   */
  public static FadeTransition createInFader(Node node) {
    return createInFader(node, FADER_DEFAULT);
  }

  public static FadeTransition createInFader(Node node, long duration) {
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(duration), node);
    fadeTransition.setFromValue(0);
    fadeTransition.setToValue(1);
    applyDefaults(node, fadeTransition);
    return fadeTransition;
  }

  public static FadeTransition createOutFader(Node node, long duration) {
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(duration), node);
    fadeTransition.setFromValue(1);
    fadeTransition.setToValue(0);
    applyDefaults(node, fadeTransition);
    return fadeTransition;
  }

  public static FadeTransition createOutFader(Node node) {
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(FADER_DEFAULT), node);
    fadeTransition.setFromValue(1);
    fadeTransition.setToValue(0);
    applyDefaults(node, fadeTransition);
    return fadeTransition;
  }

  public static ScaleTransition createScaler(Node node, double factor) {
    ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(ControllablePanel.SCROLL_DURATION), node);
    scaleTransition.setToX(factor);
    scaleTransition.setToY(factor);
    applyDefaults(node, scaleTransition);
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
    applyDefaults(node, fadeTransition);
    return fadeTransition;
  }

  /**
   * Applies common settings for transitions and their nodes.
    * @param node the node the transition is working on
   * @param transition the transition to apply the defaults for
   */
  private static void applyDefaults(Node node, Transition transition) {
    transition.setAutoReverse(false);
    transition.setInterpolator(Interpolator.EASE_BOTH);

    //apply speed as default cache strategy.
    if(!node.getCacheHint().equals(CacheHint.SPEED)) {
      node.setCache(true);
      node.setCacheHint(CacheHint.SPEED);
    }
  }
}
