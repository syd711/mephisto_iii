package de.calette.mephisto3.ui.google;

import callete.api.Callete;
import callete.api.services.music.model.Album;
import callete.api.services.music.model.PlaylistItem;
import callete.api.services.music.model.Song;
import callete.api.services.music.player.PlaybackChangeEvent;
import callete.api.services.music.player.PlaybackChangeEventListener;
import de.calette.mephisto3.control.ControlListener;
import de.calette.mephisto3.control.ServiceControlEvent;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.resources.menu.MenuResourceLoader;
import de.calette.mephisto3.ui.ControllableHBoxItemPanelBase;
import de.calette.mephisto3.ui.ControllableSelectorPanel;
import de.calette.mephisto3.util.ComponentUtil;
import de.calette.mephisto3.util.Executor;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static de.calette.mephisto3.util.TransitionUtil.*;

/**
 * Pane this displays the album cover, name and artist for the slider.
 */
public class AlbumBox extends ControllableHBoxItemPanelBase<Album> implements ControlListener, PlaybackChangeEventListener {
  private final static Logger LOG = LoggerFactory.getLogger(AlbumBox.class);

  private static final int COVER_WIDTH = 200;
  public static final int BOX_WIDTH = 220;
  public static final int COVER_HEIGHT = 200;
  public static final int TRACKS_WIDTH = 390;
  public static final int TRACK_ITEM_HEIGHT = 30;
  public static final int MAX_DISPLAY_TRACKS = 11;
  //index when the tracks should be starting scrolling
  public static final int SCROLL_INDEX = 7;
  public static final int TRACKS_BOX_WIDTH = 500;
  public static final int TOP_PADDING = 15;

  private double scaleFactor = 1.05;
  private VBox albumLabelBox = new VBox(5);
  private VBox tracksBox;
  private int selectionIndex = -1;


  public AlbumBox(ControllableSelectorPanel parentControl, Album album) {
    super(10, parentControl, album);
    this.getStyleClass().add("album-box");
    setMinWidth(BOX_WIDTH);
    setMaxHeight(345);
    setPadding(new Insets(0, 0, 0, 10));

    //box used for labels below the cover
    albumLabelBox.setPadding(new Insets(TOP_PADDING, 0, 0, 0));

    //box for compact view in slider mode
    VBox compactView = new VBox();
    compactView.getStyleClass().add("debug");
    compactView.setPadding(new Insets(TOP_PADDING, 0, 0, 0));

    if (album != null) {
      if (!StringUtils.isEmpty(album.getArtUrl())) {
        ImageView cover = ComponentUtil.loadAlbumCover(album, COVER_WIDTH, COVER_HEIGHT);

        BorderPane p = new BorderPane();
        p.setCenter(cover);
        p.setMaxHeight(cover.getImage().getHeight()+1);
        p.setMaxWidth(cover.getImage().getWidth()+1);

        p.getStyleClass().add("cover-box");
        compactView.getChildren().add(p);
      }
      else {
        LOG.warn("No cover found for " + album + ", using spacer instead.");
        VBox spacer = new VBox();
        spacer.setMinHeight(COVER_HEIGHT);
        compactView.getChildren().add(spacer);
      }

      ComponentUtil.createLabel(getModel().getName(), "default-16", albumLabelBox);
      ComponentUtil.createLabel(getModel().getArtist(), "default-16", albumLabelBox);

      compactView.getChildren().add(albumLabelBox);
    }
    else {
      setAlignment(Pos.BASELINE_RIGHT);
      setPadding(new Insets(80, 0, 0, 0));
      scaleFactor = 1.2;
      Canvas back = ComponentUtil.createImageCanvas(MenuResourceLoader.getResource("back.png"), 100, 100);
      compactView.getChildren().add(back);
    }

    getChildren().add(compactView);
  }

