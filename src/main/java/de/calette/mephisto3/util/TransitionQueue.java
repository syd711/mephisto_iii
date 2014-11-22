package de.calette.mephisto3.util;

import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helper class that ensures the sequential execution
 * of transitions, while additional ones might be added.
 */
public class TransitionQueue {

  private List<Transition> transitionQueue = Collections.synchronizedList(new ArrayList<Transition>());
  private SequentialTransition sequentialTransition;
  private boolean running = false;

  public TransitionQueue(Node node) {
    sequentialTransition = new SequentialTransition(node);
  }

  public void addTransition(Transition transition) {
    this.transitionQueue.add(transition);
  }

  /**
   * Synchronized playback of the translate transitions.
   */
  public void play() {
    if(running) {
      return;
    }
    running = true;
    sequentialTransition.getChildren().clear();
    sequentialTransition.getChildren().addAll(transitionQueue.remove(0));
    sequentialTransition.setAutoReverse(false);
    sequentialTransition.play();
    sequentialTransition.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        running = false;
        if(!transitionQueue.isEmpty()) {
          play();
        }
      }
    });
  }
}
