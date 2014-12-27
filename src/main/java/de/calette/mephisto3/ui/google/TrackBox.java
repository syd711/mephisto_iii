package de.calette.mephisto3.ui.google;

import callete.api.services.music.model.Song;
import de.calette.mephisto3.resources.menu.MenuResourceLoader;
import de.calette.mephisto3.util.ComponentUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;


/**
 * Box that represents one row of the track list.
 */
public class TrackBox extends HBox {
  private static ImageView imageView = new ImageView(new Image(MenuResourceLoader.getResource("playing.png"), 20, 20, false, true));
  public static final String DEFAULT_16 = "default-16";
  private HBox posBox;
  private Song song;

  public TrackBox(Song song, boolean active) {
    setUserData(song);
    this.song = song;
    setPadding(new Insets(4, 8, 4, 4));
    if (song.getTrack() > AlbumBox.MAX_DISPLAY_TRACKS) {
      setOpacity(0);
    }

    posBox = new HBox();
    posBox.setAlignment(Pos.CENTER_RIGHT);
    posBox.setMinWidth(25);
    getChildren().add(posBox);
    ComponentUtil.createLabel(String.valueOf(song.getTrack()), DEFAULT_16, posBox);
    final Label title = ComponentUtil.createLabel(song.getName(), DEFAULT_16, this);
    title.setPadding(new Insets(0, 0, 0, 10));
    title.setMaxWidth(AlbumBox.TRACKS_WIDTH);
    title.setMinWidth(AlbumBox.TRACKS_WIDTH);
    ComponentUtil.createText(song.getDuration(), DEFAULT_16, this);

    setActive(active);
  }

  public void setActive(boolean active) {
    posBox.getChildren().clear();
    if(active) {
      posBox.getChildren().add(imageView);
      getStyleClass().add("track-active");
    }
    else {
      ComponentUtil.createLabel(String.valueOf(song.getTrack()), DEFAULT_16, posBox);
      getStyleClass().remove("track-active");
    }
  }
}
