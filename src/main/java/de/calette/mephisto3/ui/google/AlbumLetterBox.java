package de.calette.mephisto3.ui.google;

import callete.api.services.music.model.Album;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;

/**
 * The box that is shown for each letter.
 */
public class AlbumLetterBox extends VBox {
  public final static int LETTER_BOX_WIDTH = 40;

  private Text text;
  private List<Album> albums;

  public AlbumLetterBox(String key, List<Album> albums) {
    this.albums = albums;
    setUserData(key);
    setMinWidth(LETTER_BOX_WIDTH);
    setAlignment(Pos.TOP_CENTER);
    text = new Text(key);
    text.getStyleClass().add("album-key");
    getChildren().add(text);
  }

  public void deselect() {
    TransitionUtil.createScaler(text, 1.0).play();
  }

  public void select() {
    TransitionUtil.createScaler(text, 1.25).play();
  }

  public List<Album> getAlbums() {
    return albums;
  }
}
