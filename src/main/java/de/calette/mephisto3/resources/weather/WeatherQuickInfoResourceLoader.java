package de.calette.mephisto3.resources.weather;

/**
 * Used for load images and stuff.
 */
public class WeatherQuickInfoResourceLoader {
  public static String getResource(String s) {
    return WeatherQuickInfoResourceLoader.class.getResource(s).toString();
  }

  public static java.io.InputStream getResourceAsStream(String s) {
    return WeatherQuickInfoResourceLoader.class.getResourceAsStream(s);
  }
}
