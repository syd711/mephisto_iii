package de.calette.mephisto3.ui.weather;

import callete.api.services.weather.Weather;
import de.calette.mephisto3.resources.weather.WeatherQuickInfoResourceLoader;

public class WeatherIconMapper {

  public static String getWeatherQuickInfoIcon(Weather weather) {
    String name = weather.getWeatherState().toString().toLowerCase() +  ".png";
    name = name.replaceAll("_", "-");
    return WeatherQuickInfoResourceLoader.getResource(name);
  }
}
