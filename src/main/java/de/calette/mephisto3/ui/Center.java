package de.calette.mephisto3.ui;

import de.calette.mephisto3.control.ControlListener;
import de.calette.mephisto3.control.ServiceControlEvent;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.control.ServiceState;
import de.calette.mephisto3.ui.radio.RadioStationPanel;
import de.calette.mephisto3.ui.weather.WeatherPanel;
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

  public Center(BorderPane root) {
    this.root = root;
    stackPane = new StackPane();

    activeControlPanel = new RadioStationPanel();
    stackPane.getChildren().add(activeControlPanel);
    setCenter(stackPane);

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

    activeControlPanel = new WeatherPanel();
    stackPane.getChildren().add(activeControlPanel);
  }
}
