package de.calette.mephisto3.ui.weather;

import callete.api.Callete;
import callete.api.services.ServiceModel;
import callete.api.services.weather.Weather;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.ui.ControllablePanel;

/**
 */
public class WeatherPanel extends ControllablePanel {

  public WeatherPanel() {
    super(Callete.getWeatherService().getWeather());
    setMinWidth(Mephisto3.WIDTH);

//    for (ServiceModel model : models) {
//      WeatherLocationPanel panel = new WeatherLocationPanel((Weather) model);
//      getChildren().add(panel);
//    }
  }
}
