package de.calette.mephisto3;

import callete.api.util.SystemUtils;
import de.calette.mephisto3.gpio.GPIOController;
import de.calette.mephisto3.resources.ResourceLoader;
import de.calette.mephisto3.resources.weather.WeatherQuickInfoResourceLoader;
import de.calette.mephisto3.ui.Center;
import de.calette.mephisto3.ui.Footer;
import de.calette.mephisto3.ui.Header;
import de.calette.mephisto3.util.CSSDebugger;
import de.calette.mephisto3.util.ComponentUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * In the beginning, there was main...
 */
public class Mephisto3 extends Application {
  public static final int WIDTH = 700;
  public static final int HEIGHT= 395;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(final Stage primaryStage) {
    //force rendering of small fonts
    System.setProperty("prism.lcdtext", "false");

    //create root component with background
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root, (double) WIDTH, (double) HEIGHT);
    scene.getStylesheets().add(ResourceLoader.getResource("theme.css"));
    primaryStage.setScene(scene);
    primaryStage.getScene().setRoot(root);

    //header
    root.setTop(new Header());
    //center
    root.setCenter(new Center());
    //footer
    root.setBottom(new Footer());

    //apply debugging options on windows
    if (SystemUtils.isWindows()) {
      addStateListener(primaryStage);
      addDisposeListener(primaryStage);
      CSSDebugger.dump(root);
    }
    else {
      primaryStage.initStyle(StageStyle.UNDECORATED);
    }

    //finally show the stage
    primaryStage.show();

    //initialize GPIO
    GPIOController.getInstance();
  }


  private static void addStateListener(Stage primaryStage) {
    primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.RIGHT) {

        }
      }
    });
  }

  private static void addDisposeListener(Stage primaryStage) {
    //ensures that the process is terminated on window dispose
    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      @Override
      public void handle(WindowEvent windowEvent) {
        Platform.exit();
        System.exit(0);
      }
    });
  }
}
