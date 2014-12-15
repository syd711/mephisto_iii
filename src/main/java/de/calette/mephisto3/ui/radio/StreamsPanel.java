package de.calette.mephisto3.ui.radio;

import callete.api.Callete;
import callete.api.services.music.model.Stream;
import callete.api.services.music.player.MusicPlayerPlaylist;
import callete.api.services.music.player.PlaylistMetaData;
import callete.api.services.music.player.PlaylistMetaDataChangeListener;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.control.ServiceState;
import de.calette.mephisto3.ui.ControllablePanel;
import de.calette.mephisto3.util.ComponentUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * All components for the Radio control.
 */
public class StreamsPanel extends ControllablePanel implements PlaylistMetaDataChangeListener {
  private final static String NO_DATA_TITLE = " - keine Informationen verfügbar -";

  private Stream stream;
  private Label artistLabel;
  private Label titleLabel;

  public StreamsPanel() {
    super(Callete.getStreamingService().getStreams());
    Callete.getMusicPlayer().getPlaylist().addMetaDataChangeListener(this);

    setMinWidth(Mephisto3.WIDTH);

    //initial station selection
    final List<Stream> streams = Callete.getStreamingService().getStreams();
    stream = streams.get(0);

    final MusicPlayerPlaylist playlist = Callete.getMusicPlayer().getPlaylist();
    playlist.setActiveItem(stream);
    Callete.getMusicPlayer().play();

    buildUI();
  }

  @Override
  public void pushed(ServiceState serviceState) {
    final int serviceIndex = serviceState.getServiceIndex();
    final Stream stream = (Stream) serviceState.getModels().get(serviceIndex);
    final MusicPlayerPlaylist playlist = Callete.getMusicPlayer().getPlaylist();
    playlist.setActiveItem(stream);
    Callete.getMusicPlayer().play();
  }

  @Override
  public void updateMetaData(final PlaylistMetaData metaData) {
    if(!metaData.getItem().equals(stream)) {
      return;
    }
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        if (metaData.getItem().equals(stream))
          artistLabel.setText(applyArtist(metaData.getArtist()));
        titleLabel.setText(applyTitle(metaData.getTitle()));
      }
    });
  }

  // ------------------------------- Helper -------------------------------------


  private void buildUI() {
    VBox root = new VBox(0);
    root.setMinWidth(Mephisto3.WIDTH);
    VBox labelBox = new VBox(20);
    root.getChildren().add(labelBox);
    labelBox.setPadding(new Insets(30, 30, 30, 30));

    ComponentUtil.createLabel(stream.getName(), "stream-name", labelBox);
    artistLabel = ComponentUtil.createLabel(applyArtist(stream.getArtist()), "stream-artist", labelBox);
    titleLabel = ComponentUtil.createLabel(applyTitle(stream.getTitle()), "stream-title", labelBox);
    root.getChildren().add(new Text("\n"));
    ComponentUtil.createLabel(stream.getPlaybackUrl(), "stream-url", labelBox);

    root.getStyleClass().add("stream-panel");
    getChildren().add(labelBox);

    HBox radioStatusBox = new HBox(5);
    radioStatusBox.setMinHeight(50);
    radioStatusBox.getStyleClass().add("stream-status-panel");

    root.getChildren().add(radioStatusBox);
    getChildren().add(root);
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
