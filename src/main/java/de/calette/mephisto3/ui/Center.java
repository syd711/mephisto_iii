package de.calette.mephisto3.ui;

import callete.api.Callete;
import de.calette.mephisto3.control.ControlEvent;
import de.calette.mephisto3.control.ControlListener;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.control.ServiceState;
import de.calette.mephisto3.ui.radio.RadioStationPanel;
import de.calette.mephisto3.ui.weather.WeatherPanel;
import javafx.geometry.Insets;
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

  public Center() {
    setPadding(new Insets(5, 15, 15, 15));
    stackPane = new StackPane();

    activeControlPanel = new RadioStationPanel();
    stackPane.getChildren().add(activeControlPanel);
    setCenter(stackPane);

    ServiceController.getInstance().addControlListener(this);
    ServiceController.getInstance().addServiceChangeListener(this);
  }

  @Override
  public void controlEvent(ControlEvent event) {
    if(event.equals(ControlEvent.LONG_PUSH)) {
      //handle feature switch
//      Text test = new Text("test");
//      test.getStyleClass().add("stream-name");
//      stackPane.getChildren().add(test);
      ServiceController.getInstance().updateServiceState(Callete.getWeatherService());
    }
    else if(event.equals(ControlEvent.NEXT)) {
      activeControlPanel.rotatedRight();
    }
    else if(event.equals(ControlEvent.PREVIOUS)) {
      activeControlPanel.rotatedLeft();
    }
    else if(event.equals(ControlEvent.PUSH)) {
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
