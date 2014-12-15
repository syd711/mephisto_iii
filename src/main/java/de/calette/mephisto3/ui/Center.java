package de.calette.mephisto3.ui;

import callete.api.Callete;
import callete.api.services.Service;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.control.ControlListener;
import de.calette.mephisto3.control.ServiceControlEvent;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.control.ServiceState;
import de.calette.mephisto3.ui.google.GoogleMusicPanel;
import de.calette.mephisto3.ui.radio.StreamsPanel;
import de.calette.mephisto3.ui.system.SystemPanel;
import de.calette.mephisto3.ui.weather.WeatherPanel;
import de.calette.mephisto3.util.Executor;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;

/**
 * The center panel which is a stackpane so that
 * the control overlay can be displayed.
 *
 * The center region is replaced with ControllablePanel instances.
 */
public class Center extends BorderPane implements ControlListener, ServiceChangeListener {

  protected StackPane stackPane;
  protected ControllablePanel activeControlPanel;
  private ControllablePanel newControlPanel;
  private ServiceChooser serviceChooser;

  private Map<Service, ControllablePanel> servicePanels = new HashMap<>();

  public Center() {
    setMaxWidth(Mephisto3.WIDTH);
    setMaxHeight(Mephisto3.HEIGHT);

    stackPane = new StackPane();
    serviceChooser = new ServiceChooser(this);
    activeControlPanel = getServicePanel(ServiceController.getInstance().getServiceState());

    stackPane.getChildren().add(activeControlPanel);
    setCenter(stackPane);

    ServiceController.getInstance().addControlListener(this);
    ServiceController.getInstance().addServiceChangeListener(this);

    activeControlPanel.showPanel();

    loadServices();
  }

  @Override
  public void controlEvent(ServiceControlEvent event) {
    if(event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.LONG_PUSH)) {
      serviceChooser.showServiceChooser();
    }
    else if(event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.NEXT)) {
      activeControlPanel.rotatedRight(event.getServiceState());
    }
    else if(event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PREVIOUS)) {
      activeControlPanel.rotatedLeft(event.getServiceState());
    }
    else if(event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PUSH)) {
      activeControlPanel.pushed(event.getServiceState());
    }
  }


  @Override
  public void serviceChanged(ServiceState serviceState) {
    newControlPanel = getServicePanel(serviceState);

    if(!activeControlPanel.equals(newControlPanel)) {
      activeControlPanel.hidePanel();
      stackPane.setOpacity(0);
      final FadeTransition outFader = TransitionUtil.createOutFader(activeControlPanel);
      outFader.setOnFinished(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
          stackPane.getChildren().removeAll(activeControlPanel);
          activeControlPanel = newControlPanel;
          stackPane.getChildren().add(activeControlPanel);
          activeControlPanel.showPanel();
          TransitionUtil.createInFader(stackPane).play();
        }
      });
      outFader.play();
    }
    else {
      activeControlPanel.showPanel();
    }
  }


  //----------------- Helper ------------------------------

  private ControllablePanel getServicePanel(ServiceState state) {
    if(servicePanels.isEmpty()) {
      StreamsPanel streamsPanel = new StreamsPanel();
      servicePanels.put(Callete.getStreamingService(), streamsPanel);
      serviceChooser.addService(ServiceController.SERVICE_NAME_RADIO, Callete.getStreamingService());
    }

    return servicePanels.get(state.getService());
  }

  private void loadServices() {
    Executor.run(new Runnable() {
      @Override
      public void run() {
        WeatherPanel weatherPanel = new WeatherPanel();
        servicePanels.put(Callete.getWeatherService(), weatherPanel);
        serviceChooser.addService(ServiceController.SERVICE_NAME_WEATHER, Callete.getWeatherService());

        SystemPanel systemPanel = new SystemPanel();
        servicePanels.put(Callete.getSystemService(), systemPanel);
        serviceChooser.addService(ServiceController.SERVICE_NAME_SETTINGS, Callete.getSystemService());

        GoogleMusicPanel googleMusicPanel = new GoogleMusicPanel();
        servicePanels.put(Callete.getGoogleMusicService(), googleMusicPanel);
        serviceChooser.addService(ServiceController.SERVICE_NAME_MUSIC, Callete.getGoogleMusicService());
      }
    });
  }
}
