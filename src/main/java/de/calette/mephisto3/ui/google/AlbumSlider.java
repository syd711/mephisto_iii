package de.calette.mephisto3.ui.google;

import callete.api.Callete;
import callete.api.services.music.model.Album;
import de.calette.mephisto3.control.ControlListener;
import de.calette.mephisto3.control.ServiceControlEvent;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.ui.ControllableItemPanel;
import de.calette.mephisto3.ui.ControllableSelectorPanel;
import de.calette.mephisto3.util.TransitionQueue;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 */
public class AlbumSlider extends ControllableSelectorPanel<Album> {
  public AlbumSlider(Pane parent, List<Album> albums) {
    super(20, parent, true, AlbumBox.COVER_WIDTH + 20, albums);
  }

  @Override
  protected void onHide(Object userData) {
    AlbumLetterSelector selector = new AlbumLetterSelector(getParentPane(), Callete.getGoogleMusicService().getAlbumsByArtistLetter());
    selector.showLetterSelector();
  }

  @Override
  protected ControllableItemPanel createControllableItemPanelFor(Album model) {
    return new AlbumBox(model);
  }
}
