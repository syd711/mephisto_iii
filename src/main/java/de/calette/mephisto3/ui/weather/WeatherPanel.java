package de.calette.mephisto3.ui.weather;

import callete.api.Callete;
import callete.api.services.ServiceModel;
import callete.api.services.weather.Weather;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.ui.ControllablePanel;
import de.calette.mephisto3.util.TransitionUtil;

/**
 */
public class WeatherPanel extends ControllablePanel {
  public WeatherPanel() {
    super(Callete.getWeatherService().getWeather());
    setMinWidth(Mephisto3.WIDTH);
  }

  @Override
  public void showPanel() {
    TransitionUtil.createInFader(this).play();

    for (ServiceModel model : models) {
      WeatherLocationPanel panel = new WeatherLocationPanel((Weather) model);
      getChildren().add(panel);
      panel.show();
    }
  }

  @Override
  public void hidePanel() {
    this.setOpacity(0);
    for (ServiceModel model : models) {
      WeatherLocationPanel panel = new WeatherLocationPanel((Weather) model);
      getChildren().add(panel);
      panel.hidePanel();
    }
  }
}
