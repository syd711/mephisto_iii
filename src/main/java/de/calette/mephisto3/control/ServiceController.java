package de.calette.mephisto3.control;

import callete.api.Callete;
import callete.api.services.Service;
import callete.api.services.ServiceModel;
import callete.api.services.gpio.*;
import callete.api.util.SystemUtils;
import de.calette.mephisto3.ui.ServiceChangeListener;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 */
public class ServiceController {
  public final static String SERVICE_NAME_RADIO = "Radio";
  public final static String SERVICE_NAME_WEATHER = "Wetter";
  public final static String SERVICE_NAME_MUSIC = "Musik";
  public final static String SERVICE_NAME_SETTINGS = "System";

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

  /**
   * Activates the UI for the given service.
   * @param service the service to activate.
   */
  public void switchService(Service service) {
    serviceState.setService(Callete.getWeatherService());
    serviceState.setModels(Collections.<ServiceModel>emptyList());

    if(service.equals(Callete.getWeatherService())) {
      serviceState.setService(Callete.getWeatherService());
      serviceState.setModels(Callete.getWeatherService().getWeather());
    }
    else if(service.equals(Callete.getStreamingService())) {
      serviceState.setService(Callete.getStreamingService());
      serviceState.setModels(Callete.getStreamingService().getStreams());
    }
    else if(service.equals(Callete.getGoogleMusicService())) {
      serviceState.setService(Callete.getGoogleMusicService());
      serviceState.setModels(Callete.getGoogleMusicService().getAlbums());
    }
    else if(service.equals(Callete.getSystemService())) {
      serviceState.setService(Callete.getSystemService());
    }

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

    int pin = Callete.getConfiguration().getInt("rotary.encoder.push.pin");
    PushButton pushButton = gpioService.connectPushButton(pin, "Rotary Push");
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
        return Callete.getConfiguration().getInt("push.debounce.millis", 700);
      }

      @Override
      public long getLongPushDebounceMillis() {
        return Callete.getConfiguration().getInt("long.push.debounce.millis", 1500);
      }
    });

    int pinA = Callete.getConfiguration().getInt("rotary.encoder.pin.a");
    int pinB = Callete.getConfiguration().getInt("rotary.encoder.pin.b");
    RotaryEncoder rotary = gpioService.connectRotaryEncoder(pinA, pinB, "Rotary Encoder");
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
