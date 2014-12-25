package de.calette.mephisto3.ui;

import callete.api.Callete;
import callete.api.services.Service;
import callete.api.services.music.model.AlbumCollection;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.control.ControlListener;
import de.calette.mephisto3.control.ServiceControlEvent;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.control.ServiceState;
import de.calette.mephisto3.ui.google.AlbumLetterSelector;
import de.calette.mephisto3.ui.google.GooglePlayerStatusBox;
import de.calette.mephisto3.util.TransitionQueue;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The chooser that selects the active function of the radio.
 */
public class ServiceChooser implements ControlListener {
  public static final int SCROLL_DURATION = 200;
  public static final int DISPLAY_DELAY = 500;

  private int index = 0;
  private List<ServiceNameBox> serviceBoxes = new ArrayList<>();
  private Map<Service, Pane> serviceBoxesByService = new HashMap<>();

  private Transition showFader;
  private Transition hideFader;
  private TranslateTransition scrollTransition;

  private TransitionQueue transitionQueue;
  private HBox overlay;
  private Center center;
  private final HBox scroller = new HBox();
  private Pane musicSelector;

  private Text byArtist;
  private Text byName;
  private Text playbackSelection;
  private StackPane albumSelectorCenterStack;

  private GooglePlayerStatusBox googlePlayer = new GooglePlayerStatusBox();

  public ServiceChooser(final Center center) {
    this.center = center;

    overlay = new HBox();
    overlay.setOpacity(0);
    this.albumSelectorCenterStack = new StackPane();
    overlay.getChildren().add(albumSelectorCenterStack);
    center.stackPane.getChildren().add(overlay);
    center.stackPane.getChildren().add(googlePlayer);

    overlay.setAlignment(Pos.TOP_CENTER);
    overlay.setId("chooser");
    overlay.setMinWidth(Mephisto3.WIDTH);
    overlay.setMinHeight(80);

    scroller.setPadding(new Insets(50, 0, 80, 460));
    scroller.setAlignment(Pos.CENTER);

    transitionQueue = new TransitionQueue(scroller);

    scrollTransition = TransitionUtil.createTranslateByXTransition(scroller, SCROLL_DURATION, 0);

    showFader = TransitionUtil.createInFader(overlay, DISPLAY_DELAY);
    hideFader = TransitionUtil.createOutFader(overlay, DISPLAY_DELAY);
  }

