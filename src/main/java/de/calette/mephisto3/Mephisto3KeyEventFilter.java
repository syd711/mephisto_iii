package de.calette.mephisto3;

import de.calette.mephisto3.control.ServiceController;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

/**
 */
public class Mephisto3KeyEventFilter implements EventHandler<KeyEvent> {
  @Override
  public void handle(KeyEvent keyEvent) {
    ServiceController.getInstance().fireControlEvent(keyEvent.getCode());
  }
}
