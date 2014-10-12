package de.calette.mephisto3.ui;

import callete.api.Callete;
import callete.api.services.music.model.Stream;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.gpio.ControlEvent;
import de.calette.mephisto3.gpio.ControlListener;
import de.calette.mephisto3.gpio.GPIOController;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;

/**
 */
public class Center extends BorderPane implements ControlListener {

  private HBox hBox;
  private StackPane stackPane;
  private int modelCount;
  private int index = 0;

  public Center() {
    setPadding(new Insets(5, 15, 15, 15));
    stackPane = new StackPane();


    List<Stream> streams = Callete.getStreamingService().getStreams();
    modelCount = streams.size();
    hBox = new HBox(10);
    hBox.setMinWidth(700);

    for (Stream stream : streams) {
      StreamPanel streamPanel = new StreamPanel(stream);
      hBox.getChildren().add(streamPanel);
    }

    setCenter(stackPane);
    stackPane.getChildren().add(hBox);

    GPIOController.getInstance().addControlListener(this);
  }

  @Override
  public void controlEvent(ControlEvent event) {
    if(event.equals(ControlEvent.NEXT) ||event.equals(ControlEvent.PREVIOUS)) {
      int offset = -Mephisto3.WIDTH+40;
      if(event.equals(ControlEvent.NEXT)) {
        offset = Mephisto3.WIDTH-40;
      }

      TranslateTransition tt = new TranslateTransition(Duration.millis(500), hBox);
      tt.setByX(offset);
      tt.setAutoReverse(false);
      tt.play();
    }
    else if(event.equals(ControlEvent.LONG_PUSH)) {
      Text test = new Text("test");
      test.getStyleClass().add("stream-name");
      stackPane.getChildren().add(test);
    }
  }
}
