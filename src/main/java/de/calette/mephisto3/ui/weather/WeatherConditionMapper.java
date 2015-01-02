package de.calette.mephisto3.ui.weather;

import callete.api.services.weather.Weather;
import callete.api.services.weather.WeatherState;
import de.calette.mephisto3.resources.weather.WeatherQuickInfoResourceLoader;
import de.calette.mephisto3.resources.weather.forecast.WeatherForecastResourceLoader;

public class WeatherConditionMapper {

  public static String getWeatherQuickInfoIcon(Weather weather) {
    String name = weather.getWeatherState().toString().toLowerCase() + ".png";
    name = name.replaceAll("_", "-");
    return WeatherQuickInfoResourceLoader.getResource(name);
  }

  public static String getWeatherForecastIcon(Weather weather) {
    String name = weather.getWeatherState().toString().toLowerCase() + ".png";
    name = name.replaceAll("_", "-");
    return WeatherForecastResourceLoader.getResource(name);
  }

  public static String getWeatherConditionText(WeatherState state) {
    switch(state) {
      case CLOUDY: {
        return "wolkig";
      }
      case RAINY: {
        return "regnerisch";
      }
      case SNOW: {
        return "Schnee";
      }
      case SNOW_RAINY: {
        return "Schneeregen";
      }
      case STORMY: {
        return "stürmisch";
      }
      case SUNNY: {
        return "sonnig";
      }
      case SUNNY_CLOUDY: {
        return "leicht bewölkt";
      }
      case SUNNY_CLOUDS: {
        return "stark bewölkt";
      }
      case SUNNY_RAINY: {
        return "leichter Regen";
      }
      default: {
        return "-";
      }
    }
  }
}
