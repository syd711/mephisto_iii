package de.calette.mephisto3.ui.radio;

import callete.api.Callete;
import callete.api.services.music.model.Stream;
import callete.api.services.music.player.MusicPlayerPlaylist;
import callete.api.services.music.player.PlaylistMetaData;
import callete.api.services.music.player.PlaylistMetaDataChangeListener;
import callete.api.services.music.resources.ArtistResources;
import callete.api.services.music.resources.ImageResource;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.control.ServiceState;
import de.calette.mephisto3.resources.ResourceLoader;
import de.calette.mephisto3.ui.ControllablePanel;
import de.calette.mephisto3.ui.PlayerStatusBox;
import de.calette.mephisto3.ui.ServiceScroller;
import de.calette.mephisto3.util.ComponentUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * All components for the Radio control.
 */
public class StreamsPanel extends ControllablePanel implements PlaylistMetaDataChangeListener {
  private final static Logger LOG = LoggerFactory.getLogger(StreamsPanel.class);

  private final static int IMAGE_SIZE = 400;
  private final static String NO_DATA_TITLE = " - keine Informationen verfügbar -";
  private final static String LOADING_DATA_TITLE = "Warte auf Metadaten...";

  private Label nameLabel;
  private Label artistLabel;
  private Label titleLabel;
  private Label urlLabel;

  private ImageView imageView;
  private Image randomFXImage;
  private Image defaultBackground = new Image(ResourceLoader.getResource("radio_background.png"), 700, 345, false, true);
  private VBox root;

  private Stream selectedStream;
  private Stream activeStream;

  private PlaylistMetaData currentMetaData;
  private ServiceScroller serviceScroller = new ServiceScroller();
  private PlayerStatusBox playerStatusBox = new PlayerStatusBox();
  private ArtistResources artistResources;

  public StreamsPanel() {
    super(Callete.getStreamingService().getStreams());
    setMinWidth(Mephisto3.WIDTH);

    startStreaming();
    buildUI(activeStream);
  }

