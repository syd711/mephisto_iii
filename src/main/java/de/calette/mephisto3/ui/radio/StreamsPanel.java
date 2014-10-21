package de.calette.mephisto3.ui.radio;

import callete.api.Callete;
import callete.api.services.ServiceModel;
import callete.api.services.music.model.Stream;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.ui.ControllablePanel;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.scene.Node;

/**
 * All components for the Radio control.
 */
public class StreamsPanel extends ControllablePanel {


  public StreamsPanel() {
    super(Callete.getStreamingService().getStreams());
    setMinWidth(Mephisto3.WIDTH);
  }

  @Override
  public void showPanel() {
    TransitionUtil.createInFader(this).play();

    for (ServiceModel stream : models) {
      StreamPanel streamPanel = new StreamPanel((Stream) stream);
      getChildren().add(streamPanel);
      streamPanel.show();
    }
  }

  @Override
  public void hidePanel() {
    this.setOpacity(0);
    for(Node child : getChildren()) {
      child.setOpacity(0);
    }
  }
}
