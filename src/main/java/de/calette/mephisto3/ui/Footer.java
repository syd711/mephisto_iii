package de.calette.mephisto3.ui;

import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.control.ControlListener;
import de.calette.mephisto3.control.ServiceControlEvent;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.control.ServiceState;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.BorderPane;

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
    sc.setMax(serviceState.getModels().size()-1);
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
    setVisible(!serviceState.getModels().isEmpty());

    sc.setMax(serviceState.getModels().size() - 1);
    if(serviceState.getModels().isEmpty()) {
      sc.setMax(0);
    }
    //TODO
//    sc.setValue(serviceState.getServiceIndex());
    sc.setValue(0);
  }

  @Override
  public void controlEvent(ServiceControlEvent event) {
    final ServiceControlEvent.EVENT_TYPE eventType = event.getEventType();
    if(eventType.equals(ServiceControlEvent.EVENT_TYPE.NEXT) || eventType.equals(ServiceControlEvent.EVENT_TYPE.PREVIOUS)) {
      final int serviceIndex = event.getServiceState().getServiceIndex();
      sc.setValue(serviceIndex);
    }
    else if(eventType.equals(ServiceControlEvent.EVENT_TYPE.LONG_PUSH)) {
      this.setVisible(false);
    }
    else if(eventType.equals(ServiceControlEvent.EVENT_TYPE.PUSH)) {
      this.setVisible(true);
    }
  }
}
