package de.calette.mephisto3.ui.google;

import callete.api.services.music.model.AlbumCollection;
import de.calette.mephisto3.ui.ControllableItemPanel;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.text.Text;

/**
 * The box that is shown for each letter.
 */
public class AlbumLetterBox extends ControllableItemPanel {
  public final static int LETTER_BOX_WIDTH = 40;

  private Text text;

  public AlbumLetterBox(AlbumCollection collection) {
    super(0, collection);

    setMinWidth(LETTER_BOX_WIDTH);
    setAlignment(Pos.TOP_CENTER);
    text = new Text(collection.getLetter());
    text.getStyleClass().add("album-key");
    getChildren().add(text);
  }

  @Override
  protected double getScaleFactor() {
    return 1.3;
  }

  @Override
  protected Node getScalingNode() {
    return text;
  }
}
