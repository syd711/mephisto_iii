package de.calette.mephisto3.ui.radio;

import de.calette.mephisto3.resources.ResourceLoader;
import de.calette.mephisto3.util.ComponentUtil;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Displays the current status of the media player.
 */
public class StreamStatusBox extends BorderPane {

  private Image defaultBackground = new Image(ResourceLoader.getResource("player-background.png"), 40, 40, false, true);
  private ImageView imageView;
  private Label nameLabel;
  private Label titleLabel;

  public StreamStatusBox() {
    setMinHeight(50);
    getStyleClass().add("player-status-panel");

    setLeft(createImageBox());
    setCenter(createStatusBox());
  }

  public void setTitle(String title) {
    titleLabel.setText(title);
  }

  public void setName(String name) {
    nameLabel.setText(name);
  }

  //---------- Helper -------------------------------------------------------

  private Node createStatusBox() {
    VBox status = new VBox(3);
    status.setPadding(new Insets(3,3,3,8));
    nameLabel = ComponentUtil.createCustomLabel("", "player-name-label", status);
    nameLabel.getStyleClass().remove("label");
    titleLabel = ComponentUtil.createCustomLabel("", "player-title-label", status);
    titleLabel.getStyleClass().remove("label");
    return status;
  }

  private Node createImageBox() {
    HBox wrapper = new HBox(5);
    wrapper.setPadding(new Insets(3, 2, 2, 4));
    HBox imageBox = new HBox();
    imageBox.setMaxHeight(42);
    imageBox.getStyleClass().add("player-status-image");
    imageView = new ImageView(defaultBackground);
    imageBox.getChildren().add(imageView);
    wrapper.getChildren().add(imageBox);
    return wrapper;
  }

  public void setImage(Image image) {
    if(image == null) {
      this.imageView.setImage(defaultBackground);
    }
    else {
      this.imageView.setImage(image);
    }
  }
}
