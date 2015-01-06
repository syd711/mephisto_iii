package de.calette.mephisto3.ui.google;

import callete.api.Callete;
import callete.api.services.music.model.Album;
import callete.api.services.music.model.PlaylistItem;
import callete.api.services.music.model.Song;
import callete.api.services.music.player.PlaybackChangeEvent;
import callete.api.services.music.player.PlaybackChangeListener;
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
import javafx.scene.text.Text;
import org.apache.commons.lang.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Displays the current status of the media player.
 */
public class GooglePlayerStatusBox extends BorderPane implements PlaylistChangeListener, PlaybackChangeListener {
  public static final int COVER_SIZE = 52;

  private Image defaultBackground = new Image(ResourceLoader.getResource("player-background.png"), COVER_SIZE, COVER_SIZE, false, true);
  private ImageView imageView;
  private Label nameLabel;
  private Label titleLabel;
  private Label albumInfoLabel;
  private ProgressBar progress;
  private Text totalDurationLabel;
  private Text currentDurationLabel;
  private Timer timer;
  private int currentDuration;

  public GooglePlayerStatusBox() {
    VBox spacer = new VBox();
    this.setOpacity(0);
    spacer.setMinHeight(332);
    setTop(spacer);
    setLeft(createImageBox());
    setCenter(createStatusBox());
  }

  public void showPlayer() {
    Callete.getMusicPlayer().getPlaylist().addChangeListener(this);
    Callete.getMusicPlayer().addPlaybackChangeEventListener(this);
    TransitionUtil.createInFader(this).play();
  }

  public void hidePlayer() {
    Callete.getMusicPlayer().getPlaylist().removeChangeListener(this);
    Callete.getMusicPlayer().removePlaybackChangeEventListener(this);
    TransitionUtil.createOutFader(this).play();
  }

  //---------- Helper -------------------------------------------------------

  private Node createStatusBox() {
    VBox status = new VBox(2);
    status.setMaxWidth(240);
    status.setMinWidth(240);
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
    progress.setMinWidth(280);
    progress.setMaxWidth(280);

    HBox durationWrapper = new HBox();
    durationWrapper.setMinWidth(30);
    durationWrapper.setAlignment(Pos.BASELINE_CENTER);
    currentDurationLabel = ComponentUtil.createText("", "", durationWrapper);

    HBox totalDurationWrapper = new HBox();
    totalDurationWrapper.setAlignment(Pos.BASELINE_CENTER);
    totalDurationWrapper.setMinWidth(30);
    totalDurationLabel = ComponentUtil.createText("", "", totalDurationWrapper);

    progressWrapper.getChildren().add(durationWrapper);
    progressWrapper.getChildren().add(progress);
    progressWrapper.getChildren().add(totalDurationWrapper);
    
    HBox spacer = new HBox();
    spacer.setMinWidth(10);
    progressWrapper.getChildren().add(spacer);

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
    if(image == null) {
      this.imageView.setImage(defaultBackground);
    }
    else {
      this.imageView.setImage(image);
    }
  }

  @Override
  public void playlistChanged(final PlaylistChangeEvent e) {
    Platform.runLater(() -> {
      PlaylistItem activeItem = e.getActiveItem();
      if(!(activeItem instanceof Song)) {
        cancelProgress();
        return;
      }

      final Song song = (Song) e.getActiveItem();
      Album album = song.getAlbum();

      nameLabel.setText(song.getName());
      titleLabel.setText(song.getArtist());
      String info = "";
      if(album.getYear() > 0) {
        info = String.valueOf(album.getYear());
      }
      if(!StringUtils.isEmpty(album.getGenre())) {
        if(info.length() > 0) {
          info += ", ";
        }
        info += album.getGenre();
      }

      albumInfoLabel.setText(info);
      progress.setOpacity(1);

      //reset progress to zero
      progress.setProgress(0);
      currentDurationLabel.setText("0:00");
      totalDurationLabel.setText(" " + song.getDuration());

      //reset timer
      if(timer != null) {
        timer.purge();
        timer.cancel();
        timer = null;
      }
    });

  }

  @Override
  public void playbackChanged(PlaybackChangeEvent event) {
    PlaylistItem activeItem = event.getActiveItem();
    if(!(activeItem instanceof Song)) {
      cancelProgress();
      return;
    }

    final Song song = (Song) event.getActiveItem();
    final Album album = song.getAlbum();
    //reset timer
    if(timer != null) {
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
            if(currentDuration * 1000 <= song.getDurationMillis()) {
              currentDurationLabel.setText(time+" ");
            }
            long duration = song.getDurationMillis() / 1000;
            double progressValue = 1.0 / duration;
            progressValue = progress.getProgress() + progressValue;
            progress.setProgress(progressValue);
            if(progressValue > 1) {
              timer.purge();
              timer.cancel();
              timer = null;
              progress.setProgress(0);
            }
          }
        });
      }
    }, 0, 1000);

    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        ImageView cover = ComponentUtil.loadAlbumCover(album, COVER_SIZE, COVER_SIZE);
        setImage(cover.getImage());
      }
    });
  }

  public boolean isPlaying() {
    return timer != null;
  }

  private void cancelProgress() {
    if(timer != null) {
      timer.purge();
      timer.cancel();
      timer = null;
    }
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        setImage(null);
        nameLabel.setText("");
        titleLabel.setText("");
        albumInfoLabel.setText("");
        progress.setProgress(0);
        currentDurationLabel.setText("");
        totalDurationLabel.setText("");
        progress.setOpacity(0);
      }
    });
  }
}
