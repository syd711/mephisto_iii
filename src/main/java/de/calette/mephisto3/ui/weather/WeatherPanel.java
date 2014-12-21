package de.calette.mephisto3.ui.weather;

import callete.api.Callete;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.ui.ControllablePanel;

/**
 */
public class WeatherPanel extends ControllablePanel {

  public WeatherPanel() {
    super(Callete.getWeatherService().getWeather());
    setMinWidth(Mephisto3.WIDTH);

    initUI();
  }

  // ------------------- Helper ----------------

  private void initUI() {

  }
}
