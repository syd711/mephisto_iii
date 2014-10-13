package de.calette.mephisto3.control;

/**
 * Interface for control listener.
 */
public interface ControlListener {

  /**
   * Fired when the user has inputted something.
   * The event type contains the type of input event.
   */
  void controlEvent(ControlEvent event);
}
