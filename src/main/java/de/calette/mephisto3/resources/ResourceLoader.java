package de.calette.mephisto3.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used for load images and stuff.
 */
public class ResourceLoader {
  private final static Logger LOG = LoggerFactory.getLogger(ResourceLoader.class);

  public static String getResource(String s) {
    try {
      return ResourceLoader.class.getResource(s).toExternalForm();
    } catch (Exception e) {
      LOG.error("Resource not found: " + s);
    }
    return null;
  }
}
