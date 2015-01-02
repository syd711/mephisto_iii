package de.calette.mephisto3.ui.radio;

import callete.api.Callete;
import callete.api.services.music.model.Stream;
import callete.api.services.music.player.PlaylistMetaData;
import callete.api.services.resources.ArtistResources;
import callete.api.services.resources.ImageResource;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.ui.ControllablePanel;
import de.calette.mephisto3.ui.ServiceScroller;
import de.calette.mephisto3.util.ComponentUtil;
import de.calette.mephisto3.util.Executor;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * All UI components of the stream panel
 */
public class StreamsUI extends VBox {
  private final static Logger LOG = LoggerFactory.getLogger(StreamsUI.class);

  public final static String NO_DATA_TITLE = " - keine Informationen verfügbar -";
  public final static String LOADING_DATA_TITLE = "Warte auf Metadaten...";

  private final static int MINIMUM_IMAGE_SIZE = 400;
  private final static int IMAGE_HEIGHT = 345;
  private final static int PLAYER_MINIMUM_IMAGE_SIZE = 45;
  private final static int PLAYER_IMAGE_SIZE = 42;

  private ServiceScroller serviceScroller = new ServiceScroller();
  private ImageView artistBackgroundImageView;
  private Label nameLabel;
  private Label artistLabel;
  private Label titleLabel;
  private HBox imageLoader;
  private ProgressIndicator metaDataBusyIndicator;
  private ProgressIndicator imageDataBusyIndicator;
  private Image artistBackgroundImage;
  private Image artistStatusImage;
  private ArtistResources artistResources;

  private StreamStatusBox playerStatusBox;
  private boolean imageLoaderActive = false;

  public StreamsUI(ControllablePanel parent, Stream stream) {
    createUI(parent, stream);
  }

  // ------------------------- UI State Updates ----------------------------------------

  public void reset() {
    Stream stream = (Stream) ServiceController.getInstance().getServiceState().getSelection();
    activateStream(stream);
    playerStatusBox.updateStatus(stream.getName(), "", null);
  }

  /**
   * Called when another stream has been selected but not confirmed for playback yet.
   */
  public void selectStream() {
    Platform.runLater(() -> {
      imageLoader.setOpacity(0);
      metaDataBusyIndicator.setOpacity(0);
      Stream stream = (Stream) ServiceController.getInstance().getServiceState().getSelection();
      nameLabel.setText(stream.getName());
      artistLabel.setText(NO_DATA_TITLE);
      titleLabel.setText("");

      removeImageClasses();
      setBackgroundImage(null);
    });
  }

  /**
   * Called when the playback of a newly selected station should start.
   */
  public void activateStream(Stream stream) {
    artistResources = null;
    artistBackgroundImage = null;

    Platform.runLater(() -> {
      removeImageClasses();
      nameLabel.setText(applyName(stream, null));
      artistLabel.setText(applyArtist(null));
      titleLabel.setText(applyTitle(null));

      playerStatusBox.updateStatus(stream.getName(), "", null);
      setBackgroundImage(null);
    });
  }

