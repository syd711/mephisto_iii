package de.calette.mephisto3.util;

import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * Dumps the component tree and their CSS styles.
 */
public class CSSDebugger {

  public static void dump(Node n) {
    dump(n, 0);
  }

  private static void dump(Node n, int depth) {
    for (int i = 0; i < depth; i++) System.out.print("  ");
    System.out.println(n);
    if (n instanceof Parent)
      for (Node c : ((Parent) n).getChildrenUnmodifiable())
        dump(c, depth + 1);
  }
}
