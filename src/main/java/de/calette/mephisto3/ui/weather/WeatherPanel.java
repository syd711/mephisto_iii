package de.calette.mephisto3.ui.weather;

import callete.api.Callete;
import callete.api.services.ServiceModel;
import callete.api.services.weather.Weather;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.ui.ControllablePanel;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 */
public class WeatherPanel extends ControllablePanel {

  public WeatherPanel() {
    super(Callete.getWeatherService().getWeather());
    setMinWidth(Mephisto3.WIDTH);

    for (ServiceModel model : models) {
      WeatherLocationPanel panel = new WeatherLocationPanel((Weather) model);
      getChildren().add(panel);
    }
  }
}
