package de.calette.mephisto3.control;

/**
 * Enum that represents the different input methods provided by a single rotary encoder.
 */
public class ServiceControlEvent {
  public static enum EVENT_TYPE {PUSH, LONG_PUSH, NEXT, PREVIOUS};

  private ServiceState state;
  private EVENT_TYPE eventType;

  public ServiceControlEvent(EVENT_TYPE eventType, ServiceState state) {
    this.state = state;
    this.eventType = eventType;
  }

  public EVENT_TYPE getEventType() {
    return eventType;
  }

  public ServiceState getServiceState() {
    return state;
  }
}
