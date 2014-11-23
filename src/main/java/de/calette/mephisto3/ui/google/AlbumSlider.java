package de.calette.mephisto3.ui.google;

import callete.api.Callete;
import callete.api.services.music.model.Album;
import de.calette.mephisto3.ui.ControllableItemPanel;
import de.calette.mephisto3.ui.ControllableSelectorPanel;
import javafx.scene.layout.Pane;

import java.util.List;

/**
 * A cover flow with the give list of albums.
 */
public class AlbumSlider extends ControllableSelectorPanel<Album> {
  public AlbumSlider(Pane parent, List<Album> albums) {
    super(20, parent, true, AlbumBox.COVER_WIDTH + 20, albums, AlbumBox.class);
  }

  @Override
  protected void onHide(Object userData) {
    AlbumLetterSelector selector = new AlbumLetterSelector(getParentPane(), Callete.getGoogleMusicService().getAlbumsByArtistLetter());
    selector.showLetterSelector();
  }
}
