package de.calette.mephisto3.ui.google;

import callete.api.services.music.model.AlbumCollection;
import de.calette.mephisto3.resources.menu.MenuResourceLoader;
import de.calette.mephisto3.ui.ControllableSelectorPanel;
import de.calette.mephisto3.ui.ServiceChooser;
import de.calette.mephisto3.util.ComponentUtil;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

import java.util.List;

/**
 * UI Panel for selecting the album album sorted by name or artist.
 */
public class AlbumLetterSelector extends ControllableSelectorPanel<AlbumCollection> {
  private ServiceChooser serviceChooser;
  private List<AlbumCollection> collections;

  public AlbumLetterSelector(ServiceChooser serviceChooser, Pane parent, List<AlbumCollection> collections) {
    super(0, parent, AlbumLetterBox.LETTER_BOX_WIDTH, collections, AlbumLetterBox.class);
    setBackButton(0);
    this.collections = collections;
    this.serviceChooser = serviceChooser;
  }

  @Override
  public void showPanel() {
    super.showPanel();
    Canvas selectorCanvas = ComponentUtil.createImageCanvas(MenuResourceLoader.getResource("selector.png"), 40, 40);
    getParentPane().getChildren().add(selectorCanvas);
  }

  @Override
  public void hidePanel() {
    super.hidePanel();
    getParentPane().getChildren().clear();
  }

  @Override
  protected int getTopPadding() {
    return 140;
  }

  @Override
  protected void onHide(AlbumCollection albumCollection) {
    //album selected
    if (albumCollection != null) {
      final AlbumSlider albumSlider = new AlbumSlider(serviceChooser, getParentPane(), collections, albumCollection);
      albumSlider.showPanel();
    }
    //back button selected
    else {
      serviceChooser.showServiceChooser();
    }
  }
}
