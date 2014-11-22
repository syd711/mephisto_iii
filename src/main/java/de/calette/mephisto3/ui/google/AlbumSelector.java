package de.calette.mephisto3.ui.google;

import callete.api.services.music.model.Album;
import de.calette.mephisto3.control.ControlListener;
import de.calette.mephisto3.control.ServiceControlEvent;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.resources.menu.MenuResourceLoader;
import de.calette.mephisto3.util.ComponentUtil;
import de.calette.mephisto3.util.TransitionQueue;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
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
public class AlbumSelector implements ControlListener {


  private int index = 0;
  private Map<String, List<Album>> albums;
  private TranslateTransition scrollTransition;
  private TransitionQueue transitionQueue;
  private HBox scroller;
  private Pane albumSelectorCenterStack;
  private Pane parentCenter;

  public AlbumSelector(Pane center, Map<String, List<Album>> albums) {
    this.parentCenter = center;
    this.albums = albums;

    albumSelectorCenterStack = new StackPane();
    Canvas selectorCanvas = ComponentUtil.createImageCanvas(MenuResourceLoader.getResource("selector.png"), 40, 40);
    albumSelectorCenterStack.getChildren().add(selectorCanvas);
    center.getChildren().add(albumSelectorCenterStack);

    scroller = new HBox(0);
    transitionQueue = new TransitionQueue(scroller);

    final Iterator<Map.Entry<String, List<Album>>> iterator = albums.entrySet().iterator();
    while(iterator.hasNext()) {
      final Map.Entry<String, List<Album>> next = iterator.next();
      AlbumLetterBox letterBox = new AlbumLetterBox(next.getKey(), next.getValue());
      scroller.getChildren().add(letterBox);
    }

    scroller.setPadding(new Insets(120, 0, 0, (albums.keySet().size() * AlbumLetterBox.LETTER_BOX_WIDTH)-AlbumLetterBox.LETTER_BOX_WIDTH/2));
    albumSelectorCenterStack.getChildren().add(scroller);

    scrollTransition = new TranslateTransition(Duration.millis(50), scroller);
    scrollTransition.setAutoReverse(false);

    ServiceController.getInstance().addControlListener(this);
  }

  @Override
  public void controlEvent(ServiceControlEvent event) {
    if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.LONG_PUSH)) {

    }
    else if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PUSH)) {
      AlbumLetterBox selection = (AlbumLetterBox) scroller.getChildren().get(index);
      parentCenter.getChildren().remove(albumSelectorCenterStack);
      ServiceController.getInstance().removeControlListener(this);

      new AlbumSlider(parentCenter, selection.getAlbums());
    }
    else if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.NEXT)) {
      scroll(-AlbumLetterBox.LETTER_BOX_WIDTH);
    }
    else if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PREVIOUS)) {
      scroll(AlbumLetterBox.LETTER_BOX_WIDTH);
    }
  }

  private void scroll(final int width) {
    if (index == albums.size() - 1 && width < 0) {
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
