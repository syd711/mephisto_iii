package de.calette.mephisto3.control;

import callete.api.Callete;
import callete.api.services.Service;
import callete.api.services.gpio.*;
import callete.api.util.SystemUtils;
import de.calette.mephisto3.ui.ServiceChangeListener;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class ServiceController {
  private static ServiceController instance;

  private List<ServiceChangeListener> serviceChangeListeners = new ArrayList<>();
  private List<ControlListener> controlListeners = new ArrayList<>();
  private ServiceState serviceState = new ServiceState();

  public static ServiceController getInstance() {
    if (instance == null) {
      instance = new ServiceController();
      instance.initGPIO();
      instance.initServiceState();
    }
    return instance;
  }

  public void addServiceChangeListener(ServiceChangeListener listener) {
    this.serviceChangeListeners.add(listener);
  }

  public void addControlListener(ControlListener listener) {
    this.controlListeners.add(listener);
  }

  public void updateServiceState(Service service) {
    serviceState.setService(Callete.getWeatherService());
    serviceState.setModels(Callete.getWeatherService().getWeather());
    serviceChanged();
  }

  public ServiceState getServiceState() {
    return serviceState;
  }

  // ------------------- Helper -----------------------------------
  private void serviceChanged() {
    for(ServiceChangeListener listener : serviceChangeListeners) {
      listener.serviceChanged(serviceState);
    }
  }

  private void initServiceState() {
    serviceState.setService(Callete.getStreamingService());
    serviceState.setModels(Callete.getStreamingService().getStreams());
    serviceChanged();
  }

  private void initGPIO() {
    GPIOService gpioService = Callete.getGPIOService();
    gpioService.setSimulationMode(SystemUtils.isWindows());

    PushButton pushButton = gpioService.connectPushButton(18, "Rotary Push");
    pushButton.addPushListener(new PushListener() {
      @Override
      public void pushed(final PushEvent pushEvent) {
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            ServiceControlEvent.EVENT_TYPE push = ServiceControlEvent.EVENT_TYPE.PUSH;
            if (pushEvent.isLongPush()) {
              push = ServiceControlEvent.EVENT_TYPE.LONG_PUSH;
            }
            for (ControlListener listener : controlListeners) {
              listener.controlEvent(new ServiceControlEvent(push, serviceState));
            }
          }
        });

      }

      @Override
      public long getPushDebounceMillis() {
        return 700;
      }

      @Override
      public long getLongPushDebounceMillis() {
        return 1500;
      }
    });

    RotaryEncoder rotary = gpioService.connectRotaryEncoder(12, 16, "Rotary Encoder");
    rotary.addChangeListener(new RotaryEncoderListener() {
      @Override
      public void rotated(final RotaryEncoderEvent event) {
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            ServiceControlEvent.EVENT_TYPE eventType = ServiceControlEvent.EVENT_TYPE.PREVIOUS;
            if (event.rotatedLeft()) {
              serviceState.decrementIndex();
            } else {
              eventType = ServiceControlEvent.EVENT_TYPE.NEXT;
              serviceState.incrementIndex();
            }

            final ServiceControlEvent serviceControlEvent = new ServiceControlEvent(eventType, serviceState);
            for (ControlListener listener : controlListeners) {
              listener.controlEvent(serviceControlEvent);
            }
          }
        });
      }
    });
  }
}
