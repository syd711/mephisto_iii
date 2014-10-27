package de.calette.mephisto3.resources.weather.forecast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used for load images and stuff.
 */
public class WeatherForecastResourceLoader {
  private final static Logger LOG = LoggerFactory.getLogger(WeatherForecastResourceLoader.class);

  public static String getResource(String s) {
    try {
      return WeatherForecastResourceLoader.class.getResource(s).toString();
    }
    catch (NullPointerException e) {
      LOG.error("WeatherForecastResourceLoader.class failed to load image icon " + s);
    }
    return null;
  }
}
