package de.calette.mephisto3.ui.google;

import callete.api.services.music.model.Album;
import callete.api.services.music.model.Song;
import callete.api.util.ImageCache;
import de.calette.mephisto3.control.ControlListener;
import de.calette.mephisto3.control.ServiceControlEvent;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.resources.menu.MenuResourceLoader;
import de.calette.mephisto3.ui.ControllableHBoxItemPanelBase;
import de.calette.mephisto3.ui.ControllableSelectorPanel;
import de.calette.mephisto3.util.ComponentUtil;
import de.calette.mephisto3.util.TransitionQueue;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Pane this displays the album cover, name and artist for the slider.
 */
public class AlbumBox extends ControllableHBoxItemPanelBase<Album> implements ControlListener {
  public static final int COVER_WIDTH = 200;
  public static final int COVER_HEIGHT = 200;
  public static final int TRACKS_WIDTH = 390;
  public static final int TRACK_ITEM_HEIGHT = 30;
  public static final int MAX_DISPLAY_TRACKS = 11;
  //index when the tracks should be starting scrolling
  public static final int SCROLL_INDEX = 7;
  public static final int TRACKS_BOX_WIDTH = 480;

  private double scaleFactor = 1.05;
  private Canvas cover;
  private VBox albumLabelBox = new VBox(5);
  private VBox tracksBox;
  private int selectionIndex = -1;


  public AlbumBox(ControllableSelectorPanel parentControl, Album album) {
    super(10, parentControl, album);
    this.getStyleClass().add("album-box");
    setMinWidth(COVER_WIDTH);

    //box used for labels below the cover
    albumLabelBox.setPadding(new Insets(10, 0, 0, 0));

    //box for compact view in slider mode
    VBox compactView = new VBox();


    if (album != null) {
      if (!StringUtils.isEmpty(album.getArtUrl())) {
        cover = ImageCache.loadCover(album, COVER_WIDTH, COVER_HEIGHT);
        cover.getStyleClass().add("cover-canvas");
        compactView.getChildren().add(cover);
      }
      else {
        VBox spacer = new VBox();
        spacer.setMinHeight(COVER_HEIGHT);
        compactView.getChildren().add(spacer);
      }

      ComponentUtil.createLabel(getModel().getName(), "album", albumLabelBox);
      ComponentUtil.createLabel(getModel().getArtist(), "album", albumLabelBox);

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
    TransitionUtil.createScaler(this, 1).play();
    //expand panel to full width
    TransitionUtil.createMaxWidth(this, COVER_WIDTH, TRACKS_BOX_WIDTH, true).play();
    //add control listener to this panel
    ServiceController.getInstance().addControlListener(this);

    //hide cover labels
    final FadeTransition outFader = TransitionUtil.createOutFader(albumLabelBox);
    outFader.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        //remove title name name...
        albumLabelBox.getChildren().clear();
        //...add tracks, year and genre instead.
        ComponentUtil.createLabel(getModel().getSize() + " Titel", "track", albumLabelBox);
        ComponentUtil.createLabel(getModel().getDuration(), "track", albumLabelBox);
        if (getModel().getYear() > 0) {
          ComponentUtil.createLabel(getModel().getYear(), "album", albumLabelBox);
        }
        if (!StringUtils.isEmpty(getModel().getGenre())) {
          ComponentUtil.createLabel(getModel().getGenre(), "album", albumLabelBox);
        }
        TransitionUtil.createInFader(albumLabelBox).play();

        createTracksBox();
        getChildren().add(tracksBox);
        TransitionUtil.createInFader(tracksBox).play();
      }
    });
    outFader.play();
  }

  private void createTracksBox() {
    //create track box with initial opacity
    //box used for details mode
    tracksBox = new VBox(0);
    tracksBox.setMaxWidth(TRACKS_BOX_WIDTH-20);
    tracksBox.setOpacity(0);
    final List<Song> songs = getModel().getSongs();
    for (Song song : songs) {
      HBox trackBox = new HBox();
      trackBox.setPadding(new Insets(4, 8, 4, 4));

      if (song.getTrack() > MAX_DISPLAY_TRACKS) {
        trackBox.setOpacity(0);
      }

      String styleClass = "track";

      HBox posBox = new HBox();
      posBox.setAlignment(Pos.CENTER_RIGHT);
      posBox.setMinWidth(25);
      trackBox.getChildren().add(posBox);
      ComponentUtil.createLabel(song.getTrack(), styleClass, posBox);
      final Label title = ComponentUtil.createLabel(song.getName(), styleClass, trackBox);
      title.setPadding(new Insets(0, 0, 0, 10));
      title.setMaxWidth(TRACKS_WIDTH);
      title.setMinWidth(TRACKS_WIDTH);
      ComponentUtil.createText(song.getDuration(), styleClass, trackBox);
      tracksBox.getChildren().add(trackBox);
    }
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
          TransitionUtil.createInFader(tracksBox.getChildren().get(newIndex + 4)).play();
          TransitionUtil.createOutFader(tracksBox.getChildren().get(newIndex - SCROLL_INDEX)).play();
          TransitionUtil.createTranslateByYTransition(tracksBox, 200, -TRACK_ITEM_HEIGHT).play();
        }

      }
      else {
        //scroll up
        boolean doScroll = getModel().getSize() - newIndex > 5;
        if (doScroll) {
          TransitionUtil.createOutFader(tracksBox.getChildren().get(oldIndex + 4)).play();
          TransitionUtil.createInFader(tracksBox.getChildren().get(oldIndex - SCROLL_INDEX)).play();
          TransitionUtil.createTranslateByYTransition(tracksBox, 200, TRACK_ITEM_HEIGHT).play();
        }
      }
    }
  }

  private void switchToSliderMode() {
    ServiceController.getInstance().removeControlListener(this);

    final FadeTransition outFader = TransitionUtil.createOutFader(tracksBox);
    outFader.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        final ScaleTransition scaler = TransitionUtil.createScaler(AlbumBox.this, scaleFactor);
        final Transition maxWidth = TransitionUtil.createMaxWidth(AlbumBox.this, COVER_WIDTH + TRACKS_BOX_WIDTH, TRACKS_BOX_WIDTH, false);
        final FadeTransition labelFader = TransitionUtil.createOutFader(albumLabelBox);

        ParallelTransition pt = new ParallelTransition(scaler, maxWidth, labelFader);
        pt.setOnFinished(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent actionEvent) {
            albumLabelBox.getChildren().clear();
            ComponentUtil.createLabel(getModel().getName(), "album", albumLabelBox);
            ComponentUtil.createLabel(getModel().getArtist(), "album", albumLabelBox);

            selectionIndex = -1;
            tracksBox.getChildren().clear();
            AlbumBox.this.getChildren().remove(tracksBox);

            TransitionUtil.createInFader(albumLabelBox).play();
            ServiceController.getInstance().addControlListener(getParentControlPanel());
          }
        });
        pt.play();
      }
    });
    outFader.play();
  }
}
