package de.calette.mephisto3.ui;

import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.control.ControlListener;
import de.calette.mephisto3.control.ServiceControlEvent;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.ui.google.AlbumBox;
import de.calette.mephisto3.util.TransitionQueue;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.List;

/**
 * Super class for all regular controllable panels.
 */
public abstract class ControllableSelectorPanel<T> extends HBox implements ControlListener {

  private Pane parent;
  private int scrollWidth;
  private TransitionQueue transitionQueue;
  private int index;
  private int itemCount;
  private boolean backSelection;

  public ControllableSelectorPanel(double margin, Pane parent, boolean backSelection, int scrollWidth, List<T> models) {
    super(margin);
    this.setOpacity(0);
    this.backSelection = backSelection;
    this.itemCount = models.size();
    if(backSelection) {
      index = 1;
      itemCount++;
    }
    this.parent = parent;
    this.scrollWidth = scrollWidth;
    this.parent = parent;
    transitionQueue = new TransitionQueue(this);

    //add back button if enabled
    if(backSelection) {
      AlbumBox backButton = new AlbumBox(null);
      getChildren().add(backButton);
    }

    //so lets create all children
    for (T model : models) {
      ControllableItemPanel item = createControllableItemPanelFor(model);
      this.getChildren().add(item);
    }

    //set the initial left padding to focus the first item
    double leftPadding = itemCount*scrollWidth-scrollWidth-scrollWidth-scrollWidth;
    setPadding(new Insets(20, 0, 0, leftPadding));
  }

  public Pane getParentPane() {
    return parent;
  }

  @Override
  public void controlEvent(ServiceControlEvent event) {
    if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PUSH)) {
      hidePanel();
    }
    else if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.NEXT)) {
      scroll(-scrollWidth);
    }
    else if (event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PREVIOUS)) {
      scroll(scrollWidth);
    }
  }

  /**
   * Shows the panel, plays the onShow transitions, adds the control event listener.
   */
  public void showPanel() {
    parent.getChildren().add(this);
    final ControllableItemPanel newSelection = (ControllableItemPanel) this.getChildren().get(index);
    newSelection.select();

    final FadeTransition inFader = TransitionUtil.createInFader(this);
    inFader.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        ServiceController.getInstance().addControlListener(ControllableSelectorPanel.this);
      }
    });
    inFader.play();
  }

  /**
   * Hides the panel, plays the onHide transitions, removes the control event listener and calls "onHide".
   */
  public void hidePanel() {
    ServiceController.getInstance().removeControlListener(this);
    final FadeTransition outFader = TransitionUtil.createOutFader(this);
    outFader.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        parent.getChildren().remove(ControllableSelectorPanel.this);
        onHide(getSelection().getUserData());
      }
    });
    outFader.play();
  }


  /**
   * Returns the selected ControllableItemPanel instance.
   */
  public ControllableItemPanel getSelection() {
    return (ControllableItemPanel) this.getChildren().get(index);
  }

  protected void scroll(int width) {
    if (index == itemCount - 1 && width < 0) {
      return;
    }
    if (index == 0 && width > 0) {
      return;
    }

    if(backSelection) {
      if(index == 1 && width > 0) {
        updateSelection(width > 0);
        return;
      }
    }

    if(index != 0) {
      final TranslateTransition translateTransition = new TranslateTransition(Duration.millis(50), this);
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

  /**
   * Updates the selection index and play the selection animation of the newly selected panel.
   * @param toLeft true if the scrolling goes to the left.
   */
  protected void updateSelection(boolean toLeft) {
    ControllableItemPanel oldSelection = (ControllableItemPanel) getChildren().get(index);
    oldSelection.deselect();
    if (toLeft) {
      index--;
    }
    else {
      index++;
    }
    ControllableItemPanel newSelection = (ControllableItemPanel) getChildren().get(index);
    newSelection.select();
  }

  /**
   * Factory method to be implemented by subclasses to determine the concrete panel.
   * @param model the user data model used for the ControllableItemPanel
   */
  protected abstract ControllableItemPanel createControllableItemPanelFor(T model);

  /**
   * Invoked then the hide transition is finished.
   * The user data the current selection was build with is passed here.
   * @param userData the user data of the selected panel.
   */
  protected abstract void onHide(Object userData);
}
