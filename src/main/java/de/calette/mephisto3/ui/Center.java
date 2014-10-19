package de.calette.mephisto3.ui;

import callete.api.Callete;
import callete.api.services.music.MusicServiceAuthenticationException;
import de.calette.mephisto3.control.ControlListener;
import de.calette.mephisto3.control.ServiceControlEvent;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.control.ServiceState;
import de.calette.mephisto3.ui.radio.StreamsPanel;
import de.calette.mephisto3.ui.weather.WeatherPanel;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

/**
 * The center panel which is a stackpane so that
 * the control overlay can be displayed.
 *
 * The center region is replaced with ControllablePanel instances.
 */
public class Center extends BorderPane implements ControlListener, ServiceChangeListener {

  private StackPane stackPane;
  private ControllablePanel activeControlPanel;
  private BorderPane root;
  private ServiceChooser serviceChooser;

  private WeatherPanel weatherPanel;
  private StreamsPanel streamsPanel;

  public Center(BorderPane root) {
    this.root = root;
    stackPane = new StackPane();

    streamsPanel = new StreamsPanel();
    activeControlPanel = streamsPanel;
    stackPane.getChildren().add(activeControlPanel);
    setCenter(stackPane);

    new Thread() {
      @Override
      public void run() {
        try {
          weatherPanel = new WeatherPanel();

          Callete.getGoogleMusicService().authenticate();
        } catch (MusicServiceAuthenticationException e) {
          e.printStackTrace();
        }
      }
    }.start();

    ServiceController.getInstance().addControlListener(this);
    ServiceController.getInstance().addServiceChangeListener(this);

    serviceChooser = new ServiceChooser(root);
  }

  @Override
  public void controlEvent(ServiceControlEvent event) {
    //action are delegated to the function chooser
    if(serviceChooser.visible()) {
      return;
    }

    if(event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.LONG_PUSH)) {
      root.setEffect(new GaussianBlur(18));
      serviceChooser.show();
    }
    else if(event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.NEXT)) {
      activeControlPanel.rotatedRight(event.getServiceState());
    }
    else if(event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PREVIOUS)) {
      activeControlPanel.rotatedLeft(event.getServiceState());
    }
    else if(event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PUSH)) {
      activeControlPanel.pushed();
    }
  }

  @Override
  public void serviceChanged(ServiceState serviceState) {
    stackPane.getChildren().removeAll(activeControlPanel);

    if(serviceState.getService().equals(Callete.getStreamingService())) {
      activeControlPanel = streamsPanel;
    }
    else if(serviceState.getService().equals(Callete.getWeatherService())) {
      activeControlPanel = weatherPanel;
    }
    activeControlPanel.setOpacity(0);
    stackPane.getChildren().add(activeControlPanel);

    TransitionUtil.createInFader(activeControlPanel).play();
  }
}
