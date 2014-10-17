package de.calette.mephisto3.ui;

import callete.api.Callete;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.control.ServiceControlEvent;
import de.calette.mephisto3.control.ControlListener;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.control.ServiceState;
import de.calette.mephisto3.ui.radio.RadioStationPanel;
import de.calette.mephisto3.ui.weather.WeatherPanel;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;

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
  private FunctionChooser functionChooser;

  public Center(BorderPane root) {
    this.root = root;
    stackPane = new StackPane();

    activeControlPanel = new RadioStationPanel();
    stackPane.getChildren().add(activeControlPanel);
    setCenter(stackPane);

    ServiceController.getInstance().addControlListener(this);
    ServiceController.getInstance().addServiceChangeListener(this);

    functionChooser = new FunctionChooser(root);
  }

  @Override
  public void controlEvent(ServiceControlEvent event) {
    //action are delegated to the function chooser
    if(functionChooser.visible()) {
      return;
    }

    if(event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.LONG_PUSH)) {
      root.setEffect(new GaussianBlur(18));
      functionChooser.show();
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
