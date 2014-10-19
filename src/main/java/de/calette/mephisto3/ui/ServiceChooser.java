package de.calette.mephisto3.ui;

import callete.api.Callete;
import callete.api.services.Service;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.control.ControlListener;
import de.calette.mephisto3.control.ServiceControlEvent;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.resources.ResourceLoader;
import de.calette.mephisto3.util.TransitionQueue;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * The chooser that selects the active function of the radio.
 */
public class ServiceChooser implements ControlListener {

  public static final int SERVICE_BOX_WIDTH = 160;
  public static final double SELECTION_SCALE_FACTOR = 1.4;
  public static final int DISPLAY_DELAY = 500;

  private HBox overlay;
  private Stage dialog;

  private BorderPane root;

  private HBox scroller;

  private int index = 0;
  private List<Text> serviceBoxes = new ArrayList<>();
  private TransitionQueue transitionQueue;

  public ServiceChooser(BorderPane root) {
    this.root = root;

    dialog = new Stage();
    overlay = new HBox();
    overlay.setAlignment(Pos.CENTER);
    overlay.setId("chooser");
    overlay.setMinWidth(Mephisto3.WIDTH);
    overlay.setMinHeight(80);

    scroller = new HBox();
    scroller.setPadding(new Insets(0, 0, 0, 480));
    scroller.setAlignment(Pos.CENTER);

    transitionQueue = new TransitionQueue(scroller);

    scroller.getChildren().add(createServiceBox(ServiceController.SERVICE_NAME_RADIO, Callete.getStreamingService()));
    scroller.getChildren().add(createServiceBox(ServiceController.SERVICE_NAME_WEATHER, Callete.getWeatherService()));
    scroller.getChildren().add(createServiceBox(ServiceController.SERVICE_NAME_MUSIC, Callete.getGoogleMusicService()));
    scroller.getChildren().add(createServiceBox(ServiceController.SERVICE_NAME_SETTINGS, Callete.getSystemService()));

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
    TransitionUtil.createInFader(overlay, DISPLAY_DELAY).play();
    TransitionUtil.createScaler(serviceBoxes.get(index), ControllablePanel.SCROLL_DURATION, SELECTION_SCALE_FACTOR).play();
  }

  public boolean visible() {
    return dialog.isShowing();
  }

  @Override
  public void controlEvent(ServiceControlEvent event) {
    if (dialog.isShowing()) {
      if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PUSH)) {
        final FadeTransition blink = TransitionUtil.createBlink(serviceBoxes.get(index));
        blink.setOnFinished(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent actionEvent) {
            root.setEffect(new GaussianBlur(0));
            final FadeTransition outFader = TransitionUtil.createOutFader(overlay,DISPLAY_DELAY);
            outFader.setOnFinished(new EventHandler<ActionEvent>() {
              @Override
              public void handle(ActionEvent actionEvent) {
                dialog.hide();
              }
            });
            outFader.play();

            Platform.runLater(new Runnable() {
              @Override
              public void run() {
                final Service service = (Service) serviceBoxes.get(index).getUserData();
                ServiceController.getInstance().switchService(service);
              }
            });
          }
        });
        blink.play();
      } else if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.NEXT)) {
        scroll(-SERVICE_BOX_WIDTH);
      } else if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PREVIOUS)) {
        scroll(SERVICE_BOX_WIDTH);
      }
    }
  }

  // --------------- Helper -----------------------------

  private void scroll(int width) {
    if(index == serviceBoxes.size()-1 && width<0) {
      return;
    }
    if(index == 0 && width > 0 ) {
      return;
    }

    TranslateTransition tt = new TranslateTransition(Duration.millis(ControllablePanel.SCROLL_DURATION), scroller);
    tt.setByX(width);
    tt.setAutoReverse(false);
    transitionQueue.addTransition(tt);
    tt.play();

    TransitionUtil.createScaler(serviceBoxes.get(index), ControllablePanel.SCROLL_DURATION, 1.0).play();
    if(width > 0) {
      index--;
    }
    else {
      index++;
    }

    TransitionUtil.createScaler(serviceBoxes.get(index), ControllablePanel.SCROLL_DURATION, SELECTION_SCALE_FACTOR).play();
  }

  private HBox createServiceBox(String label, Service service) {
    Text text = new Text(label);
    text.setUserData(service);
    serviceBoxes.add(text);
    text.getStyleClass().add("service-name");
    HBox box = new HBox();
    box.setAlignment(Pos.CENTER);
    box.setMinWidth(SERVICE_BOX_WIDTH);
    box.getChildren().add(text);
    return box;
  }


}
