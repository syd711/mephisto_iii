package de.calette.mephisto3;

import callete.api.Callete;
import callete.api.services.impl.music.google.AlbumCoverCache;
import callete.api.util.SystemUtils;
import de.calette.mephisto3.resources.ResourceLoader;
import de.calette.mephisto3.ui.Center;
import de.calette.mephisto3.util.NodeDebugger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.File;

/**
 * In the beginning, there was main...
 */
public class Mephisto3 extends Application implements EventHandler<KeyEvent> {
  public static final int WIDTH = 700;
  public static final int HEIGHT = 395;
  private boolean debug = Callete.getConfiguration().getBoolean("debug", false);
  private StackPane rootStack;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(final Stage primaryStage) {
    Callete.getGPIOService().setSimulationMode(debug);

    //force rendering of small fonts
    System.setProperty("prism.lcdtext", "false");

    //apply new image cache dir
    if(!SystemUtils.isWindows()) {
      AlbumCoverCache.setCacheDir(new File("../image_cache/"));
    }

    //create root component with background
    rootStack = new StackPane();
    rootStack.getChildren().add(new Center());

    Scene scene = new Scene(rootStack, (double) WIDTH, (double) HEIGHT);
    scene.getStylesheets().add(ResourceLoader.getResource("theme.css"));
    primaryStage.setScene(scene);
    primaryStage.getScene().setRoot(rootStack);

    primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, new Mephisto3KeyEventFilter());
    primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, this);
    addDisposeListener(primaryStage);

    //apply debugging options on windows
    if (debug) {
//      NodeDebugger.dump(rootStack);
    }
    else {
      primaryStage.initStyle(StageStyle.UNDECORATED);
    }

    //finally show the stage
    primaryStage.show();
  }

  //--------------------------- Helper --------------------------------------------

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

  @Override
  public void handle(KeyEvent event) {
    KeyCode code = event.getCode();
    if(code == KeyCode.Q || code == KeyCode.ESCAPE) {
      System.exit(0);
    }

    if(code == KeyCode.D) {
      NodeDebugger.dump(rootStack);
    }
  }
}
