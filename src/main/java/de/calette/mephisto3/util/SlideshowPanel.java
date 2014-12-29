package de.calette.mephisto3.util;

import callete.api.services.resources.ImageResource;
import callete.api.services.resources.SlideShow;
import javafx.application.Platform;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.Timer;
import java.util.TimerTask;

/**
 */
public class SlideshowPanel extends StackPane {
  public static final int PERIOD = 5000;
  public static final int TRANSITION_MILLIS = 800;

  private ImageView imageViewOld = new ImageView();
  private ImageView imageViewNew = new ImageView();
  private SlideShow slideShow;
  private Timer timer;

  public SlideshowPanel() {
    ColorAdjust brightness = new ColorAdjust();
    brightness.setBrightness(-0.3);
    imageViewNew.setEffect(brightness);
    imageViewOld.setEffect(brightness);
    getChildren().add(imageViewOld);
    getChildren().add(imageViewNew);
  }

  public void setSlideShow(SlideShow slideShow) {
    if(timer != null) {
      this.stopSlideShow();
    }
    this.slideShow = slideShow;
  }

  public void startSlideShow() {
    //apply datetime timer
    boolean startTimer = slideShow.size() > 1;

    if(startTimer) {
      timer = new Timer();
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          nextImage();
        }
      }, 0, PERIOD);
    }
    else {
      nextImage();
    }
  }

  public void stopSlideShow() {
    this.timer.cancel();
    this.timer.purge();
  }

  // ----------------- Helper ---------------------------------

  /**
   * Applies the next image from the slide show.
   */
  private void nextImage() {
    ImageResource imageResource = slideShow.nextImage();
    final Image image = ComponentUtil.toFXImage(imageResource);

    Platform.runLater(new Runnable() {
      @Override
      public void run() {
        ImageView showView = imageViewOld;
        ImageView hideView = imageViewNew;
        if(hideView.getOpacity() == 0) {
          showView = imageViewNew;
          hideView = imageViewOld;
        }

        showView.setImage(image);
        TransitionUtil.createInFader(showView, TRANSITION_MILLIS).play();

        if(hideView.getOpacity() == 1) {
          TransitionUtil.createOutFader(hideView, TRANSITION_MILLIS).play();
        }
      }
    });
  }
}
