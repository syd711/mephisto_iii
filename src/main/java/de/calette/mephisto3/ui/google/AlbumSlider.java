package de.calette.mephisto3.ui.google;

import callete.api.Callete;
import callete.api.services.music.model.Album;
import de.calette.mephisto3.control.ControlListener;
import de.calette.mephisto3.control.ServiceControlEvent;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.util.TransitionQueue;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 */
public class AlbumSlider implements ControlListener {
  public static final int SCROLL_WIDTH = AlbumBox.COVER_WIDTH+20;

  private final static Logger LOG = LoggerFactory.getLogger(AlbumSlider.class);

  private Pane parent;
  private Pane scroller;
  private int index = 1;

  private TranslateTransition scrollTransition;
  private TransitionQueue transitionQueue;
  private int itemCount = 0;

  public AlbumSlider(Pane parent, List<Album> albums) {
    this.itemCount = albums.size()+1; //back
    this.parent = parent;
    scroller = new HBox(20);
    scroller.setOpacity(0);

    parent.getChildren().add(scroller);
    transitionQueue = new TransitionQueue(scroller);

    //spacer
    AlbumBox backButton = new AlbumBox(null);
    scroller.getChildren().add(backButton);

    for (Album album : albums) {
      if (!StringUtils.isEmpty(album.getArtUrl())) {
        AlbumBox albumPanel = new AlbumBox(album);
        scroller.getChildren().add(albumPanel);
      }
      else {
        //        String url = ResourceLoader.getResource("cover.png");
        //        Canvas cover = TransitionUtil.createImageCanvas(url, COVER_SIZE, COVER_SIZE);
        //        vbox.getChildren().add(cover);
      }
    }

    scroller.setPadding(new Insets(20, 0, 0, ((itemCount-2) * AlbumBox.COVER_WIDTH)-AlbumBox.COVER_WIDTH/2));

    LOG.info("Finished creation of AlbumSlider panel.");

    scrollTransition = new TranslateTransition(Duration.millis(50), scroller);
    scrollTransition.setAutoReverse(false);
  }

  @Override
  public void controlEvent(ServiceControlEvent event) {
    if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PUSH)) {
      //AlbumBox newSelection = (AlbumBox) scroller.getChildren().get(index);
      hideSlider();
    }
    else if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.NEXT)) {
      scroll(-SCROLL_WIDTH);
    }
    else if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PREVIOUS)) {
      scroll(SCROLL_WIDTH);
    }
  }

  public void showSlider() {
    AlbumBox newSelection = (AlbumBox) scroller.getChildren().get(1);
    newSelection.select();

    final FadeTransition inFader = TransitionUtil.createInFader(scroller);
    inFader.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        ServiceController.getInstance().addControlListener(AlbumSlider.this);
      }
    });
    inFader.play();
  }

  public void hideSlider() {
    ServiceController.getInstance().removeControlListener(this);
    final FadeTransition outFader = TransitionUtil.createOutFader(scroller);
    outFader.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        parent.getChildren().remove(scroller);
        AlbumLetterSelector selector = new AlbumLetterSelector(parent, Callete.getGoogleMusicService().getAlbumsByArtistLetter());
        selector.showLetterSelector();
      }
    });
    outFader.play();
  }

  //----------- Helper ---------------------

  private void scroll(int width) {
    int scrollWidth = width;
    if (index == itemCount - 1 && width < 0) {
      return;
    }
    if (index == 0 && width > 0) {
      return;
    }

    if(index == 1 && width > 0) {
      updateSelection(width > 0);
      return;
    }


    if(index != 0) {
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
    }

    updateSelection(width > 0);
  }

  private void updateSelection(boolean toLeft) {
    AlbumBox oldSelection = (AlbumBox) scroller.getChildren().get(index);
    oldSelection.deselect();
    if (toLeft) {
      index--;
    }
    else {
      index++;
    }
    AlbumBox newSelection = (AlbumBox) scroller.getChildren().get(index);
    newSelection.select();
  }
}