  /**
   * Shows the details of the album, including the title selector.
   * That's why this panel takes control when mode is enabled.
   */
  public void switchToDetailsMode() {
    //scale no normal size: remove the selection highlighting
    createScaler(this, 1).play();

    //add control listener to this panel
    ServiceController.getInstance().addControlListener(this);
    Callete.getMusicPlayer().addPlaybackChangeEventListener(this);

    //expand panel to full width
    Transition maxWidthTransition = createMaxWidthTransition(this, COVER_WIDTH, TRACKS_BOX_WIDTH, true);

    //hide cover labels
    final FadeTransition outFader = createOutFader(albumLabelBox);
    outFader.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        //remove title name name...
        albumLabelBox.getChildren().clear();
        //...add tracks, year and genre instead.
        ComponentUtil.createLabel(getModel().getSize() + " Titel", "track", albumLabelBox);
        ComponentUtil.createLabel(getModel().getDuration(), "track", albumLabelBox);
        if (getModel().getYear() > 0) {
          ComponentUtil.createLabel(String.valueOf(getModel().getYear()), "default-16", albumLabelBox);
        }
        if (!StringUtils.isEmpty(getModel().getGenre())) {
          ComponentUtil.createLabel(getModel().getGenre(), "default-16", albumLabelBox);
        }
        createInFader(albumLabelBox).play();

        createTracksBox();
        getChildren().add(tracksBox);
        createInFader(tracksBox).play();
      }
    });

    ParallelTransition pt = new ParallelTransition(maxWidthTransition, outFader);
    pt.play();
  }

  @Override
  public void playbackChanged(PlaybackChangeEvent event) {
    if (tracksBox == null || tracksBox.getChildren().isEmpty()) {
      return;
    }

    LOG.info("Received playback change event for " + this + " with item " + event.getActiveItem());

    final PlaylistItem activeItem = event.getActiveItem();
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        for (Node node : tracksBox.getChildren()) {
          if (activeItem != null && node.getUserData().equals(activeItem)) {
            node.getStyleClass().add("track-active");
          }
          else {
            node.getStyleClass().remove("track-active");
          }
        }
      }
    });
  }

  @Override
  public double getScaleFactor() {
    return scaleFactor;
  }

  @Override
  public void controlEvent(ServiceControlEvent event) {
    if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PUSH)) {
      if (selectionIndex == -1) {
        switchToSliderMode();
      }
      else {
        final Node node = tracksBox.getChildren().get(selectionIndex);
        FadeTransition blink = TransitionUtil.createBlink(node);
        blink.setOnFinished(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent actionEvent) {
            Executor.run(new Runnable() {
              @Override
              public void run() {
                Song song = (Song) node.getUserData();
                Callete.getMusicPlayer().getPlaylist().setPlaylist(getModel());
                Callete.getMusicPlayer().getPlaylist().setActiveItem(song);
                Callete.getMusicPlayer().play();
              }
            });
          }
        });
        blink.play();
      }
    }
    else if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.NEXT)) {
      if (selectionIndex < getModel().getSize() - 1) {
        int oldIndex = selectionIndex;
        selectionIndex++;
        updateSelection(oldIndex, selectionIndex);
      }
    }
    else if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PREVIOUS)) {
      if (selectionIndex >= 0) {
        int oldIndex = selectionIndex;
        selectionIndex--;
        updateSelection(oldIndex, selectionIndex);
      }
    }
  }

  //----------------------- Helper methods ------------------------------------------

  /**
   * Creates the additional track list panel shown when
   * the details mode of an album is shown.
   */
  private void createTracksBox() {
    //create track box with initial opacity
    //box used for details mode
    tracksBox = new VBox(0);
    tracksBox.setPadding(new Insets(TOP_PADDING, 0, 0, 0));
    tracksBox.setMaxWidth(TRACKS_BOX_WIDTH - 20);
    tracksBox.setOpacity(0);
    final List<Song> songs = getModel().getSongs();

    PlaylistItem item = Callete.getMusicPlayer().getPlaylist().getActiveItem();

    for (Song song : songs) {
      HBox trackBox = new HBox();
      trackBox.setUserData(song);
      trackBox.setPadding(new Insets(4, 8, 4, 4));

      if (item != null && song.equals(item)) {
        trackBox.getStyleClass().add("track-active");
      }

      if (song.getTrack() > MAX_DISPLAY_TRACKS) {
        trackBox.setOpacity(0);
      }

      String styleClass = "default-16";

      HBox posBox = new HBox();
      posBox.setAlignment(Pos.CENTER_RIGHT);
      posBox.setMinWidth(25);
      trackBox.getChildren().add(posBox);
      ComponentUtil.createLabel(String.valueOf(song.getTrack()), styleClass, posBox);
      final Label title = ComponentUtil.createLabel(song.getName(), styleClass, trackBox);
      title.setPadding(new Insets(0, 0, 0, 10));
      title.setMaxWidth(TRACKS_WIDTH);
      title.setMinWidth(TRACKS_WIDTH);
      ComponentUtil.createText(song.getDuration(), styleClass, trackBox);
      tracksBox.getChildren().add(trackBox);
    }
  }

  /**
   * Updates the background to indicate the selection,
   * additionally scrolls down if the playlist lengths exceeds the height.
   *
   * @param oldIndex the index to remove the background from
   * @param newIndex the index to add the background to
   */
  private void updateSelection(int oldIndex, int newIndex) {
    if (oldIndex >= 0) {
      Node node = tracksBox.getChildren().get(oldIndex);
      node.getStyleClass().remove("track-selection");
    }

    if (newIndex >= 0 && newIndex <= tracksBox.getChildren().size() - 1) {
      Node node = tracksBox.getChildren().get(newIndex);
      node.getStyleClass().add("track-selection");
    }

    //scroll down
    if (getModel().getSize() > MAX_DISPLAY_TRACKS && (newIndex >= SCROLL_INDEX || oldIndex >= SCROLL_INDEX)) {
      if (oldIndex < newIndex) {
        //scroll down
        boolean doScroll = getModel().getSize() - newIndex >= 5;
        if (doScroll) {
          scroll(newIndex + 4, newIndex - SCROLL_INDEX, -TRACK_ITEM_HEIGHT);
        }

      }
      else {
        //scroll up
        boolean doScroll = getModel().getSize() - newIndex > 5;
        if (doScroll) {
          scroll(oldIndex - SCROLL_INDEX, oldIndex + 4, TRACK_ITEM_HEIGHT);
        }
      }
    }
  }

  private void scroll(int inFaderIndex, int outFaderIndex, int scrollWidth) {
    ServiceController.getInstance().setControlEnabled(false);
    final FadeTransition outFader = createOutFader(tracksBox.getChildren().get(outFaderIndex));
    final FadeTransition inFader = createInFader(tracksBox.getChildren().get(inFaderIndex));
    final TranslateTransition translateByYTransition = createTranslateByYTransition(tracksBox, 200, scrollWidth);
    ParallelTransition pt = new ParallelTransition(outFader, inFader, translateByYTransition);
    pt.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        ServiceController.getInstance().setControlEnabled(true);
      }
    });
    pt.play();
  }

  private void switchToSliderMode() {
    ServiceController.getInstance().removeControlListener(this);
    Callete.getMusicPlayer().removePlaybackChangeEventListener(this);

    final FadeTransition outFader = createOutFader(tracksBox);
    outFader.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        final ScaleTransition scaler = createScaler(AlbumBox.this, scaleFactor);
        final Transition maxWidth = createMaxWidthTransition(AlbumBox.this, BOX_WIDTH + TRACKS_BOX_WIDTH, TRACKS_BOX_WIDTH, false);
        final FadeTransition labelFader = createOutFader(albumLabelBox);

        ParallelTransition pt = new ParallelTransition(scaler, maxWidth, labelFader);
        pt.setOnFinished(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent actionEvent) {
            albumLabelBox.getChildren().clear();
            ComponentUtil.createLabel(getModel().getName(), "default-16", albumLabelBox);
            ComponentUtil.createLabel(getModel().getArtist(), "default-16", albumLabelBox);

            selectionIndex = -1;
            tracksBox.getChildren().clear();
            AlbumBox.this.getChildren().remove(tracksBox);

            createInFader(albumLabelBox).play();
            ServiceController.getInstance().addControlListener(getParentControlPanel());
          }
        });
        pt.play();
      }
    });
    outFader.play();
  }

  @Override
  public String toString() {
    return "Album Box '" + getModel() + "'";
  }
}
