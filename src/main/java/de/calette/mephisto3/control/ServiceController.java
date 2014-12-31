package de.calette.mephisto3.control;

import callete.api.Callete;
import callete.api.services.Service;
import callete.api.services.ServiceModel;
import callete.api.services.gpio.*;
import callete.api.services.impl.simulator.Simulator;
import callete.api.services.impl.simulator.SimulatorPushButton;
import callete.api.services.impl.simulator.SimulatorRotaryEncoder;
import callete.api.services.music.model.Stream;
import de.calette.mephisto3.ui.ServiceChangeListener;
import de.calette.mephisto3.util.Executor;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Registers the GPIO components and their listeners.
 * Switches the service states.
 */
public class ServiceController {
  public final static String SERVICE_NAME_RADIO = "Radio";
  public final static String SERVICE_NAME_WEATHER = "Wetter";
  public final static String SERVICE_NAME_MUSIC = "Musik";
  public final static String SERVICE_NAME_SETTINGS = "System";

  public static final String ROTARY_ENCODER_PUSH_BUTTON_NAME = "Rotary Push";
  public static final String ROTARY_ENCODER_NAME = "Rotary Encoder";

  private static ServiceController instance;

  private List<ServiceChangeListener> serviceChangeListeners = Collections.synchronizedList(new ArrayList<ServiceChangeListener>());
  private List<ControlListener> controlListeners = Collections.synchronizedList(new ArrayList<ControlListener>());
  private ServiceState serviceState = new ServiceState();
  private boolean controlEnabled = false;

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

  public void removeControlListener(ControlListener listener) {
    this.controlListeners.remove(listener);
  }

  /**
   * Activates the UI for the given service.
   *
   * @param service the service to activate.
   */
  public void switchService(Service service) {
    Executor.run(new Runnable() {
      @Override
      public void run() {
        if (service.equals(Callete.getWeatherService())) {
          serviceState.setService(Callete.getWeatherService());
          serviceState.setModels(Callete.getWeatherService().getWeather());
        }
        else if (service.equals(Callete.getStreamingService())) {
          serviceState.setService(Callete.getStreamingService());
          serviceState.setModels(Callete.getStreamingService().getStreams());
        }
        else if (service.equals(Callete.getGoogleMusicService())) {
          serviceState.setService(Callete.getGoogleMusicService());
          serviceState.setModels(Callete.getGoogleMusicService().getAlbums());
        }
        else if (service.equals(Callete.getSystemService())) {
          serviceState.setService(Callete.getSystemService());
          serviceState.setModels(Collections.<ServiceModel>emptyList());
        }

        serviceChanged();
      }
    });
  }

  public void setControlEnabled(boolean b) {
    this.controlEnabled = b;
  }

  public ServiceState getServiceState() {
    return serviceState;
  }

  // ------------------- Helper -----------------------------------
  public void serviceChanged() {
    for (ServiceChangeListener listener : serviceChangeListeners) {
      listener.serviceChanged(serviceState);
    }
  }

  /**
   * Sets the initial service state, therefore the initial service.
   */
  private void initServiceState() {
    serviceState.setService(Callete.getStreamingService());
    List<Stream> streams = Callete.getStreamingService().getStreams();
    serviceState.setModels(streams);

    //update index with last save state
    int index = Callete.getSettings().getInt(ServiceState.SETTING_SERVICE_SELECTION, 0);
    if(index>=streams.size()) {
      index = 0;
    }
    serviceState.setServiceIndex(index);
  }

  private void initGPIO() {
    GPIOService gpioService = Callete.getGPIOService();

    int pin = Callete.getConfiguration().getInt("rotary.encoder.push.pin");
    PushButton pushButton = gpioService.connectPushButton(pin, ROTARY_ENCODER_PUSH_BUTTON_NAME);
    pushButton.addPushListener(new PushListener() {
      @Override
      public void pushed(final PushEvent pushEvent) {
        if(!controlEnabled) {
          return;
        }
        Executor.run(new Runnable() {
          @Override
          public void run() {
            ServiceControlEvent.EVENT_TYPE push = ServiceControlEvent.EVENT_TYPE.PUSH;

            if (pushEvent.isLongPush()) {
              serviceState.setModels(null);
              push = ServiceControlEvent.EVENT_TYPE.LONG_PUSH;
            }

            //update listeners
            for (ControlListener listener : new ArrayList<>(controlListeners)) {
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
    RotaryEncoder rotary = gpioService.connectRotaryEncoder(pinA, pinB, ROTARY_ENCODER_NAME);
    rotary.addChangeListener(new RotaryEncoderListener() {
      @Override
      public void rotated(final RotaryEncoderEvent event) {
        if(!controlEnabled) {
          return;
        }
        ServiceControlEvent.EVENT_TYPE eventType;
        if (event.rotatedLeft()) {
          eventType = ServiceControlEvent.EVENT_TYPE.PREVIOUS;
        }
        else {
          eventType = ServiceControlEvent.EVENT_TYPE.NEXT;
        }

        //skip this in service chooser mode
        if(serviceState.getModels() != null) {
          if (eventType.equals(ServiceControlEvent.EVENT_TYPE.PREVIOUS)) {
            serviceState.decrementIndex();
          }
          else if (eventType.equals(ServiceControlEvent.EVENT_TYPE.NEXT)) {
            serviceState.incrementIndex();
          }
        }


        final ServiceControlEvent serviceControlEvent = new ServiceControlEvent(eventType, serviceState);
        for (ControlListener listener : controlListeners) {
          listener.controlEvent(serviceControlEvent);
        }
      }
    });
  }

  public void fireControlEvent(KeyCode code) {
    SimulatorRotaryEncoder encoder = (SimulatorRotaryEncoder) Simulator.getInstance().getGpioComponent(ROTARY_ENCODER_NAME);
    SimulatorPushButton pushButton = (SimulatorPushButton) Simulator.getInstance().getGpioComponent(ROTARY_ENCODER_PUSH_BUTTON_NAME);

    if (code == KeyCode.RIGHT) {
      encoder.right();
    }
    else if (code == KeyCode.LEFT) {
      encoder.left();
    }
    else if (code == KeyCode.DOWN) {
      pushButton.push(false);
    }
    else if (code == KeyCode.UP) {
      pushButton.push(true);
    }
  }
}
