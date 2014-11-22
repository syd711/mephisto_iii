package de.calette.mephisto3.ui.google;

import callete.api.services.music.model.Album;
import callete.api.util.ImageCache;
import de.calette.mephisto3.resources.menu.MenuResourceLoader;
import de.calette.mephisto3.util.ComponentUtil;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Pane this displays the album cover, name and artist for the slider.
 */
public class AlbumBox extends VBox {
  public static final int COVER_WIDTH = 200;
  public static final int COVER_HEIGHT = 200;

  private double scaleFactor = 1.05;

  public AlbumBox(Album album) {
    super(10);
    setUserData(album);
    setMinWidth(COVER_WIDTH);

    if (album != null) {
      Canvas cover = ImageCache.loadCover(album, COVER_WIDTH, COVER_HEIGHT);
      Label name = new Label(album.getName());
      name.getStyleClass().add("album");
      final Label artist = new Label(album.getArtist());
      artist.getStyleClass().add("artist");

      getChildren().add(cover);
      getChildren().add(name);
      getChildren().add(artist);
    }
    else {
      setAlignment(Pos.BASELINE_RIGHT);
      setPadding(new Insets(80, 0, 0, 0));
      scaleFactor = 1.2;
      Canvas back = ComponentUtil.createImageCanvas(MenuResourceLoader.getResource("back.png"), 100, 100);
      getChildren().add(back);
    }
  }

  public void deselect() {
    TransitionUtil.createScaler(this, 1.0).play();
  }

  public void select() {
    TransitionUtil.createScaler(this, scaleFactor).play();
  }
}
