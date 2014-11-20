package de.calette.mephisto3.ui.google;

import callete.api.Callete;
import callete.api.services.music.model.Album;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.control.ServiceState;
import de.calette.mephisto3.ui.ControllablePanel;
import de.calette.mephisto3.ui.Footer;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays all albums of the Google user
 */
public class GoogleMusicPanel extends ControllablePanel {
  private final static Logger LOG = LoggerFactory.getLogger(GoogleMusicPanel.class);
  public static final int COVER_WIDTH = 200;
  public static final int COVER_HEIGHT = 200;
  public static final int SCROLL_WIDTH = 230;

  private HBox center;

  private Node lastSelection;

  public GoogleMusicPanel() {
    super(Callete.getGoogleMusicService().getAlbums());
    super.setScrollWidth(SCROLL_WIDTH);
    setMinWidth(Mephisto3.WIDTH);

    BorderPane root = new BorderPane();
    root.setPadding(new Insets(30, 20, 10, 20));

    center = new HBox(20);
    center.setAlignment(Pos.TOP_LEFT);
    root.setCenter(center);

    loadGoogleMusic();

    getChildren().add(root);
  }

  @Override
  public void rotatedRight(ServiceState serviceState) {
    super.rotatedRight(serviceState);
    updateSelection(serviceState);
  }

  @Override
  public void rotatedLeft(ServiceState serviceState) {
    super.rotatedLeft(serviceState);
    updateSelection(serviceState);
  }

  @Override
  public void showPanel() {
    super.showPanel();

    //updating foot
    //TODO
    List<Album> albums = new ArrayList<>();
    for (Album album : Callete.getGoogleMusicService().getAlbums()) {
      if (!album.getArtist().toUpperCase().startsWith("S")) {
        continue;
      }
      albums.add(album);
    }
    AlbumSlider slider = new AlbumSlider(center, albums);

    final ServiceState serviceState = ServiceController.getInstance().getServiceState();
    serviceState.setModels(slider.getUpdatedServiceModels());
    Footer.getInstance().serviceChanged(serviceState);

    updateSelection(serviceState);
  }

  @Override
  public void hidePanel() {
    super.hidePanel();
    LOG.info("Destroying current album slider");
    center.getChildren().clear();
  }

  //--------------- Helper ------------------------------------------------------

  /**
   * Asynchronous load of google music
   */
  private void loadGoogleMusic() {
    try {
      Callete.getGoogleMusicService().authenticate();
    } catch (Exception e) {
      LOG.error("Error authenticating Google music: " + e.getMessage(), e);
    }
  }

  private void updateSelection(ServiceState serviceState) {
    Album album = (Album) serviceState.getActiveServiceModel();
    final ObservableList<Node> children = center.getChildren();
    for (Node node : children) {
      if (node.getUserData() != null && node.getUserData().equals(album.getId())) {
        if (lastSelection != null) {
          TransitionUtil.createScaler(lastSelection, ControllablePanel.SCROLL_DURATION, 1.0).play();
        }
        lastSelection = node;
        TransitionUtil.createScaler(node, ControllablePanel.SCROLL_DURATION, 1.2).play();
      }
    }
  }
}
