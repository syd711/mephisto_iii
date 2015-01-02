package de.calette.mephisto3.ui.google;

import callete.api.services.music.model.Album;
import callete.api.services.music.model.AlbumCollection;
import de.calette.mephisto3.control.ServiceControlEvent;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.ui.ControllableItemPanel;
import de.calette.mephisto3.ui.ControllableSelectorPanel;
import de.calette.mephisto3.ui.ServiceChooser;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * A cover flow with the give list of albums.
 */
public class AlbumSlider extends ControllableSelectorPanel<Album> {
  private final static Logger LOG = LoggerFactory.getLogger(AlbumSlider.class);

  private int visibleItemCount = 3;
  private ServiceChooser serviceChooser;
  private List<AlbumCollection> collections;
  private AlbumCollection albumCollection;

  public AlbumSlider(ServiceChooser serviceChooser, Pane parent, List<AlbumCollection> collections, AlbumCollection albumCollection, List<Album> albums, int visibleItemCount) {
    super(10, parent, AlbumBox.BOX_WIDTH + 10, albums, AlbumBox.class);
    this.visibleItemCount = visibleItemCount;
    setBackButton(70);
    this.albumCollection = albumCollection;
    this.collections = collections;
    this.serviceChooser = serviceChooser;
    LazyAlbumCoverCache.load(albumCollection.getAlbums());
  }

  @Override
  public void controlEvent(ServiceControlEvent event) {
    super.controlEvent(event);

    if(event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.NEXT)) {
      if(appendAlbumOnRight()) {
        removeAlbumFromLeft();
      }
    }
    else if(event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PREVIOUS)) {
      if(appendAlbumOnLeft()) {
        removeAlbumFromRight();
      }
    }
  }

  @Override
  protected int getScrollWidth() {
//    if(getSelectionIndex() == 1) {
//      return 230;
//    }
    return 0; //0 since we do not scroll anymore and the pseudo scrolling is achieved by just adding the panel
  }

  @Override
  protected void onLongPush() {
    super.onLongPush();
    setSelectionIndex(0);
    super.hidePanel();
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

  @Override
  protected int getItemCount() {
    return albumCollection.getAlbums().size();
  }

  @Override
  protected void deselect(boolean toLeft, int oldIndex) {
    if(toLeft) {
      super.deselect(toLeft, 1);
    }
    else {
      if(oldIndex == 0) {
        super.deselect(toLeft, 0);
      }
      else {
        super.deselect(toLeft, 1);
      }
    }
  }

  @Override
  protected void select(boolean toLeft, int newIndex) {
    if(toLeft) {
      super.select(toLeft, 0);
    }
    else {
      if(newIndex == 1) {
        super.select(toLeft, 1);
      }
      else {
        super.select(toLeft, 2);
      }
    }
  }

  @Override
  public ControllableItemPanel getSelectedPanel() {
    int visibleSelection = 1;
    int index = getSelectionIndex();
    int addIndex = (visibleItemCount - 1) + index;
    if(addIndex == albumCollection.getAlbums().size()) {
      visibleSelection = 1;
    }
    if(index == 0) {
      visibleSelection = 0;
    }

    ControllableItemPanel selection = (ControllableItemPanel) getChildren().get(visibleSelection);
    LOG.debug("Selection is " + selection);
    return selection;
  }

  //---------------------- Helper ----------------------

  /**
   * Adds an album box
   */
  private boolean appendAlbumOnRight() {
    int index = getSelectionIndex();
    int addIndex = index;

    if(index <= 1) {
      return false;
    }

    if(addIndex == albumCollection.getAlbums().size()) {
      //check if it has not been added already
      for(Node node : this.getChildren()) {
        AlbumBox child = (AlbumBox) node;
        if(child.getUserData() == null) {
          return false;
        }
      }

      LOG.info("End of albums reached, adding spacer");
      AlbumBox item = new AlbumBox(this, null, false);
      this.getChildren().add(item);
    }
    else {
      Album album = albumCollection.getAlbums().get(addIndex);
      LOG.info("Adding AlbumBox for " + album + " on position " + addIndex);
      ControllableItemPanel item = createControllableItemPanelFor(AlbumBox.class, album);
      this.getChildren().add((Node) item);
    }
    return true;
  }

  /**
   * Removes an album from the beginning
   */
  private void removeAlbumFromLeft() {
    Node node = this.getChildren().get(0);
    LOG.info("Removing " + node + " from the beginning.");
    this.getChildren().remove(0);
  }

  private boolean appendAlbumOnLeft() {
    int index = getSelectionIndex();
    if(index <= 1) {
      //check if it has not been added already
      for(Node node : this.getChildren()) {
        if(node.getUserData() == null) {
          return false;
        }
      }

      LOG.info("Beginning of albums reached, adding back button");
      AlbumBox item = new AlbumBox(this, null, true);
      this.getChildren().add(0, item);
    }
    else {
      int addIndex = index - (visibleItemCount - 1);
      Album album = albumCollection.getAlbums().get(addIndex);
      LOG.info("Adding AlbumBox for " + album + " on position " + addIndex);
      ControllableItemPanel item = createControllableItemPanelFor(AlbumBox.class, album);
      this.getChildren().add(0, (Node) item);
    }
    return true;
  }

  private void removeAlbumFromRight() {
    int size = this.getChildren().size();
    Node remove = this.getChildren().remove(size - 1);
    LOG.info("Removing " + remove + " from the end.");
  }
}
