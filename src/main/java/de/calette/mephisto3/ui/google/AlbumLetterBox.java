package de.calette.mephisto3.ui.google;

import callete.api.services.music.model.AlbumCollection;
import de.calette.mephisto3.ui.ControllableSelectorPanel;
import de.calette.mephisto3.ui.ControllableVBoxItemPanelBase;
import de.calette.mephisto3.util.ComponentUtil;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.text.Text;

/**
 * The box that is shown for each letter.
 */
public class AlbumLetterBox extends ControllableVBoxItemPanelBase {
  public final static int LETTER_BOX_WIDTH = 40;

  private Text text;

  public AlbumLetterBox(ControllableSelectorPanel parentControl, AlbumCollection collection) {
    super(0, parentControl, collection);

    setMinWidth(LETTER_BOX_WIDTH);
    setAlignment(Pos.TOP_CENTER);
    text = ComponentUtil.createText(collection.getLetter(), "album-key", this);
  }

  @Override
  public double getScaleFactor() {
    return 1.3;
  }

  @Override
  public Node getScalingNode() {
    return text;
  }
}
