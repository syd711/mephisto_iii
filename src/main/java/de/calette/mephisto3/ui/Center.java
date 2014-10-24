package de.calette.mephisto3.ui;

import callete.api.Callete;
import callete.api.services.Service;
import callete.api.services.music.MusicServiceAuthenticationException;
import de.calette.mephisto3.control.ControlListener;
import de.calette.mephisto3.control.ServiceControlEvent;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.control.ServiceState;
import de.calette.mephisto3.ui.radio.StreamsPanel;
import de.calette.mephisto3.ui.weather.WeatherPanel;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.effect.GaussianBlur;
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

  private StackPane stackPane;
  private ControllablePanel activeControlPanel;
  private ControllablePanel newControlPanel;
  private BorderPane root;
  private ServiceChooser serviceChooser;

  private Map<Service, ControllablePanel> servicePanels = new HashMap<>();

  public Center(BorderPane root) {
    this.root = root;
    stackPane = new StackPane();

    activeControlPanel = getServicePanel(ServiceController.getInstance().getServiceState());

    stackPane.getChildren().add(activeControlPanel);
    setCenter(stackPane);

    ServiceController.getInstance().addControlListener(this);
    ServiceController.getInstance().addServiceChangeListener(this);

    serviceChooser = new ServiceChooser(root);

    activeControlPanel.showPanel();
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
    newControlPanel = getServicePanel(serviceState);

    final FadeTransition outFader = TransitionUtil.createOutFader(activeControlPanel);
    outFader.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        stackPane.getChildren().removeAll(activeControlPanel);
        activeControlPanel = newControlPanel;
        stackPane.getChildren().add(activeControlPanel);
        activeControlPanel.showPanel();
      }
    });
    outFader.play();
  }

  private ControllablePanel getServicePanel(ServiceState state) {
    if(servicePanels.isEmpty()) {
      WeatherPanel weatherPanel = new WeatherPanel();
      StreamsPanel streamsPanel = new StreamsPanel();

      servicePanels.put(Callete.getWeatherService(), weatherPanel);
      servicePanels.put(Callete.getStreamingService(), streamsPanel);
    }

    //TODO
    new Thread() {
      @Override
      public void run() {
//        try {
//          Callete.getGoogleMusicService().authenticate();
//        } catch (MusicServiceAuthenticationException e) {
//          e.printStackTrace();
//        }
      }
    }.start();

    return servicePanels.get(state.getService());
  }
}
