package de.calette.mephisto3.ui.system;

import callete.api.Callete;
import callete.api.services.system.SystemService;
import callete.api.util.SystemUtils;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.control.ServiceState;
import de.calette.mephisto3.ui.ControllablePanel;
import de.calette.mephisto3.util.ComponentUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Display different system information.
 */
public class SystemPanel extends ControllablePanel {
  private final static Logger LOG = LoggerFactory.getLogger(SystemPanel.class);

  private ProgressIndicator diskSpace;
  private Text freeMem;
  private Text usedMem;

  private ProgressIndicator heapSpace;
  private Text freeDisk;
  private Text usedDisk;

  private Timer refreshTimer;

  private ProgressIndicator cpuUsage;

  public SystemPanel() {
    setMinWidth(Mephisto3.WIDTH);
  }

  @Override
  public void pushed(ServiceState serviceState) {
    ServiceController.getInstance().fireControlEvent(KeyCode.UP);
  }

  @Override
  public void showPanel() {
    if(diskSpace == null) {
      buildUI();
    }

    super.showPanel();
    this.refreshTimer = new Timer();
    this.refreshTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            refresh();
          }
        });
      }
    }, 0, 1000);
  }

  @Override
  public void hidePanel() {
    super.hidePanel();
    this.refreshTimer.cancel();
    this.refreshTimer.purge();
  }

  //--------------- Helper ------------------------------------------------------

  private void refresh() {
    final SystemService systemService = Callete.getSystemService();
    usedDisk.setText(SystemUtils.humanReadableByteCount(systemService.getUsedDiskSpace()));
    freeDisk.setText(SystemUtils.humanReadableByteCount(systemService.getAvailableDiskSpace()));
    double usage = (systemService.getAvailableDiskSpace() * 100 / systemService.getUsedDiskSpace()) / new Double(100);
    diskSpace.setProgress(1 - usage);

    usedMem.setText(SystemUtils.humanReadableByteCount(systemService.getFreeMemory()));
    freeMem.setText(SystemUtils.humanReadableByteCount(systemService.getTotalMemory()));

    double memoryUsage = (systemService.getFreeMemory() * 100 / systemService.getTotalMemory()) / new Double(100);
    heapSpace.setProgress(memoryUsage);

    double cpuPercentage = systemService.getCpuUsage();
    if(!Double.isNaN(cpuPercentage)) {
      double v = cpuPercentage / 100;
      if(v == 1) {
        v = 9.99;
      }
      cpuUsage.setProgress(v);
    }
  }

  private Text createInfo(Pane parent, String key, String value) {
    HBox infoBox = new HBox(10);
    ComponentUtil.createText(key, "system-key", infoBox);
    Text valueText = ComponentUtil.createText(value, "default-16", infoBox);
    parent.getChildren().add(infoBox);
    return valueText;
  }

  private void buildUI() {
    LOG.info("Creating System Panel UI");
    final SystemService systemService = Callete.getSystemService();


    Label titleLabel = ComponentUtil.createCustomLabel("System Information", "system-tab-title", this);
    titleLabel.setPadding(new Insets(20, 30, 0, 20));

    BorderPane root = new BorderPane();
    root.setPadding(new Insets(90, 20, 10, 20));

    HBox center = new HBox(20);
    center.setAlignment(Pos.TOP_CENTER);
    root.setCenter(center);

    //disc usage
    VBox diskSpaceBox = new VBox(20);
    diskSpaceBox.setAlignment(Pos.TOP_CENTER);
    center.getChildren().add(diskSpaceBox);

    ComponentUtil.createText("Datenspeicher", "system-title", diskSpaceBox);
    diskSpace = new ProgressIndicator();
    diskSpace.getStyleClass().add("progressIndicator");
    diskSpaceBox.getChildren().add(diskSpace);

    VBox detailsDiskSpace = new VBox(5);
    detailsDiskSpace.setAlignment(Pos.TOP_CENTER);
    detailsDiskSpace.setPadding(new Insets(10, 5, 5, 5));
    freeDisk = createInfo(detailsDiskSpace, "Frei:", SystemUtils.humanReadableByteCount(systemService.getAvailableDiskSpace()));
    usedDisk = createInfo(detailsDiskSpace, "Benutzt:", SystemUtils.humanReadableByteCount(systemService.getUsedDiskSpace()));
    diskSpaceBox.getChildren().add(detailsDiskSpace);

    //memory
    VBox memoryBox = new VBox(20);
    memoryBox.setAlignment(Pos.TOP_CENTER);
    center.getChildren().add(memoryBox);

    ComponentUtil.createText("Heap-Speicher", "system-title", memoryBox);
    heapSpace = new ProgressIndicator();
    heapSpace.getStyleClass().add("progressIndicator");
    memoryBox.getChildren().add(heapSpace);
    VBox detailsMemory = new VBox(5);
    detailsMemory.setAlignment(Pos.TOP_CENTER);
    detailsMemory.setPadding(new Insets(10, 5, 5, 5));
    freeMem = createInfo(detailsMemory, "Maximaler Heap:", SystemUtils.humanReadableByteCount(systemService.getFreeMemory()));
    usedMem = createInfo(detailsMemory, "Aktueller Heap:", SystemUtils.humanReadableByteCount(systemService.getTotalMemory()));
    memoryBox.getChildren().add(detailsMemory);


    //CPU
    VBox cpuBox = new VBox(20);
    cpuBox.setAlignment(Pos.TOP_CENTER);
    center.getChildren().add(cpuBox);
    ComponentUtil.createText("CPU", "system-title", cpuBox);

    cpuUsage = new ProgressIndicator();
    cpuUsage.getStyleClass().add("progressIndicator");
    cpuBox.getChildren().add(cpuUsage);

    VBox detailsCPU= new VBox(5);
    detailsCPU.setAlignment(Pos.TOP_CENTER);
    createInfo(detailsCPU, "Rechername:", systemService.getHostname());
    createInfo(detailsCPU, "IP Adresse:", systemService.getHostAddress());
    createInfo(detailsCPU, "Processors:", Runtime.getRuntime().availableProcessors() + "");
    cpuBox.getChildren().add(detailsCPU);

    getChildren().add(root);
  }
}