  @Override
  public void pushed(ServiceState serviceState) {
    randomFXImage = null;
    activeStream = (Stream) serviceState.getSelection();
    serviceState.saveState();
    final MusicPlayerPlaylist playlist = Callete.getMusicPlayer().getPlaylist();
    playlist.setActiveItem(activeStream);
    Callete.getMusicPlayer().play();
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        playerStatusBox.setName(activeStream.getName());
        playerStatusBox.setTitle("");
        playerStatusBox.setImage(null);
        artistLabel.setText(LOADING_DATA_TITLE);
      }
    });
  }

  @Override
  public void updateMetaData(final PlaylistMetaData metaData) {
    if (!metaData.getItem().equals(activeStream)) {
      return;
    }
    //store data for re-selection
    currentMetaData = metaData;

    //apply the labels if the current stream is the active stream
    if(activeStream.equals(selectedStream)) {
      Platform.runLater(new Runnable() {
        @Override
        public void run() {
          nameLabel.setText(applyName(metaData));
          artistLabel.setText(applyArtist(metaData));
          titleLabel.setText(applyTitle(metaData));
          playerStatusBox.applyMetaData(metaData);
        }
      });
    }

    //apply the background image: image there is no artist, use the default background
    if (StringUtils.isEmpty(metaData.getArtist())) {
      if(activeStream.equals(selectedStream) && !imageView.getImage().equals(defaultBackground)) {
        imageView.setImage(defaultBackground);
      }
      return;
    }

    //ok, so there is an artist, check if is has been applied yet or if the artist has changed.
    if(artistResources == null || !artistResources.getArtist().equals(metaData.getArtist())) {
      //reset existing image so that the new is applied
      randomFXImage = null;
      artistResources = Callete.getArtistResourcesService().getImageResourcesFor(metaData.getArtist());
    }

    //no data found for artist, so apply default image again
    if(artistResources.isEmpty()) {
      if(!imageView.getImage().equals(defaultBackground)) {
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            imageView.setImage(defaultBackground);
          }
        });
      }
      return;
    }

    //apply a new artist image if there there is a available resource bundle.
    if(randomFXImage == null && artistResources != null) {
      ImageResource randomImage = artistResources.getRandomImage(Mephisto3.WIDTH, 345, IMAGE_SIZE);
      ImageResource randomPlayerImage = artistResources.getRandomImage(42,42, 45);
      if(randomImage != null) {
        randomFXImage = ComponentUtil.toFXImage(randomImage);
        final Image randomPlayerFXImage = ComponentUtil.toFXImage(randomPlayerImage);
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            //the user may have selected another stream while the image has been loaded.
            if(activeStream.equals(selectedStream)) {
              imageView.setImage(randomFXImage);
              playerStatusBox.setImage(randomPlayerFXImage);
            }
          }
        });
      }
    }
  }

  @Override
  public void showPanel() {
    Callete.getMusicPlayer().getPlaylist().addMetaDataChangeListener(this);
    serviceScroller.showScroller();
    super.showPanel();
  }

  @Override
  public void hidePanel() {
    Callete.getMusicPlayer().getPlaylist().removeMetaDataChangeListener(this);
    serviceScroller.hideScroller();
    super.hidePanel();
  }

  @Override
  protected void serviceStateChanged(ServiceState serviceState) {
    selectedStream = (Stream) serviceState.getSelection();
    nameLabel.setText(selectedStream.getName());
    urlLabel.setText(selectedStream.getPlaybackUrl());

    if (selectedStream == activeStream) {
      if (randomFXImage != null) {
        imageView.setImage(randomFXImage);
      }
      else {
        imageView.setImage(defaultBackground);
      }
      artistLabel.setText(applyArtist(currentMetaData));
      titleLabel.setText(applyTitle(currentMetaData));
    }
    else {
      imageView.setImage(defaultBackground);
      artistLabel.setText(NO_DATA_TITLE);
      titleLabel.setText("");
    }
  }

  // ------------------------------- Helper -------------------------------------

  /**
   * No matter if the UI is build yet, start playing the stream.
   */
  private void startStreaming() {
    //initial station selection
    activeStream = (Stream) ServiceController.getInstance().getServiceState().getSelection();
    selectedStream = activeStream;

    final MusicPlayerPlaylist playlist = Callete.getMusicPlayer().getPlaylist();
    playlist.setActiveItem(activeStream);
    Callete.getMusicPlayer().play();
    LOG.info("Starting playback of last stream selection: " + activeStream);
  }


  private void buildUI(Stream stream) {
    root = new VBox(0);
    root.setMinWidth(Mephisto3.WIDTH);
    VBox labelBox = new VBox(20);
    labelBox.getStyleClass().add("streams-panel");
    labelBox.setMinHeight(Mephisto3.HEIGHT - 60);
    labelBox.setAlignment(Pos.TOP_LEFT);
    root.getChildren().add(labelBox);
    labelBox.setPadding(new Insets(20, 30, 30, 30));


    imageView = new ImageView(defaultBackground);
    ColorAdjust brightness = new ColorAdjust();
    brightness.setBrightness(-0.3);
    imageView.setEffect(brightness);
    getChildren().add(imageView);

    nameLabel = ComponentUtil.createLabel(stream.getName(), "stream-name", labelBox);
    artistLabel = ComponentUtil.createLabel(NO_DATA_TITLE, "stream-artist", labelBox);
    titleLabel = ComponentUtil.createLabel("", "stream-title", labelBox);
    HBox spacer = new HBox();
    spacer.setMinHeight(50);
    labelBox.getChildren().add(spacer);
    urlLabel = ComponentUtil.createLabel(stream.getPlaybackUrl(), "stream-url", labelBox);

    root.getStyleClass().add("stream-panel");
    root.getChildren().add(serviceScroller);

    root.getChildren().add(playerStatusBox);
    getChildren().add(root);
  }

  private String applyArtist(PlaylistMetaData metaData) {
    if(metaData == null) {
      return NO_DATA_TITLE;
    }
    if (StringUtils.isEmpty(metaData.getArtist()) || String.valueOf(metaData.getArtist()).equals(metaData.getTitle())) {
      return NO_DATA_TITLE;
    }
    return metaData.getArtist();
  }

  private String applyTitle(PlaylistMetaData metaData) {
    if(metaData == null){
      return "";
    }
    if (StringUtils.isEmpty(metaData.getTitle()) || String.valueOf(metaData.getArtist()).equals(String.valueOf(metaData.getTitle()))) {
      return "";
    }
    return metaData.getTitle();
  }

  private String applyName(PlaylistMetaData metaData) {
    if (StringUtils.isEmpty(metaData.getName()) && metaData.getName().equals(activeStream.getName())) {
      return metaData.getName();
    }
    return activeStream.getName();
  }
}
