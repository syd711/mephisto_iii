package de.calette.mephisto3;

import de.calette.mephisto3.control.ServiceController;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Key event listener for controlling the UI via keyboard.
 */
public class Mephisto3KeyEventFilter implements EventHandler<KeyEvent> {
  private final static Logger LOG = LoggerFactory.getLogger(Mephisto3KeyEventFilter.class);
  
  @Override
  public void handle(KeyEvent keyEvent) {
    LOG.info("Firing key code event for key " + keyEvent.getCode().getName());
    ServiceController.getInstance().fireControlEvent(keyEvent.getCode());
  }
}
