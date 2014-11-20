package de.calette.mephisto3.resources.menu;

/**
 * Used for load images and stuff.
 */
public class MenuResourceLoader {
  public static String getResource(String s) {
    return MenuResourceLoader.class.getResource(s).toExternalForm();
  }
}