  /**
   * Adds a new entry to the chooser
   */
  public void addService(final String label, final Service service) {
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        final ServiceNameBox serviceBox = new ServiceNameBox(label, service);
        scroller.getChildren().add(serviceBox);
        serviceBoxes.add(serviceBox);
        serviceBoxesByService.put(service, serviceBox);
        TransitionUtil.createInFader(serviceBox).play();
      }
    });
  }

  @Override
  public void controlEvent(ServiceControlEvent event) {
    Service service = (Service) serviceBoxes.get(index).getUserData();
    final Pane searchSelectionBox = serviceBoxesByService.get(service);

    if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.LONG_PUSH)) {
      //not assigned
    }
    else if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PUSH)) {
      if(musicSelector != null) {
        final FadeTransition blink = TransitionUtil.createBlink(playbackSelection);
        blink.setOnFinished(new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent actionEvent) {
            overlay.getChildren().remove(scroller);
            ServiceController.getInstance().removeControlListener(ServiceChooser.this);
            AlbumLetterSelector selector = new AlbumLetterSelector(ServiceChooser.this, albumSelectorCenterStack, (List<AlbumCollection>) playbackSelection.getUserData());
            selector.showPanel();

            googlePlayer.showPlayer();

          }
        });
        blink.play();
      }
      else {
        if (service.equals(Callete.getGoogleMusicService())) {
          showMusicOptions(searchSelectionBox);
        }
        else {
          hideServiceChooser();
        }
      }
    }
    else {
      if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.NEXT)) {
        if (musicSelector != null) {
          togglePlaybackSelection();
        }
        else {
          scroll(-ServiceNameBox.SERVICE_BOX_WIDTH);
        }
      }
      else if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PREVIOUS)) {
        if (playbackSelection == null || playbackSelection == byArtist) {
          scroll(ServiceNameBox.SERVICE_BOX_WIDTH);
        }
        else {
          togglePlaybackSelection();
        }
      }

      service = (Service) serviceBoxes.get(index).getUserData();
      if (!service.equals(Callete.getGoogleMusicService())) {
        hideMusicOptions(searchSelectionBox);
      }
    }
  }

  // --------------- Helper -----------------------------
  private void togglePlaybackSelection() {
    Text oldSelection;
    if(playbackSelection == null || playbackSelection == byName) {
      playbackSelection = byArtist;
      oldSelection = byName;
    }
    else {
      playbackSelection = byName;
      oldSelection = byArtist;
    }
    TransitionUtil.createScaler(playbackSelection, 1.2).play();
    TransitionUtil.createScaler(oldSelection, 1.0).play();
  }

  /**
   * Display method of the whole chooser, updates the control.
   */
  public void showServiceChooser() {
    overlay.getChildren().add(scroller);
    showFader.play();
    center.activeControlPanel.hidePanel();
    ServiceController.getInstance().addControlListener(this);
    ServiceController.getInstance().removeControlListener(center);
    final ServiceNameBox serviceNameBox = serviceBoxes.get(index);
    serviceNameBox.select();
  }

  /**
   * Hides the Service Chooser, updates the control.
   */
  private void hideServiceChooser() {
    ServiceController.getInstance().removeControlListener(this);
    ServiceController.getInstance().addControlListener(center);
    ServiceController.getInstance().setControlEnabled(false);

    final FadeTransition blink = TransitionUtil.createBlink(serviceBoxes.get(index));
    blink.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        playCloseAnimationAndHide();
      }
    });

    final Service service = (Service) serviceBoxes.get(index).getUserData();
    final ServiceState serviceState = ServiceController.getInstance().getServiceState();
    if (!serviceState.getService().equals(service)) {
      blink.play();
    }
    else {
      playCloseAnimationAndHide();
    }
  }

  /**
   * Closes the chooser, resets the UI state.
   */
  private void playCloseAnimationAndHide() {
    final Service service = (Service) serviceBoxes.get(index).getUserData();
    hideFader.play();
    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        overlay.getChildren().remove(scroller);
        ServiceController.getInstance().switchService(service);
      }
    });
  }

  private void showMusicOptions(Pane searchSelectionBox) {
    if (musicSelector == null) {
      musicSelector = new VBox(20);
      musicSelector.setOpacity(0);

      byArtist = new Text("Albums by Artist");
      byArtist.setUserData(Callete.getGoogleMusicService().getAlbumsByArtistLetter());
      byArtist.getStyleClass().add("selector");
      byName = new Text("Albums by Name");
      byName.setUserData(Callete.getGoogleMusicService().getAlbumByNameLetter());
      byName.getStyleClass().add("selector");

      musicSelector.getChildren().add(byArtist);
      musicSelector.getChildren().add(byName);
    }
    searchSelectionBox.getChildren().add(musicSelector);

    TransitionUtil.createInFader(musicSelector).play();
  }

  private void hideMusicOptions(final Pane searchSelectionBox) {
    if (musicSelector == null) {
      return;
    }
    final FadeTransition inFader = TransitionUtil.createOutFader(musicSelector);
    inFader.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        final boolean remove = searchSelectionBox.getChildren().remove(musicSelector);
        musicSelector = null;
        playbackSelection = null;
      }
    });
    inFader.play();
  }


  private void scroll(final int width) {
    if (index == serviceBoxes.size() - 1 && width < 0) {
      return;
    }
    if (index == 0 && width > 0) {
      return;
    }

    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                          scrollTransition.setByX(width);
                          transitionQueue.addTransition(scrollTransition);
                          transitionQueue.play();
                        }
                      }
    );


    ServiceNameBox oldSelection = serviceBoxes.get(index);
    oldSelection.deselect();
    if (width > 0) {
      index--;
    }
    else {
      index++;
    }
    ServiceNameBox newSelection = serviceBoxes.get(index);
    newSelection.select();
  }
}
