package de.calette.mephisto3.ui;

import callete.api.services.Service;
import callete.api.services.ServiceModel;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.control.ControlEvent;
import de.calette.mephisto3.control.ControlListener;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.control.ServiceState;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.BorderPane;

import java.util.List;

/**
 * The footer only contains the scrollbar, indicating
 * the position of the current selection, e.g. for radio stations.
 */
public class Footer extends BorderPane implements ServiceChangeListener, ControlListener {
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

    ServiceState serviceState = ServiceController.getInstance().getServiceState();
    sc.setMax(serviceState.getModels().size());
    sc.setVisibleAmount(0.9);
    sc.setValue(0);

    setCenter(sc);

    ServiceController.getInstance().addServiceChangeListener(this);
    ServiceController.getInstance().addControlListener(this);
    show();
  }

  public void show() {
    TransitionUtil.createInFader(this).play();
  }


  @Override
  public void serviceChanged(ServiceState serviceState) {
    sc.setMax(serviceState.getModels().size() - 1);
    sc.setValue(serviceState.getServiceIndex());
  }

  @Override
  public void controlEvent(ControlEvent event) {
    if(event.equals(ControlEvent.NEXT)) {
      sc.setValue(sc.getValue()+1);
    }
    else if(event.equals(ControlEvent.PREVIOUS)) {
      sc.setValue(sc.getValue()-1);
    }
  }
}
