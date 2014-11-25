package de.calette.mephisto3.ui;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Super class for all regular controllable panels.
 */
public abstract class ControllableSelectorPanel<T> extends HBox implements ControlListener {
  private final static Logger LOG = LoggerFactory.getLogger(ControllableSelectorPanel.class);

  private Pane parent;
  private int scrollWidth;
  private TransitionQueue transitionQueue;
  private int index;
  private int itemCount;
  private Class controlItemBoxClass;
  private List<T> models;
  private int backTopPadding = -1;
  private T selection;

  public ControllableSelectorPanel(double margin, Pane parent, int scrollWidth, List<T> models, Class controlItemBoxClass) {
    super(margin);
    this.setOpacity(0);

    this.models = models;
    this.controlItemBoxClass = controlItemBoxClass;
    this.parent = parent;
    this.scrollWidth = scrollWidth;
    this.parent = parent;

    this.itemCount = models.size();
    transitionQueue = new TransitionQueue(this);
  }

  public void setSelection(T selection) {
    this.selection = selection;
  }

  public Pane getParentPane() {
    return parent;
  }

  protected void setBackButton(int backTopPadding) {
    BackButtonBox backButton = new BackButtonBox(scrollWidth, backTopPadding);
    getChildren().add(backButton);
    index = 1;
    itemCount++;
  }

  protected int getTopPadding() {
    return 20;
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
    //so lets create all children
    for (T model : models) {
      ControllableItemPanel item = createControllableItemPanelFor(controlItemBoxClass, model);
      this.getChildren().add(item);
    }

    //set the initial left padding to focus the first item
    double leftPadding = itemCount*scrollWidth-scrollWidth-scrollWidth-scrollWidth;
    if(backTopPadding != -1) {
      leftPadding = itemCount*scrollWidth-scrollWidth;
    }
    setPadding(new Insets(getTopPadding(), 0, 0, leftPadding));

    parent.getChildren().add(this);
    final ControllableItemPanel newSelection = (ControllableItemPanel) this.getChildren().get(index);
    newSelection.select();

    if(selection != null) {
      while(!selection.equals(getSelectedPanel().getUserData())) {
        scroll(-scrollWidth);
      }
    }

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
        T userData = (T) getSelectedPanel().getUserData();
        onHide(userData);
      }
    });
    outFader.play();
  }

  /**
   * Returns the selected ControllableItemPanel instance.
   */
  public ControllableItemPanel getSelectedPanel() {
    return (ControllableItemPanel) this.getChildren().get(index);
  }

  protected void scroll(int width) {
    if (index == itemCount - 1 && width < 0) {
      return;
    }
    if (index == 0 && width > 0) {
      return;
    }

    if(backTopPadding == -1) {
      if(index == 1 && width > 0) {
        updateSelection(width > 0);
        return;
      }
    }

    if(index != 0 || backTopPadding != -1) {
      final TranslateTransition translateTransition = new TranslateTransition(Duration.millis(50), this);
      translateTransition.setByX(width);
      transitionQueue.addTransition(translateTransition);

      Platform.runLater(new Runnable() {
                          @Override
                          public void run() {
                            transitionQueue.play();
                          }
                        });
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
   * @param controlItemBoxClass name of the class to create for the item
   * @param model the user data model used for the ControllableItemPanel
   */
  protected ControllableItemPanel createControllableItemPanelFor(Class controlItemBoxClass, T model) {
    try {
      final Class<?> modelClass = model.getClass();
      Constructor constructor = controlItemBoxClass.getConstructor(new Class[]{modelClass});
      return (ControllableItemPanel) constructor.newInstance(model);
    } catch (Exception e) {
      LOG.error("Error creating item panel: " + e.getMessage(), e);
    }
    return null;
  }

  /**
   * Invoked then the hide transition is finished.
   * The user data the current selection was build with is passed here.
   * @param userData the user data of the selected panel.
   */
  protected abstract void onHide(T userData);
}
