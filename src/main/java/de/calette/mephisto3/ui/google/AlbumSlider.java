package de.calette.mephisto3.ui.google;

import callete.api.services.music.model.Album;
import callete.api.services.music.model.AlbumCollection;
import de.calette.mephisto3.control.ServiceController;
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
  private AlbumCollection albumCollection;

  public AlbumSlider(ServiceChooser serviceChooser, Pane parent, List<AlbumCollection> collections, AlbumCollection albumCollection) {
    super(10, parent, AlbumBox.BOX_WIDTH+10, albumCollection.getAlbums(), AlbumBox.class);
    setBackButton(70);
    this.albumCollection = albumCollection;
    this.collections = collections;
    this.serviceChooser = serviceChooser;

    LazyAlbumCoverCache.load(albumCollection.getAlbums());
  }

  @Override
  protected void onLongPush() {
    super.onLongPush();
    setSelectionIndex(0);
    hidePanel();
  }

  @Override
  public void hidePanel() {
    //check if the back button was selected
    if(getSelectedPanel().getUserData() == null) {
      super.hidePanel();
    }
    else {
      AlbumBox albumBox = (AlbumBox) getSelectedPanel();
      albumBox.switchToDetailsMode();
      ServiceController.getInstance().removeControlListener(this);
    }
  }

  @Override
  protected void onHide(Album selection) {
    AlbumLetterSelector selector = new AlbumLetterSelector(serviceChooser, getParentPane(), collections);
    selector.setSelection(albumCollection);
    selector.showPanel();
  }
}
