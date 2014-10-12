package de.calette.mephisto3.ui;

import callete.api.services.music.model.Stream;
import de.calette.mephisto3.Mephisto3;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 */
public class StreamPanel extends StackPane {

  public StreamPanel(Stream stream) {
    VBox root = new VBox(20);
    root.setPadding(new Insets(30, 30, 30, 30));
    root.setMinWidth(Mephisto3.WIDTH-50);
    Text name = new Text("Antenne Voralberg");
    name.getStyleClass().add("stream-name");
    Text artist = new Text("The fabulous baker boys");
    artist.getStyleClass().add("stream-artist");
    Text title = new Text("Sweet Child O Mine");
    title.getStyleClass().add("stream-title");
    Text url = new Text(stream.getPlaybackUrl());
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
