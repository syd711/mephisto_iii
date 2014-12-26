package de.calette.mephisto3.ui;

import callete.api.services.Service;
import de.calette.mephisto3.util.ComponentUtil;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Service box created for each service that is registered
 * in the ServiceChooser.
 */
public class ServiceNameBox extends VBox {
  public static final double SELECTION_SCALE_FACTOR = 1.5;
  public static final int SERVICE_BOX_WIDTH = 160;
  private Text text;

  public ServiceNameBox(String label, Service service) {
    super(20);

    setUserData(service);
    setPadding(new Insets(80, 0, 0, 0));
    setMinHeight(200);
    setOpacity(0);
    setAlignment(Pos.TOP_CENTER);
    setMinWidth(SERVICE_BOX_WIDTH);

    text = ComponentUtil.createText(label, "service-name", this);
  }

  public void deselect() {
    TransitionUtil.createScaler(text, 1.0).play();
  }

  public void select() {
    TransitionUtil.createScaler(text, SELECTION_SCALE_FACTOR).play();
  }
}
