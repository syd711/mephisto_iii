package de.calette.mephisto3.ui.system;

import callete.api.Callete;
import callete.api.services.system.SystemService;
import callete.api.util.SystemUtils;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.ui.ControllablePanel;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Display different system information.
 */
public class SystemPanel extends ControllablePanel {

  private Transition transition;

  ProgressIndicator diskSpace;
  private Text freeMem;
  private Text usedMem;

  ProgressIndicator heapSpace;
  private Text freeDisk;
  private Text usedDisk;


  public SystemPanel() {
    super(Callete.getWeatherService().getWeather());
    setMinWidth(Mephisto3.WIDTH);

    final SystemService systemService = Callete.getSystemService();

    BorderPane root = new BorderPane();
    root.setPadding(new Insets(10, 20, 10, 20));

    HBox center = new HBox(30);
    center.setAlignment(Pos.TOP_CENTER);
    root.setCenter(center);

    //disc usage
    VBox diskSpaceBox = new VBox(20);
    diskSpaceBox.setAlignment(Pos.TOP_CENTER);
    center.getChildren().add(diskSpaceBox);

    Text keyText = new Text("Datenspeicher");
    keyText.getStyleClass().add("system-title");
    diskSpaceBox.getChildren().add(keyText);
    diskSpace = new ProgressIndicator();
    diskSpace.getStyleClass().add("progressIndicator");
    diskSpaceBox.getChildren().add(diskSpace);

    VBox detailsDiskSpace = new VBox(5);
    detailsDiskSpace.setPadding(new Insets(10, 5, 5, 5));
    freeDisk = createInfo(detailsDiskSpace, "Frei:", SystemUtils.humanReadableByteCount(systemService.getAvailableDiskSpace()));
    usedDisk = createInfo(detailsDiskSpace, "Benutzt:", SystemUtils.humanReadableByteCount(systemService.getUsedDiskSpace()));
    diskSpaceBox.getChildren().add(detailsDiskSpace);

    //memory
    VBox memoryBox = new VBox(20);
    memoryBox.setAlignment(Pos.TOP_CENTER);
    center.getChildren().add(memoryBox);

    Text heapTitleText = new Text("Heap-Speicher");
    heapTitleText.getStyleClass().add("system-title");
    memoryBox.getChildren().add(heapTitleText);
    heapSpace = new ProgressIndicator();
    heapSpace.getStyleClass().add("progressIndicator");
    memoryBox.getChildren().add(heapSpace);
    VBox detailsMemory = new VBox(5);
    detailsMemory.setPadding(new Insets(10, 5, 5, 5));
    freeMem = createInfo(detailsMemory, "Maximaler Heap:", SystemUtils.humanReadableByteCount(systemService.getFreeMemory()));
    usedMem = createInfo(detailsMemory, "Aktueller Heap:", SystemUtils.humanReadableByteCount(systemService.getTotalMemory()));
    memoryBox.getChildren().add(detailsMemory);


    //system
    VBox systemBox = new VBox(20);
    systemBox.setAlignment(Pos.TOP_CENTER);
    center.getChildren().add(systemBox);

    Text systemTitle = new Text("System");
    systemTitle.getStyleClass().add("system-title");
    systemBox.getChildren().add(systemTitle);

    VBox systemDetailsBox = new VBox(5);
    systemDetailsBox.setPadding(new Insets(10, 5, 5, 5));
    createInfo(systemDetailsBox, "Rechername:", systemService.getHostname());
    createInfo(systemDetailsBox, "IP Adresse:", systemService.getHostAddress());
    createInfo(systemDetailsBox, "User:", System.getProperty("user.name"));
    createInfo(systemDetailsBox, "OS Name:", System.getProperty("os.name"));
    createInfo(systemDetailsBox, "Processors:", Runtime.getRuntime().availableProcessors()+"");
    createInfo(systemDetailsBox, "JDK:", System.getProperty("java.version"));
    systemBox.getChildren().add(systemDetailsBox);

    getChildren().add(root);

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
    final SystemService systemService = Callete.getSystemService();
    usedDisk.setText(SystemUtils.humanReadableByteCount(systemService.getUsedDiskSpace()));
    freeDisk.setText(SystemUtils.humanReadableByteCount(systemService.getAvailableDiskSpace()));
    double usage = (systemService.getAvailableDiskSpace() * 100 / systemService.getUsedDiskSpace()) / new Double(100);
    diskSpace.setProgress(1 - usage);

    usedMem.setText(SystemUtils.humanReadableByteCount(systemService.getFreeMemory()));
    freeMem.setText(SystemUtils.humanReadableByteCount(systemService.getTotalMemory()));

    double memoryUsage = (systemService.getFreeMemory() * 100 / systemService.getTotalMemory()) / new Double(100);
    heapSpace.setProgress(memoryUsage);

    transition.play();
  }

  //--------------- Helper ------------------------------------------------------

  private Text createInfo(Pane parent, String key, String value) {
    HBox infoBox = new HBox(10);
    Text keyText = new Text(key);
    keyText.getStyleClass().add("system-key");
    infoBox.getChildren().add(keyText);

    Text valueText = new Text(value);
    valueText.getStyleClass().add("system-value");
    infoBox.getChildren().add(valueText);

    parent.getChildren().add(infoBox);
    return valueText;
  }
}