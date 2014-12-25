package de.calette.mephisto3.ui.google;

import callete.api.Callete;
import callete.api.services.music.model.Album;
import callete.api.services.music.model.Song;
import callete.api.services.music.player.PlaylistChangeEvent;
import callete.api.services.music.player.PlaylistChangeListener;
import de.calette.mephisto3.resources.ResourceLoader;
import de.calette.mephisto3.util.ComponentUtil;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Displays the current status of the media player.
 */
public class GooglePlayerStatusBox extends BorderPane implements PlaylistChangeListener {
  public static final int COVER_HEIGHT = 52;

  private Image defaultBackground = new Image(ResourceLoader.getResource("player-background.png"), COVER_HEIGHT, COVER_HEIGHT, false, true);
  private ImageView imageView;
  private Label nameLabel;
  private Label titleLabel;
  private ProgressBar progress;
  private Label durationLabel;

  public GooglePlayerStatusBox() {
    VBox spacer = new VBox();
    this.setOpacity(0);
    spacer.setMinHeight(335);
    setTop(spacer);
    setLeft(createImageBox());
    setCenter(createStatusBox());
  }

  public void showPlayer() {
    Callete.getMusicPlayer().getPlaylist().addChangeListener(this);
    TransitionUtil.createInFader(this).play();
  }

  public void hidePlayer() {
    Callete.getMusicPlayer().getPlaylist().removeChangeListener(this);
    TransitionUtil.createOutFader(this).play();
  }

  //---------- Helper -------------------------------------------------------

  private Node createStatusBox() {
    VBox status = new VBox(3);
    status.setMaxWidth(250);
    status.setPadding(new Insets(3, 3, 3, 8));
    nameLabel = ComponentUtil.createLabel("", "player-name-label", status);
    titleLabel = ComponentUtil.createLabel("", "player-title-label", status);

    BorderPane statusBox = new BorderPane();
    statusBox.getStyleClass().add("player-status-panel");
    statusBox.setLeft(status);

    progress = new ProgressBar();
    progress.setOpacity(0);
    progress.setMinWidth(350);
    statusBox.setCenter(progress);

    VBox durationBox = new VBox();
    durationBox.setAlignment(Pos.BASELINE_CENTER);
    durationBox.setMinWidth(70);
    durationBox.setMinHeight(COVER_HEIGHT);
    durationLabel = new Label();
    durationLabel.getStyleClass().add("");
    statusBox.setRight(durationLabel);
    return statusBox;
  }

  private Node createImageBox() {
    HBox wrapper = new HBox(5);
    wrapper.getStyleClass().add("player-status-panel");
    wrapper.setPadding(new Insets(3, 2, 2, 4));
    HBox imageBox = new HBox();
    imageBox.setMaxHeight(42);
    imageBox.getStyleClass().add("player-status-image");
    imageView = new ImageView(defaultBackground);
    imageBox.getChildren().add(imageView);
    wrapper.getChildren().add(imageBox);
    return wrapper;
  }

  public void setImage(Image image) {
    if(image == null) {
      this.imageView.setImage(defaultBackground);
    }
    else {
      this.imageView.setImage(image);
    }
  }

  @Override
  public void playlistChanged(final PlaylistChangeEvent e) {
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        Song song = (Song) e.getActiveItem();
        Album album = song.getAlbum();

        titleLabel.setText(song.getName());
        nameLabel.setText(song.getArtist());
        progress.setOpacity(1);
        durationLabel.setText(song.getDuration());

        ImageView cover = ComponentUtil.loadAlbumCover(album, COVER_HEIGHT, COVER_HEIGHT);
        setImage(cover.getImage());
      }
    });

  }
}
