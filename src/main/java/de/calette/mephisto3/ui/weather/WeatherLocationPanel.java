package de.calette.mephisto3.ui.weather;

import callete.api.services.weather.Weather;
import de.calette.mephisto3.Mephisto3;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 */
public class WeatherLocationPanel extends StackPane {

  public WeatherLocationPanel(Weather weather) {
    VBox root = new VBox(20);
    root.setPadding(new Insets(30, 30, 30, 30));
    root.setMinWidth(Mephisto3.WIDTH - 50);
    Text name = new Text(weather.getCity());
    name.getStyleClass().add("stream-name");

    root.getChildren().add(name);
    root.getChildren().add(new Text("\n"));
    root.getStyleClass().add("stream-panel");
    getChildren().add(root);
  }
}
