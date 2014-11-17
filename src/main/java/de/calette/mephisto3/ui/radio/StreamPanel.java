package de.calette.mephisto3.ui.radio;

import callete.api.services.music.model.Stream;
import de.calette.mephisto3.Mephisto3;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 */
public class StreamPanel extends StackPane {

  public StreamPanel(Stream stream) {
    VBox root = new VBox(20);
    root.setPadding(new Insets(30, 30, 30, 30));
    root.setMinWidth(Mephisto3.WIDTH);
    Label name = new Label("Antenne Voralberg");
    name.getStyleClass().add("stream-name");
    Label artist = new Label("The fabulous baker boys");
    artist.getStyleClass().add("stream-artist");
    Label title = new Label("Sweet Child O Mine");
    title.getStyleClass().add("stream-title");
    Label url = new Label(stream.getPlaybackUrl());
    url.getStyleClass().add("stream-url");

    root.getChildren().add(name);
    root.getChildren().add(title);
    root.getChildren().add(artist);
    root.getChildren().add(new Text("\n"));
    root.getChildren().add(url);
    root.getStyleClass().add("stream-panel");
    getChildren().add(root);
  }
}
