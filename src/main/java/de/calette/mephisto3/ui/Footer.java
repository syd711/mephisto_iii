package de.calette.mephisto3.ui;

import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.BorderPane;

/**
 * The footer only contains the scrollbar, indicating
 * the position of the current selection, e.g. for radio stations.
 */
public class Footer extends BorderPane {
  private ScrollBar sc;

  public Footer() {
    setMaxWidth(Mephisto3.WIDTH);
    setOpacity(0);
    sc = new ScrollBar();
    sc.setOrientation(Orientation.HORIZONTAL);
    //this will hide the already invisible scroll buttons
    sc.setPadding(new Insets(0, -20, 0, -20));
    sc.setId("scroller");
    sc.setMin(0);
    sc.setMax(50);
    sc.setVisibleAmount(20);

    sc.setValue(0);
    setCenter(sc);

    show();
  }

  public void show() {
    TransitionUtil.createInFader(this).play();
  }
}
