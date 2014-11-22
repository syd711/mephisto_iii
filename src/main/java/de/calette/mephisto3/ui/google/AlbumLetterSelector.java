package de.calette.mephisto3.ui.google;

import callete.api.services.music.model.Album;
import de.calette.mephisto3.control.ControlListener;
import de.calette.mephisto3.control.ServiceControlEvent;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.resources.menu.MenuResourceLoader;
import de.calette.mephisto3.util.ComponentUtil;
import de.calette.mephisto3.util.TransitionQueue;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * UI Panel for selecting the album album sorted by name or artist.
 */
public class AlbumLetterSelector implements ControlListener {


  private int index = 0;
  private Map<String, List<Album>> albums;
  private TranslateTransition scrollTransition;
  private TransitionQueue transitionQueue;
  private HBox scroller;
  private Pane albumSelectorCenterStack;
  private Pane parentCenter;

  public AlbumLetterSelector(Pane center, Map<String, List<Album>> albums) {
    this.parentCenter = center;
    this.albums = albums;

    albumSelectorCenterStack = new StackPane();
    center.getChildren().add(albumSelectorCenterStack);

    addSelectorIcons();

    scroller = new HBox(0);
    transitionQueue = new TransitionQueue(scroller);

    final Iterator<Map.Entry<String, List<Album>>> iterator = albums.entrySet().iterator();
    while(iterator.hasNext()) {
      final Map.Entry<String, List<Album>> next = iterator.next();
      AlbumLetterBox letterBox = new AlbumLetterBox(next.getKey(), next.getValue());
      scroller.getChildren().add(letterBox);
    }

    final int offset = (albums.keySet().size() * AlbumLetterBox.LETTER_BOX_WIDTH) - AlbumLetterBox.LETTER_BOX_WIDTH;
    scroller.setPadding(new Insets(115, 0, 0, offset));
    albumSelectorCenterStack.getChildren().add(scroller);

    scrollTransition = new TranslateTransition(Duration.millis(50), scroller);
    scrollTransition.setAutoReverse(false);


  }

  @Override
  public void controlEvent(ServiceControlEvent event) {
    if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.LONG_PUSH)) {

    }
    else if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PUSH)) {
      hideLetterSelector();
    }
    else if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.NEXT)) {
      scroll(-AlbumLetterBox.LETTER_BOX_WIDTH);
    }
    else if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PREVIOUS)) {
      scroll(AlbumLetterBox.LETTER_BOX_WIDTH);
    }
  }

  private void hideLetterSelector() {
    ServiceController.getInstance().removeControlListener(this);

    final AlbumLetterBox selection = (AlbumLetterBox) scroller.getChildren().get(index);
    final FadeTransition outFader = TransitionUtil.createOutFader(albumSelectorCenterStack);
    outFader.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        parentCenter.getChildren().remove(albumSelectorCenterStack);
        final AlbumSlider albumSlider = new AlbumSlider(parentCenter, selection.getAlbums());
        albumSlider.showPanel();
      }
    });
    outFader.play();
  }

  public void showLetterSelector() {
    ServiceController.getInstance().addControlListener(this);
  }

  //----------- Helper -------------------------



  /**
   * Creates the two arrow icons with up and down for marking the selection.
   */
  private void addSelectorIcons() {
    Canvas selectorCanvas = ComponentUtil.createImageCanvas(MenuResourceLoader.getResource("selector.png"), 40, 40);
    albumSelectorCenterStack.getChildren().add(selectorCanvas);

    Canvas selectorDownCanvas = ComponentUtil.createImageCanvas(MenuResourceLoader.getResource("selector_down.png"), 40, 40);
    selectorDownCanvas.setLayoutY(20);
    albumSelectorCenterStack.getChildren().add(selectorDownCanvas);
  }

  private void scroll(final int width) {
    if (index == albums.size() - 1 && width < 0) {
      return;
    }
    if (index == 0 && width > 0) {
      return;
    }
    final TranslateTransition translateTransition = new TranslateTransition(Duration.millis(50), scroller);
    translateTransition.setByX(width);
    transitionQueue.addTransition(translateTransition);

    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                          transitionQueue.play();
        }
      }
    );


    AlbumLetterBox oldSelection = (AlbumLetterBox) scroller.getChildren().get(index);
    oldSelection.deselect();
    if (width > 0) {
      index--;
    }
    else {
      index++;
    }
    AlbumLetterBox newSelection = (AlbumLetterBox) scroller.getChildren().get(index);
    newSelection.select();

  }
}
