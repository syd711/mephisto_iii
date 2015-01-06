package de.calette.mephisto3;

import callete.api.Callete;
import callete.api.services.impl.music.google.AlbumCoverCache;
import callete.api.util.SystemUtils;
import de.calette.mephisto3.resources.ResourceLoader;
import de.calette.mephisto3.ui.Center;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.File;

/**
 * In the beginning, there was main...
 */
public class Mephisto3 extends Application {
  public static final int WIDTH = Callete.getConfiguration().getInt("width", 700);
  public static final int HEIGHT = Callete.getConfiguration().getInt("height", 395);
  public static StackPane rootStack;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(final Stage primaryStage) {
    Callete.getGPIOService().setSimulationMode(SystemUtils.isWindows());

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
    addDisposeListener(primaryStage);

    if(!SystemUtils.isWindows()) {
      primaryStage.initStyle(StageStyle.UNDECORATED);
    }
    
    int x = Callete.getConfiguration().getInt("position.x", 0);
    int y = Callete.getConfiguration().getInt("position.y", 0);
    if(x == 0 && y == 0) {
      primaryStage.centerOnScreen();
    }
    else {
      primaryStage.setX(x);
      primaryStage.setY(y);
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
}
