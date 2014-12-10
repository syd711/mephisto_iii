package de.calette.mephisto3.ui.radio;

import callete.api.Callete;
import callete.api.services.ServiceModel;
import callete.api.services.music.model.Stream;
import callete.api.services.music.player.MusicPlayerPlaylist;
import callete.api.services.music.player.MusicPlayerService;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.control.ServiceState;
import de.calette.mephisto3.ui.ControllablePanel;

import java.util.List;

/**
 * All components for the Radio control.
 */
public class StreamsPanel extends ControllablePanel {

  public StreamsPanel() {
    super(Callete.getStreamingService().getStreams());
    setMinWidth(Mephisto3.WIDTH);
    for (ServiceModel stream : models) {
      StreamPanel streamPanel = new StreamPanel((Stream) stream);
      getChildren().add(streamPanel);
    }

    final List<Stream> streams = Callete.getStreamingService().getStreams();
    Stream stream = streams.get(0);
    final MusicPlayerPlaylist playlist = Callete.getMusicPlayer().getPlaylist();
    playlist.setActiveItem(stream);
    Callete.getMusicPlayer().play();
  }

  @Override
  public void pushed(ServiceState serviceState) {
    final int serviceIndex = serviceState.getServiceIndex();
    final Stream stream = (Stream)serviceState.getModels().get(serviceIndex);
    final MusicPlayerPlaylist playlist = Callete.getMusicPlayer().getPlaylist();
    playlist.setActiveItem(stream);
    Callete.getMusicPlayer().play();
  }
}
