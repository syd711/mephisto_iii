package de.calette.mephisto3.ui.radio;

import callete.api.Callete;
import callete.api.services.music.model.Stream;
import callete.api.services.music.player.MusicPlayerPlaylist;
import callete.api.services.music.player.PlaylistMetaData;
import callete.api.services.music.player.PlaylistMetaDataChangeListener;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.control.ServiceState;
import de.calette.mephisto3.ui.ControllablePanel;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * All components for the Radio control.
 */
public class StreamsController extends ControllablePanel implements PlaylistMetaDataChangeListener {
  private final static Logger LOG = LoggerFactory.getLogger(StreamsController.class);

  private Stream selectedStream;
  private Stream activeStream;

  private PlaylistMetaData currentMetaData;
  private StreamsUI streamsUI;

  public StreamsController() {
    super(Callete.getStreamingService().getStreams());

    //initial station selection
    activeStream = (Stream) ServiceController.getInstance().getServiceState().getSelection();
    selectedStream = activeStream;

    //create the basic UI panel
    streamsUI = new StreamsUI(this, activeStream);
    getChildren().add(streamsUI);
  }

  //--------------------------- UI control ----------------------------------------------

  @Override
  public void pushed(ServiceState serviceState) {
    //reset meta data status
    currentMetaData = null;

    //save last selected state first
    Stream selection = (Stream) serviceState.getSelection();

    //check if the push button was pressed for the current selection, select next station then
    if(selection.equals(activeStream)) {
      ServiceController.getInstance().fireControlEvent(KeyCode.RIGHT);
      pushed(serviceState);
      return;
    }
    activeStream = selection;
    serviceState.saveState();

    //well, play the selected stream
    final MusicPlayerPlaylist playlist = Callete.getMusicPlayer().getPlaylist();
    playlist.setActiveItem(activeStream);
    Callete.getMusicPlayer().play();

    //update the UI
    updateUI(activeStream);
  }

  @Override
  protected void serviceStateChanged(ServiceState serviceState) {
    selectedStream = (Stream) serviceState.getSelection();
    updateUI(null);
  }

  //--------------------------- Event listeners  ----------------------------------------------

  @Override
  public void updateMetaData(final PlaylistMetaData metaData) {
    if(!metaData.getItem().equals(activeStream)) {
      return;
    }
    //store data for re-selection
    currentMetaData = metaData;

    //apply the labels if the current stream is the active stream
    updateUI(null);
  }

  // -------------------- Overridden UI states -----------------------------

  @Override
  public void showPanel() {
    startStreaming();
    Callete.getMusicPlayer().getPlaylist().addMetaDataChangeListener(this);
    streamsUI.showControl();
    super.showPanel();
  }

  @Override
  public void hidePanel() {
    Callete.getMusicPlayer().getPlaylist().removeMetaDataChangeListener(this);
    streamsUI.hideControl();
    super.hidePanel();
  }

  // ------------------- Helper --------------------------------------------

  private void updateUI(final Stream stream) {
    //if a stream is given, it's from a new selection, so activate it.
    if(stream != null) {
      streamsUI.activateStream(stream);
    }
    else if(selectedStream == activeStream) {
      streamsUI.selectActiveStream(activeStream, currentMetaData);
    }
    else {
      streamsUI.selectStream();
    }
  }

  /**
   * No matter if the UI is build yet, start playing the stream.
   */
  private void startStreaming() {
    final MusicPlayerPlaylist playlist = Callete.getMusicPlayer().getPlaylist();
    playlist.setActiveItem(activeStream);

    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        streamsUI.reset();
      }
    });

    Callete.getMusicPlayer().play();
    LOG.info("Starting playback of last stream selection: " + activeStream);
  }

}
