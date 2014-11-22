package de.calette.mephisto3.ui.google;

import callete.api.services.music.model.Album;
import callete.api.util.ImageCache;
import de.calette.mephisto3.resources.menu.MenuResourceLoader;
import de.calette.mephisto3.ui.ControllableItemPanel;
import de.calette.mephisto3.util.ComponentUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.apache.commons.lang.StringUtils;

/**
 * Pane this displays the album cover, name and artist for the slider.
 */
public class AlbumBox extends ControllableItemPanel {
  public static final int COVER_WIDTH = 200;
  public static final int COVER_HEIGHT = 200;

  private double scaleFactor = 1.05;

  public AlbumBox(Album album) {
    super(10, album);
    setMinWidth(COVER_WIDTH);

    if (album != null) {
      if(!StringUtils.isEmpty(album.getArtUrl())) {
        Canvas cover = ImageCache.loadCover(album, COVER_WIDTH, COVER_HEIGHT);
        getChildren().add(cover);
      }
      else {
        VBox spacer = new VBox();
        spacer.setMinHeight(COVER_HEIGHT);
        getChildren().add(spacer);
      }

      Label name = new Label(album.getName());
      name.getStyleClass().add("album");
      final Label artist = new Label(album.getArtist());
      artist.getStyleClass().add("artist");

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

  @Override
  protected double getScaleFactor() {
    return scaleFactor;
  }
}
