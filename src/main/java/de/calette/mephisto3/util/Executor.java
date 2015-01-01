package de.calette.mephisto3.util;

/**
 * Executes an asynchronous task
 */
public class Executor {

  public static Thread run(final Runnable r) {
    Thread t = new Thread(r);
    t.start();
    return t;
  }
}
