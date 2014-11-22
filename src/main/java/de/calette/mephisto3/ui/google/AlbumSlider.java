package de.calette.mephisto3.ui.google;

import callete.api.services.music.model.Album;
import callete.api.util.ImageCache;
import de.calette.mephisto3.control.ControlListener;
import de.calette.mephisto3.control.ServiceControlEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class AlbumSlider implements ControlListener {
  public static final int COVER_WIDTH = 200;
  public static final int COVER_HEIGHT = 200;
  public static final int SCROLL_WIDTH = 230;

  private final static Logger LOG = LoggerFactory.getLogger(AlbumSlider.class);
  private List<Album> updatedServiceModel = new ArrayList<>();

  public AlbumSlider(Pane parent, List<Album> albumList) {
    HBox center = new HBox();
    parent.getChildren().add(center);

    HBox spacer = new HBox();
    spacer.setMinWidth(SCROLL_WIDTH);
    center.getChildren().add(spacer);

    for (Album album : albumList) {
      updatedServiceModel.add(album);

      if (!StringUtils.isEmpty(album.getArtUrl())) {
        Canvas cover = ImageCache.loadCover(album, COVER_WIDTH, COVER_HEIGHT);
        VBox albumPanel = new VBox(10);
        albumPanel.setUserData(album.getId());

        albumPanel.setPrefWidth(COVER_WIDTH);
        Label name = new Label(album.getName());
        name.getStyleClass().add("album");
        final Label artist = new Label(album.getArtist());
        artist.getStyleClass().add("artist");

        albumPanel.getChildren().add(cover);
        albumPanel.getChildren().add(name);
        albumPanel.getChildren().add(artist);

        center.getChildren().add(albumPanel);
      }
      else {
        //        String url = ResourceLoader.getResource("cover.png");
        //        Canvas cover = TransitionUtil.createImageCanvas(url, COVER_SIZE, COVER_SIZE);
        //        vbox.getChildren().add(cover);
      }


    }
    LOG.info("Finished creation of AlbumSlider panel.");
  }


  public List<Album> getUpdatedServiceModels() {
    return updatedServiceModel;
  }

  @Override
  public void controlEvent(ServiceControlEvent event) {

  }
}
