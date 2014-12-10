package de.calette.mephisto3.ui.radio;

import callete.api.Callete;
import callete.api.services.music.model.Stream;
import callete.api.services.music.player.PlaylistMetaData;
import callete.api.services.music.player.PlaylistMetaDataChangeListener;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.util.ComponentUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.commons.lang.StringUtils;

/**
 * Panel that displays the default of the given stream.
 * The panel updates through a PlaylistMetaDataChangeListener
 * that is fired for each meta data change the MPD monitoring thread detects.
 */
public class StreamPanel extends StackPane implements PlaylistMetaDataChangeListener {
  private final static String NO_DATA_TITLE = " - keine Informationen verfügbar -";

  private Stream stream;
  private Label artistLabel;
  private Label titleLabel;

  public StreamPanel(Stream stream) {
    this.stream = stream;
    Callete.getMusicPlayer().getPlaylist().addMetaDataChangeListener(this);

    showUI();
  }

  private void showUI() {
    VBox root = new VBox(20);
    root.setPadding(new Insets(30, 30, 30, 30));
    root.setMinWidth(Mephisto3.WIDTH);

    ComponentUtil.createLabel(stream.getName(), "stream-name", root);
    artistLabel = ComponentUtil.createLabel(applyArtist(stream.getArtist()), "stream-artist", root);
    titleLabel = ComponentUtil.createLabel(applyTitle(stream.getTitle()), "stream-title", root);
    root.getChildren().add(new Text("\n"));
    ComponentUtil.createLabel(stream.getPlaybackUrl(), "stream-url", root);

    root.getStyleClass().add("stream-panel");
    getChildren().add(root);
  }

  @Override
  public void updateMetaData(final PlaylistMetaData metaData) {
    if(!metaData.getItem().equals(stream)) {
      return;
    }
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        if(metaData.getItem().equals(stream))
        artistLabel.setText(applyArtist(metaData.getArtist()));
        titleLabel.setText(applyTitle(metaData.getTitle()));
      }
    });
  }

  private String applyArtist(String artist) {
    if(StringUtils.isEmpty(artist)) {
      artist = NO_DATA_TITLE;
    }
    return artist;
  }

  private String applyTitle(String title) {
    if(StringUtils.isEmpty(title)) {
      title = "";
    }
    return title;
  }
}
