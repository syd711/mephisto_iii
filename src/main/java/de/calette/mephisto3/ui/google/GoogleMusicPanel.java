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
 * Well, this panel is not used!
 * Since the music selection is delegated to subpanel, we only use
 * this panel to initialize Google music.
 */
public class GoogleMusicPanel extends ControllablePanel {
  private final static Logger LOG = LoggerFactory.getLogger(GoogleMusicPanel.class);

  public GoogleMusicPanel() {
    super(Callete.getGoogleMusicService().getAlbums());
    loadGoogleMusic();
  }

  //--------------- Helper ------------------------------------------------------

  /**
   * Google Music Login
   */
  private void loadGoogleMusic() {
    try {
      Callete.getGoogleMusicService().authenticate();
    } catch (Exception e) {
      LOG.error("Error authenticating Google music: " + e.getMessage(), e);
    }
  }
}
