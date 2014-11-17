package de.calette.mephisto3.ui;

import callete.api.Callete;
import callete.api.services.Service;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.control.ControlListener;
import de.calette.mephisto3.control.ServiceControlEvent;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.control.ServiceState;
import de.calette.mephisto3.util.TransitionQueue;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * The chooser that selects the active function of the radio.
 */
public class ServiceChooser implements ControlListener {

  public static final int SERVICE_BOX_WIDTH = 160;
  public static final double SELECTION_SCALE_FACTOR = 1.6;
  public static final int DISPLAY_DELAY = 500;

  private int index = 0;
  private List<Text> serviceBoxes = new ArrayList<>();

  private Transition showFader;
  private Transition hideFader;
  private TranslateTransition scrollTransition;

  private TransitionQueue transitionQueue;
  private HBox overlay;
  private Center center;
  private boolean visible = false;

  public ServiceChooser(final Center center) {
    this.center = center;

    overlay = new HBox();
    overlay.setOpacity(0);
    center.stackPane.getChildren().add(overlay);

    overlay.setAlignment(Pos.TOP_CENTER);
    overlay.setId("chooser");
    overlay.setMinWidth(Mephisto3.WIDTH);
    overlay.setMinHeight(80);

    final HBox scroller = new HBox();
    scroller.setPadding(new Insets(0, 0, 0, 480));
    scroller.setAlignment(Pos.CENTER);
    scroller.setCache(true);
    scroller.setCacheHint(CacheHint.SPEED);

    transitionQueue = new TransitionQueue(scroller);

    scroller.getChildren().add(createServiceBox(ServiceController.SERVICE_NAME_RADIO, Callete.getStreamingService()));
    scroller.getChildren().add(createServiceBox(ServiceController.SERVICE_NAME_WEATHER, Callete.getWeatherService()));
    scroller.getChildren().add(createServiceBox(ServiceController.SERVICE_NAME_MUSIC, Callete.getGoogleMusicService()));
    scroller.getChildren().add(createServiceBox(ServiceController.SERVICE_NAME_SETTINGS, Callete.getSystemService()));

    overlay.getChildren().add(scroller);

    ServiceController.getInstance().addControlListener(this);

    scrollTransition = new TranslateTransition(Duration.millis(ControllablePanel.SCROLL_DURATION), scroller);
    scrollTransition.setAutoReverse(false);

    showFader = TransitionUtil.createInFader(overlay, DISPLAY_DELAY);
    hideFader = TransitionUtil.createOutFader(overlay, DISPLAY_DELAY);
    hideFader.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        ServiceController.getInstance().addControlListener(center);
        visible = false;
      }
    });
  }

  @Override
  public void controlEvent(ServiceControlEvent event) {
    if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.LONG_PUSH)) {
      ServiceController.getInstance().removeControlListener(center);
      if(!visible) {
        visible = true;
        center.activeControlPanel.hidePanel();
        show();
      }
    }
    else if (visible && event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PUSH)) {
      ServiceController.getInstance().setControlEnabled(false);


      final FadeTransition blink = TransitionUtil.createBlink(serviceBoxes.get(index));
      blink.setOnFinished(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
          closeServiceChooser();
        }
      });

      final Service service = (Service) serviceBoxes.get(index).getUserData();
      final ServiceState serviceState = ServiceController.getInstance().getServiceState();
      if(!serviceState.getService().equals(service)) {
        blink.play();
      }
      else {
        closeServiceChooser();
      }
    }

    if(visible) {
      if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.NEXT)) {
        scroll(-SERVICE_BOX_WIDTH);
      } else if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PREVIOUS)) {
        scroll(SERVICE_BOX_WIDTH);
      }
    }
  }

  // --------------- Helper -----------------------------

  /**
   * Closes the chooser, resets the UI state.
   */
  private void closeServiceChooser() {
    final Service service = (Service) serviceBoxes.get(index).getUserData();
    hideFader.play();
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        ServiceController.getInstance().switchService(service);
      }
    });
  }

  private void show() {
    showFader.play();
    TransitionUtil.createScaler(serviceBoxes.get(index), ControllablePanel.SCROLL_DURATION, SELECTION_SCALE_FACTOR).play();
  }

  private void scroll(final int width) {
    if (index == serviceBoxes.size() - 1 && width < 0) {
      return;
    }
    if (index == 0 && width > 0) {
      return;
    }

    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                          scrollTransition.setByX(width);
                          transitionQueue.addTransition(scrollTransition);
                          transitionQueue.play();
                        }
                      }
    );


    TransitionUtil.createScaler(serviceBoxes.get(index), ControllablePanel.SCROLL_DURATION, 1.0).play();
    if (width > 0) {
      index--;
    } else {
      index++;
    }

    TransitionUtil.createScaler(serviceBoxes.get(index), ControllablePanel.SCROLL_DURATION, SELECTION_SCALE_FACTOR).play();
  }

  private HBox createServiceBox(String label, Service service) {
    Text text = new Text(label);
    text.setCache(true);
    text.setCacheHint(CacheHint.SPEED);
    text.setUserData(service);
    serviceBoxes.add(text);
    text.getStyleClass().add("service-name");
    HBox box = new HBox();
    box.setAlignment(Pos.CENTER);
    box.setMinWidth(SERVICE_BOX_WIDTH);
    box.getChildren().add(text);
    return box;
  }
}
