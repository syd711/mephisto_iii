package de.calette.mephisto3.util;

import de.calette.mephisto3.ui.ControllablePanel;
import javafx.animation.*;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
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
   * Expands the width of the given Pane to the target width.
   * @param node the pane to expand
   * @param offset the offset width of the pane
   */
  public static Transition createMaxWidth(final Pane node, final int originalWidth, final int offset, final boolean increase) {
    final Transition transition = new Transition() {
      {
        setCycleDuration(Duration.millis(200));
      }
      @Override
      protected void interpolate(double v) {
        double percent = v*100;
        double newWidthOffset = offset*percent/100;

        if(increase) {
          node.setMinWidth(originalWidth+newWidthOffset);
        }
        else {
          node.setMinWidth(originalWidth-newWidthOffset);
        }
      }
    };
    transition.setAutoReverse(false);
    transition.setInterpolator(Interpolator.EASE_BOTH);
    applyDefaults(node, transition);
    return transition;
  }

  /**
   * Translate transition used for scrolling
   */
  public static TranslateTransition createTranslateTransition(Node node, long duration, int width) {
    TranslateTransition translateTransition = new TranslateTransition(Duration.millis(duration), node);
    translateTransition.setByX(width);
    applyDefaults(node, translateTransition);
    return translateTransition;
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
