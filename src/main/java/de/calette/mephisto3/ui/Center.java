package de.calette.mephisto3.ui;

import callete.api.Callete;
import callete.api.services.Service;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.control.ControlListener;
import de.calette.mephisto3.control.ServiceControlEvent;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.control.ServiceState;
import de.calette.mephisto3.ui.google.GoogleMusicPanel;
import de.calette.mephisto3.ui.radio.StreamsController;
import de.calette.mephisto3.ui.system.SystemPanel;
import de.calette.mephisto3.ui.weather.WeatherPanel;
import de.calette.mephisto3.util.Executor;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * The center panel which is a stackpane so that
 * the control overlay can be displayed.
 * <p/>
 * The center region is replaced with ControllablePanel instances.
 */
public class Center extends BorderPane implements ControlListener, ServiceChangeListener {
  private final static Logger LOG = LoggerFactory.getLogger(Center.class);

  protected StackPane stackPane;
  protected ControllablePanel activeControlPanel;
  private ControllablePanel newControlPanel;
  private ServiceChooser serviceChooser;

  private Map<Service, ControllablePanel> servicePanels = new HashMap<>();
  private static Center instance;
  
  public static Center getInstance() {
    return instance;    
  }

  public Center() {
    setMaxWidth(Mephisto3.WIDTH);
    setMaxHeight(Mephisto3.HEIGHT);
    
    stackPane = new StackPane();

    serviceChooser = new ServiceChooser(this);
    StreamsController streamsController = new StreamsController();
    servicePanels.put(Callete.getStreamingService(), streamsController);
    serviceChooser.addService(Callete.getStreamingService());

    activeControlPanel = getServicePanel(ServiceController.getInstance().getServiceState());
    stackPane.getChildren().add(activeControlPanel);
    setCenter(stackPane);


    ServiceController.getInstance().addControlListener(this);
    ServiceController.getInstance().addServiceChangeListener(this);
    ServiceController.getInstance().serviceChanged();
    
    instance = this;
  }

  @Override
  public void controlEvent(ServiceControlEvent event) {
    if(event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.LONG_PUSH)) {
      serviceChooser.showServiceChooser();
    }
    else if(event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PUSH)) {
      activeControlPanel.pushed(event.getServiceState());
    }
    else if(event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.NEXT)) {
      activeControlPanel.rotatedRight(event.getServiceState());
    }
    else if(event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PREVIOUS)) {
      activeControlPanel.rotatedLeft(event.getServiceState());
    }

  }


  @Override
  public void serviceChanged(ServiceState serviceState) {
    newControlPanel = getServicePanel(serviceState);

    Platform.runLater(new Runnable() {
      @Override
      public void run() {
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
    });
  }


  //----------------- Helper ------------------------------

  private ControllablePanel getServicePanel(ServiceState state) {
    return servicePanels.get(state.getService());
  }

  public void loadServices() {
    Executor.run(new Runnable() {
      @Override
      public void run() {
        Thread.currentThread().setName("Service Initializer");
        LOG.debug("Added service chooser for System");
        SystemPanel systemPanel = new SystemPanel();
        servicePanels.put(Callete.getSystemService(), systemPanel);
        serviceChooser.addService(Callete.getSystemService());

        LOG.debug("Added service chooser for Google");
        GoogleMusicPanel googleMusicPanel = new GoogleMusicPanel();
        servicePanels.put(Callete.getGoogleMusicService(), googleMusicPanel);
        serviceChooser.addService(Callete.getGoogleMusicService());

        LOG.debug("Added service chooser for Weather");
        WeatherPanel weatherPanel = new WeatherPanel();
        servicePanels.put(Callete.getWeatherService(), weatherPanel);
        serviceChooser.addService(Callete.getWeatherService());
      }
    });
  }
}
