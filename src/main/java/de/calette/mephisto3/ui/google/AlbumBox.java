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
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Pane this displays the album cover, name and artist for the slider.
 */
public class AlbumBox extends ControllableHBoxItemPanelBase<Album> implements ControlListener {
  public static final int COVER_WIDTH = 200;
  public static final int COVER_HEIGHT = 200;

  private double scaleFactor = 1.05;
  private boolean detailsMode = false;
  private Canvas cover;
  private VBox albumLabelBox = new VBox(5);
  private VBox tracksBox = new VBox(0);
  private int selectionIndex = -1;

  public AlbumBox(ControllableSelectorPanel parentControl, Album album) {
    super(10, parentControl, album);
    setMinWidth(COVER_WIDTH);

    //box used for labels below the cover
    albumLabelBox.setPadding(new Insets(10, 0, 0, 0));

    //box used for details mode
    tracksBox.setMaxWidth(450);

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
    detailsMode = true;
    //scale no normal size: remove the selection highlighting
    TransitionUtil.createScaler(this, 1).play();
    //expand panel to full width
    TransitionUtil.createMaxWidth(this, COVER_WIDTH, 480, true).play();
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
    tracksBox.setOpacity(0);
    final List<Song> songs = getModel().getSongs();
    for (Song song : songs) {
      HBox trackBox = new HBox();
      trackBox.setPadding(new Insets(4, 8, 4, 4));

      String styleClass = "track";
      if(song.getTrack() == 12) {
        styleClass = "track-bottom";
      }

      HBox posBox = new HBox();
      posBox.setAlignment(Pos.CENTER_RIGHT);
      posBox.setMinWidth(25);
      trackBox.getChildren().add(posBox);
      ComponentUtil.createLabel(song.getTrack(), styleClass, posBox);
      final Label title = ComponentUtil.createLabel(song.getName(), styleClass, trackBox);
      title.setPadding(new Insets(0, 0, 0, 10));
      title.setMaxWidth(370);
      title.setMinWidth(370);
      ComponentUtil.createLabel(song.getDuration(), styleClass, trackBox);
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
      switchToSliderMode();
    }
    else if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.LONG_PUSH)) {
      switchToSliderMode();
    }
    else if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.NEXT)) {
      int oldIndex = selectionIndex;
      selectionIndex++;
      updateSelection(oldIndex, selectionIndex);
    }
    else if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PREVIOUS)) {
      int oldIndex = selectionIndex;
      selectionIndex--;
      updateSelection(oldIndex, selectionIndex);
    }
  }

  private void updateSelection(int oldIndex, int newIndex) {
    if(oldIndex >= 0 ) {
      Node node = tracksBox.getChildren().get(oldIndex);
      node.getStyleClass().remove("track-selection");
    }

    if(newIndex >= 0 && newIndex < tracksBox.getChildren().size()-1) {
      Node node = tracksBox.getChildren().get(newIndex);
      node.getStyleClass().add("track-selection");
    }

    //scroll down
    if(newIndex == 7 && oldIndex < newIndex) {
      //TransitionUtil.create
    }
  }

  private void switchToSliderMode() {
    ServiceController.getInstance().removeControlListener(this);
    detailsMode = false;

    final FadeTransition outFader = TransitionUtil.createOutFader(tracksBox);
    outFader.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        TransitionUtil.createScaler(AlbumBox.this, scaleFactor).play();

        AlbumBox.this.getChildren().remove(tracksBox);
        final Transition widthTransition = TransitionUtil.createMaxWidth(AlbumBox.this, COVER_WIDTH + 480, 480, false);
        widthTransition.setOnFinished(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent actionEvent) {
            ServiceController.getInstance().addControlListener(getParentControlPanel());
          }
        });
        widthTransition.play();
      }
    });
    outFader.play();

    final FadeTransition labelFader = TransitionUtil.createOutFader(albumLabelBox);
    labelFader.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        albumLabelBox.getChildren().clear();
        ComponentUtil.createLabel(getModel().getName(), "album", albumLabelBox);
        ComponentUtil.createLabel(getModel().getArtist(), "album", albumLabelBox);
        TransitionUtil.createInFader(albumLabelBox).play();
      }
    });
    labelFader.play();
  }
}
