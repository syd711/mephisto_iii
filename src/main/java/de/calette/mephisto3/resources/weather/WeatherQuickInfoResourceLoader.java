package de.calette.mephisto3.resources.weather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used for load images and stuff.
 */
public class WeatherQuickInfoResourceLoader {
  private final static Logger LOG = LoggerFactory.getLogger(WeatherQuickInfoResourceLoader.class);

  public static String getResource(String s) {
    try {
      return WeatherQuickInfoResourceLoader.class.getResource(s).toString();
    }
    catch (NullPointerException e) {
      LOG.error("WeatherQuickInfoResourceLoader.class failed to load image icon " + s);
    }
    return null;
  }
}
