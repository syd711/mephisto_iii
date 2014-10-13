package de.calette.mephisto3.ui.radio;

import callete.api.Callete;
import callete.api.services.ServiceModel;
import callete.api.services.music.model.Stream;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.ui.ControllablePanel;

/**
 * All components for the Radio control.
 */
public class RadioStationPanel extends ControllablePanel {


  public RadioStationPanel() {
    super(Callete.getStreamingService().getStreams());
    setMinWidth(Mephisto3.WIDTH);

    for (ServiceModel stream : models) {
      StreamPanel streamPanel = new StreamPanel((Stream) stream);
      getChildren().add(streamPanel);
    }
  }
}
