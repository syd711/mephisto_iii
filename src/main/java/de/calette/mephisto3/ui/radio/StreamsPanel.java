package de.calette.mephisto3.ui.radio;

import callete.api.Callete;
import callete.api.services.ServiceModel;
import callete.api.services.music.model.Stream;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.ui.ControllablePanel;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * All components for the Radio control.
 */
public class StreamsPanel extends ControllablePanel {

  private Transition transition;

  public StreamsPanel() {
    super(Callete.getStreamingService().getStreams());
    setMinWidth(Mephisto3.WIDTH);
    for (ServiceModel stream : models) {
      StreamPanel streamPanel = new StreamPanel((Stream) stream);
      getChildren().add(streamPanel);
    }
    this.transition = TransitionUtil.createInFader(this);
    this.transition.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        ServiceController.getInstance().setControlEnabled(true);
      }
    });
  }

  @Override
  public void showPanel() {
    this.transition.play();
  }
}
