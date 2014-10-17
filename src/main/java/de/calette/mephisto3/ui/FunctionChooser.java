package de.calette.mephisto3.ui;

import de.calette.mephisto3.control.ControlListener;
import de.calette.mephisto3.control.ServiceControlEvent;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.resources.ResourceLoader;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The chooser that selects the active function of the radio.
 */
public class FunctionChooser implements ControlListener {

  private HBox overlay;
  private Stage dialog;
  private Text music;
  private BorderPane root;

  public FunctionChooser(BorderPane root) {
    this.root = root;

    dialog = new Stage();
    overlay = new HBox(0);
    overlay.setAlignment(Pos.CENTER);
    overlay.setId("chooser");
    overlay.setMinWidth(700);
    overlay.setMinHeight(80);

    HBox scroller = new HBox(40);
    scroller.setAlignment(Pos.CENTER);

    Text radio = new Text("Radio");
    radio.getStyleClass().add("function-name");
    Text weather = new Text("Weather");
    weather.getStyleClass().add("function-name");
    music = new Text("Music");
    music.getStyleClass().add("function-name");
    Text settings = new Text("Settings");
    settings.getStyleClass().add("function-name");

    scroller.getChildren().add(radio);
    scroller.getChildren().add(weather);
    scroller.getChildren().add(music);
    scroller.getChildren().add(settings);

    overlay.getChildren().add(scroller);
    Scene scene = new Scene(overlay);
    scene.getStylesheets().add(ResourceLoader.getResource("theme.css"));
    scene.setFill(null);
    dialog.initStyle(StageStyle.TRANSPARENT);
    dialog.setScene(scene);

    ServiceController.getInstance().addControlListener(this);
  }

  public void show() {
    dialog.show();
    final FadeTransition inFader = TransitionUtil.createInFader(overlay, 500);
    inFader.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        TransitionUtil.createScaler(music, 200).play();
      }
    });
    inFader.play();

  }

  public boolean visible() {
    return dialog.isShowing();
  }

  @Override
  public void controlEvent(ServiceControlEvent event) {
    if (dialog.isShowing()) {
      if(event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PUSH)) {
        root.setEffect(new GaussianBlur(0));
        final FadeTransition outFader = TransitionUtil.createOutFader(overlay, 500);
        outFader.setOnFinished(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent actionEvent) {
            dialog.hide();
          }
        });
        outFader.play();
      }
      else if(event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.NEXT)) {

      }
      else if(event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PREVIOUS)) {

      }
    }
  }
}
