package de.calette.mephisto3.gpio;

import callete.api.Callete;
import callete.api.services.gpio.*;
import callete.api.util.SystemUtils;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

/**
 * The controller registered the hardware input components and delegates the events to the UI.
 */
public class GPIOController {
  private static GPIOController instance;

  private List<ControlListener> controlListeners = new ArrayList<>();


  public static GPIOController getInstance() {
    if (instance == null) {
      instance = new GPIOController();
      instance.init();
    }
    return instance;
  }

  public void addControlListener(ControlListener listener) {
    this.controlListeners.add(listener);
  }

  // ------------------- Helper -----------------------------------

  private void init() {
    GPIOService gpioService = Callete.getGPIOService();
    gpioService.setSimulationMode(SystemUtils.isWindows());

    PushButton pushButton = gpioService.connectPushButton(18, "Rotary Push");
    pushButton.addPushListener(new PushListener() {
      @Override
      public void pushed(final PushEvent pushEvent) {
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            ControlEvent push = ControlEvent.PUSH;
            if(pushEvent.isLongPush()) {
              push = ControlEvent.LONG_PUSH;
            }
            for (ControlListener listener : controlListeners) {
              listener.controlEvent(push);
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
      public void rotated(RotaryEncoderEvent event) {
        for (ControlListener listener : controlListeners) {
          if (event.rotatedLeft()) {
            listener.controlEvent(ControlEvent.NEXT);
          } else {
            listener.controlEvent(ControlEvent.PREVIOUS);
          }
        }
      }
    });
  }
}