  /**
   * Called when the active stream has been re-selected, the stored metadata must be re-applied.
   */
  public void selectActiveStream(Stream stream, final PlaylistMetaData currentMetaData) {
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        nameLabel.setText(applyName(stream, currentMetaData));
        artistLabel.setText(applyArtist(currentMetaData));
        titleLabel.setText(applyTitle(currentMetaData));

        if(currentMetaData != null && !StringUtils.isEmpty(currentMetaData.getArtist()) && !StringUtils.isEmpty(currentMetaData.getTitle())) {
          playerStatusBox.updateStatus(currentMetaData.getArtist() + " - " + currentMetaData.getTitle());
        }
      }
    });


    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        //only apply additional data if metadata is available.
        if (currentMetaData == null || StringUtils.isEmpty(currentMetaData.getArtist())) {
          LOG.debug("No meta data available or no artist found, resetting UI.");
          removeImageClasses();
          setBackgroundImage(null);
          return;
        }

        //the artist loader is already loading
        if(imageLoaderActive) {
          LOG.debug("Image loader already active, skipping resource request.");
          return;
        }

        //first check if an image has already been requested
        if (artistResources == null || !artistResources.getArtist().equals(currentMetaData.getArtist())) {
          imageLoader.setOpacity(1);
          setBackgroundImage(null);
          loadArtistResource(currentMetaData);
        }

        //apply image if already available
        if (artistResources != null && artistResources.getArtist().equals(currentMetaData.getArtist()) && artistBackgroundImage != null) {
          playerStatusBox.updateStatus(null, null, artistStatusImage);
          addImageClasses();
          setBackgroundImage(artistBackgroundImage);
          if(imageLoader.getOpacity() != 0) {
            imageLoader.setOpacity(0);
          }
        }

        //hide the image loader if no images are available
        if(artistResources != null && artistResources.isEmpty()) {
          if(imageLoader.getOpacity() != 0) {
            LOG.debug("No resources found, hiding image loader.");
            imageLoader.setOpacity(0);
          }
        }
      }
    });
  }

  // -------------------- Helper -------------------------------------------

  /**
   * Asynchronously loading of image resources.
   * @param currentMetaData the meta data used to retrieve the image data
   */
  private void loadArtistResource(final PlaylistMetaData currentMetaData) {
    imageLoaderActive = true;
    Callete.getMusicPlayer().enableMonitoring(false);
    Executor.run(new Runnable() {
      @Override
      public void run() {
        LOG.debug("Requesting resources for " + currentMetaData.getArtist());
        artistResources = Callete.getResourcesService().getImageResourcesFor(currentMetaData.getArtist());
        if (!artistResources.isEmpty()) {
          LOG.debug("Creating images for " + currentMetaData.getArtist());
          ImageResource randomPlayerImage = artistResources.getRandomImage(PLAYER_IMAGE_SIZE, PLAYER_IMAGE_SIZE, PLAYER_MINIMUM_IMAGE_SIZE);
          ImageResource img = artistResources.getRandomImage(Mephisto3.WIDTH, IMAGE_HEIGHT, MINIMUM_IMAGE_SIZE);
          if(img != null) {
            artistBackgroundImage = ComponentUtil.toFXImage(img);
          }
          else {
            artistBackgroundImage = null;
          }
          if(randomPlayerImage != null) {
            artistStatusImage = ComponentUtil.toFXImage(randomPlayerImage);
          }
          else {
            artistStatusImage = null;
          }
        }
        imageLoaderActive = false;
        Callete.getMusicPlayer().enableMonitoring(true);
      }
    });
  }

  private void addImageClasses() {
    nameLabel.getStyleClass().remove("stream-name");
    nameLabel.getStyleClass().add("stream-name-active");
    artistLabel.getStyleClass().remove("stream-artist");
    artistLabel.getStyleClass().add("stream-artist-active");
    titleLabel.getStyleClass().remove("stream-title");
    titleLabel.getStyleClass().add("stream-title-active");
  }

  private void removeImageClasses() {
    nameLabel.getStyleClass().clear();
    nameLabel.getStyleClass().addAll("label", "label-defaults", "stream-name");
    artistLabel.getStyleClass().clear();
    artistLabel.getStyleClass().addAll("label", "label-defaults", "stream-artist");
    titleLabel.getStyleClass().clear();
    titleLabel.getStyleClass().addAll("label", "label-defaults", "stream-title");
  }

  private String applyArtist(PlaylistMetaData metaData) {
    if (metaData == null) {
      metaDataBusyIndicator.setOpacity(1);
      return LOADING_DATA_TITLE;
    }
    metaDataBusyIndicator.setOpacity(0);
    if (StringUtils.isEmpty(metaData.getArtist()) || String.valueOf(metaData.getArtist()).equals(metaData.getTitle())) {
      return NO_DATA_TITLE;
    }
    return metaData.getArtist();
  }

  private String applyTitle(PlaylistMetaData metaData) {
    if (metaData == null) {
      return "";
    }
    if (StringUtils.isEmpty(metaData.getTitle()) || String.valueOf(metaData.getArtist()).equals(String.valueOf(metaData.getTitle()))) {
      return "";
    }
    return metaData.getTitle();
  }

  private String applyName(Stream stream, PlaylistMetaData metaData) {
    if (metaData != null && StringUtils.isEmpty(metaData.getName()) && metaData.getName().equals(stream.getName())) {
      return metaData.getName();
    }
    return stream.getName();
  }

  private void setBackgroundImage(Image image) {
    //method is called for an image that is already applied
    if (image != null && artistBackgroundImageView.getImage() != null && artistBackgroundImageView.getImage().equals(image)) {
      //only check if the image is visible
      if(artistBackgroundImageView.getOpacity() == 0) {
        TransitionUtil.createInFader(artistBackgroundImageView, 100).play();
      }
      return;
    }

    //hide image and already hidden? ignore call
    if(image == null && artistBackgroundImageView.getOpacity() == 0) {
      return;
    }

    if(artistBackgroundImageView.getImage() != null && artistBackgroundImageView.getOpacity() == 1) {
      FadeTransition outFader = TransitionUtil.createOutFader(artistBackgroundImageView, 100);
      outFader.setOnFinished(actionEvent -> {
        if (image != null) {
          artistBackgroundImageView.setImage(image);
          if(image != null) {
            addImageClasses();
          }
          TransitionUtil.createInFader(artistBackgroundImageView, 100).play();
        }
      });
      outFader.play();
    }
    else {
      artistBackgroundImageView.setImage(image);
      FadeTransition inFader = TransitionUtil.createInFader(artistBackgroundImageView, 100);
      inFader.setOnFinished(new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent event) {
          if(image != null) {
            addImageClasses();
          }
        }
      });
      inFader.play();
    }
  }

  /**
   * Only create the UI, using the initial stream as default which is also
   * played at the start of the radio UI.
   *
   * @param parent
   * @param stream the initially activated stream.
   */
  private void createUI(ControllablePanel parent, Stream stream) {
    setMinWidth(Mephisto3.WIDTH);
    VBox labelBox = new VBox(20);
    labelBox.getStyleClass().add("streams-panel");
    labelBox.setMinHeight(Mephisto3.HEIGHT - 60);
    labelBox.setAlignment(Pos.TOP_LEFT);
    getChildren().add(labelBox);

    getChildren().add(serviceScroller);
    labelBox.setPadding(new Insets(20, 30, 30, 30));


    artistBackgroundImageView = new ImageView();
    ColorAdjust brightness = new ColorAdjust();
    brightness.setBrightness(-0.3);
    artistBackgroundImageView.setEffect(brightness);
    parent.getChildren().add(artistBackgroundImageView);

    nameLabel = ComponentUtil.createLabel(stream.getName(), "stream-name", labelBox);

    HBox artistLabelBox = new HBox();
    artistLabelBox.setMaxHeight(28);
    artistLabel = ComponentUtil.createLabel(LOADING_DATA_TITLE, "stream-artist", artistLabelBox);
    labelBox.getChildren().add(artistLabelBox);
    metaDataBusyIndicator = new ProgressIndicator();
    artistLabelBox.getChildren().add(metaDataBusyIndicator);
    titleLabel = ComponentUtil.createLabel("", "stream-title", labelBox);

    HBox spacer = new HBox();
    spacer.setMinHeight(70);
    labelBox.getChildren().add(spacer);

    imageLoader = new HBox();
    imageLoader.setOpacity(0);
    imageLoader.setMinHeight(20);
    imageLoader.setMaxWidth(Mephisto3.WIDTH);
    imageLoader.setAlignment(Pos.CENTER_RIGHT);
    labelBox.getChildren().add(imageLoader);
    ComponentUtil.createLabel("Suche nach Bildern...", "", imageLoader);
    imageDataBusyIndicator = new ProgressIndicator();
    imageLoader.getChildren().add(imageDataBusyIndicator);

    playerStatusBox = new StreamStatusBox();
    getChildren().add(playerStatusBox);

    //set the initial UI state
    playerStatusBox.updateStatus(stream.getName(), null, null);
  }

  public void hideControl() {
    serviceScroller.hideScroller();
  }

  public void showControl() {
    serviceScroller.showScroller();
  }
}
