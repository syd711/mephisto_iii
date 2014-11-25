package de.calette.mephisto3.ui.google;

import callete.api.services.music.model.Album;
import callete.api.services.music.model.AlbumCollection;
import de.calette.mephisto3.ui.ControllableSelectorPanel;
import de.calette.mephisto3.ui.ServiceChooser;
import javafx.scene.layout.Pane;

import java.util.List;

/**
 * A cover flow with the give list of albums.
 */
public class AlbumSlider extends ControllableSelectorPanel<Album> {
  private ServiceChooser serviceChooser;
  private List<AlbumCollection> collections;

  public AlbumSlider(ServiceChooser serviceChooser, Pane parent, List<AlbumCollection> collections, AlbumCollection albumCollection) {
    super(20, parent, AlbumBox.COVER_WIDTH + 20, albumCollection.getAlbums(), AlbumBox.class);
    setBackButton(70);
    this.collections = collections;
    this.serviceChooser = serviceChooser;
  }

  @Override
  protected void onHide(Album selection) {
    AlbumLetterSelector selector = new AlbumLetterSelector(serviceChooser, getParentPane(), collections);
    selector.showPanel();
  }
}
