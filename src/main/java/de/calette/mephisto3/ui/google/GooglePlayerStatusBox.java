package de.calette.mephisto3.ui.google;

import callete.api.Callete;
import callete.api.services.music.model.Album;
import callete.api.services.music.model.Song;
import callete.api.services.music.player.PlaylistChangeEvent;
import callete.api.services.music.player.PlaylistChangeListener;
import callete.api.util.DateUtil;
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
import org.apache.commons.lang.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Displays the current status of the media player.
 */
public class GooglePlayerStatusBox extends BorderPane implements PlaylistChangeListener {
  public static final int COVER_HEIGHT = 52;
  public static final int COVER_WIDTH = 52;

  private Image defaultBackground = new Image(ResourceLoader.getResource("player-background.png"), COVER_WIDTH, COVER_HEIGHT, false, true);
  private ImageView imageView;
  private Label nameLabel;
  private Label titleLabel;
  private Label albumInfoLabel;
  private ProgressBar progress;
  private Label totalDurationLabel;
  private Label currentDurationLabel;
  private Timer timer;
  private int currentDuration;

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
    VBox status = new VBox(2);
    status.setMaxWidth(220);
    status.setMinWidth(220);
    status.setPadding(new Insets(3, 3, 0, 5));
    nameLabel = ComponentUtil.createCustomLabel("", "player-name-label", status);
    titleLabel = ComponentUtil.createCustomLabel("", "player-title-label", status);
    albumInfoLabel = ComponentUtil.createCustomLabel("", "player-albuminfo-label", status);

    BorderPane statusBox = new BorderPane();
    statusBox.getStyleClass().add("player-status-panel");
    statusBox.setLeft(status);

    HBox progressWrapper = new HBox(5);
    progressWrapper.setPadding(new Insets(20, 10, 0, 0));
    statusBox.setCenter(progressWrapper);
    progress = new ProgressBar();
    progress.setOpacity(0);
    progress.setProgress(0);
    progress.setMinWidth(330);
    progress.setMaxWidth(330);

    currentDurationLabel = ComponentUtil.createLabel("", "", progressWrapper);
    currentDurationLabel.setAlignment(Pos.CENTER);
    currentDurationLabel.setMinWidth(30);
    progressWrapper.getChildren().add(progress);
    totalDurationLabel = ComponentUtil.createLabel("", "", progressWrapper);
    totalDurationLabel.setAlignment(Pos.CENTER);
    totalDurationLabel.setMinWidth(30);
    return statusBox;
  }

  private Node createImageBox() {
    HBox wrapper = new HBox(5);
    wrapper.getStyleClass().add("player-status-panel");
    wrapper.setPadding(new Insets(4, 2, 2, 4));
    HBox imageBox = new HBox();
    imageBox.setMaxHeight(42);
    imageBox.getStyleClass().add("player-status-image");
    imageView = new ImageView(defaultBackground);
    imageBox.getChildren().add(imageView);
    wrapper.getChildren().add(imageBox);
    return wrapper;
  }

  public void setImage(Image image) {
    if (image == null) {
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
        final Song song = (Song) e.getActiveItem();
        Album album = song.getAlbum();

        //reset progress to zero
        progress.setProgress(0);

        titleLabel.setText(song.getName());
        nameLabel.setText(song.getArtist());
        String info = "";
        if (album.getYear() > 0) {
          info = String.valueOf(album.getYear());
        }
        if (!StringUtils.isEmpty(album.getGenre())) {
          if (info.length() > 0) {
            info += ", ";
          }
          info += album.getGenre();
        }

        albumInfoLabel.setText(info);
        progress.setOpacity(1);
        totalDurationLabel.setText(song.getDuration());

        //reset timer
        if (timer != null) {
          timer.purge();
          timer.cancel();
          timer = null;
        }

        timer = new Timer();
        currentDuration = 0;
        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            Platform.runLater(new Runnable() {
              @Override
              public void run() {
                currentDuration++;

                String time = DateUtil.formatTime(currentDuration);
                currentDurationLabel.setText(time);
                long duration = song.getDurationMillis() / 1000;
                double progressValue = 1.0 / duration;
                progressValue = progress.getProgress() + progressValue;
                progress.setProgress(progressValue);
                if (progressValue > 1) {
                  timer.purge();
                  timer.cancel();
                  progress.setProgress(1.0);
                }
              }
            });
          }
        }, 0, 1000);

        ImageView cover = ComponentUtil.loadAlbumCover(album, COVER_WIDTH, COVER_HEIGHT);
        setImage(cover.getImage());
      }
    });

  }
}
