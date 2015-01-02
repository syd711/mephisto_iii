package de.calette.mephisto3.ui;

import de.calette.mephisto3.control.ControlListener;
import de.calette.mephisto3.control.ServiceControlEvent;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.util.TransitionQueue;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
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
  private Class controlItemBoxClass;

  private List<T> models;

  private int backTopPadding = -1;
  private T selection;
  private double margin;

  public ControllableSelectorPanel(double margin, Pane parent, int scrollWidth, List<T> models, Class controlItemBoxClass) {
    super(margin);
    this.margin = margin;
    this.setOpacity(0);

    this.models = models;
    this.controlItemBoxClass = controlItemBoxClass;
    this.parent = parent;
    this.scrollWidth = scrollWidth;
    this.parent = parent;

    transitionQueue = new TransitionQueue(this);
  }

  public List<T> getModels() {
    return models;
  }

  public void setSelection(T selection) {
    this.selection = selection;
  }

  public void setSelectionIndex(int index) {
    this.index = index;
  }

  public int getSelectionIndex() {
    return index;
  }

  public Pane getParentPane() {
    return parent;
  }

  protected void setBackButton(int backTopPadding) {
    BackButtonBox backButton = new BackButtonBox(scrollWidth, backTopPadding);
    getChildren().add(backButton);
    index = 1;
  }

  protected int getTopPadding() {
    return 0;
  }

  @Override
  public void controlEvent(ServiceControlEvent event) {
    if(event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PUSH)) {
      Platform.runLater(new Runnable() {
        @Override
        public void run() {
          hidePanel();
        }
      });
    }
    else if(event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.NEXT)) {
      scroll(false, -getScrollWidth());
    }
    else if(event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.PREVIOUS)) {
      scroll(true, getScrollWidth());
    }
    else if(event.getEventType().equals(ServiceControlEvent.EVENT_TYPE.LONG_PUSH)) {
      Platform.runLater(new Runnable() {
        @Override
        public void run() {
          onLongPush();
        }
      });
    }
  }

  /**
   * Shows the panel, plays the onShow transitions, adds the control event listener.
   */
  public void showPanel() {
    parent.getChildren().add(this);

    //so lets create all children
    LOG.debug("ControllableSelectorPanel creates " + models.size() + " child components");
    List<Node> items = new ArrayList<>();
    for(T model : models) {
      ControllableItemPanel item = createControllableItemPanelFor(controlItemBoxClass, model);
      items.add((Node) item);
    }

    getChildren().addAll(items);

    //set the initial left padding to focus the first item
    int itemCount = models.size() + 1; //+1 for the back button
    double leftPadding = itemCount * scrollWidth - scrollWidth - scrollWidth - scrollWidth - margin;
    if(backTopPadding != -1) {
      leftPadding = itemCount * scrollWidth - scrollWidth;
    }
    setPadding(new Insets(getTopPadding(), 0, 0, leftPadding));


    //update panel view to the selected model
    applyLastSelection();

    final ControllableItemPanel newSelection = (ControllableItemPanel) getChildren().get(index);
    newSelection.select();

    final FadeTransition inFader = TransitionUtil.createInFader(ControllableSelectorPanel.this);
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

  protected int getScrollWidth() {
    return scrollWidth;
  }

  /**
   * Returns the selected ControllableItemPanel instance.
   */
  public ControllableItemPanel getSelectedPanel() {
    return (ControllableItemPanel) this.getChildren().get(index);
  }

  protected void scroll(boolean toLeft, int width) {
    if(index == getItemCount() && !toLeft) {
      return;
    }
    if(index == 1 && getItemCount() == 1 && !toLeft) {
      return;
    }
    if(index == 0 && toLeft) {
      return;
    }

    //ignore scrolling when back button is available and selected
    if(backTopPadding == -1 && index == 1 && toLeft) {
      updateSelection(toLeft);
      return;
    }

    if(index != 0 || backTopPadding != -1) {
      Transition translateTransition = TransitionUtil.createTranslateByXTransition(this, 50, width);
      transitionQueue.addTransition(translateTransition);

      Platform.runLater(new Runnable() {
        @Override
        public void run() {
          transitionQueue.play();
        }
      });
    }

    updateSelection(toLeft);
  }

  /**
   * Returns the amount of items this panel should scroll.
   */
  protected int getItemCount() {
    return models.size();
  }

  /**
   * Updates the selection index and play the selection animation of the newly selected panel.
   *
   * @param toLeft true if the scrolling goes to the left.
   */
  protected void updateSelection(boolean toLeft) {
    deselect(toLeft, index);
    if(toLeft) {
      index--;
    }
    else {
      index++;
    }
    select(toLeft, index);
  }

  /**
   * Deselects the current selection
   *
   * @param oldIndex the old index before the new index is updated.
   */
  protected void deselect(boolean toLeft, int oldIndex) {
    ControllableItemPanel oldSelection = (ControllableItemPanel) getChildren().get(oldIndex);
    oldSelection.deselect();
  }

  /**
   * Selects the current selection
   *
   * @param toLeft
   * @param newIndex the new index after deselection has been executed
   */
  protected void select(boolean toLeft, int newIndex) {
    ControllableItemPanel newSelection = (ControllableItemPanel) getChildren().get(newIndex);
    newSelection.select();
  }

  /**
   * Factory method to be implemented by subclasses to determine the concrete panel.
   *
   * @param controlItemBoxClass name of the class to create for the item
   * @param model               the user data model used for the ControllableItemPanel
   */
  protected ControllableItemPanel createControllableItemPanelFor(Class controlItemBoxClass, T model) {
    try {
      final Class<?> modelClass = model.getClass();
      Constructor constructor = controlItemBoxClass.getConstructor(new Class[]{ControllableSelectorPanel.class, modelClass});
      return (ControllableItemPanel) constructor.newInstance(this, model);
    } catch (Exception e) {
      LOG.error("Error creating item panel for " + controlItemBoxClass + ": " + e.getMessage(), e);
    }
    return null;
  }

  /**
   * Invoked then the hide transition is finished.
   * The user data the current selection was build with is passed here.
   *
   * @param userData the user data of the selected panel.
   */
  protected abstract void onHide(T userData);

  /**
   * Optional action to be executed when a long push is executed.
   */
  protected void onLongPush() {
  }

  /**
   * Checks if a selection model was passed.
   * If true, the selector scrolls silently to the position and updates the selection index.
   */
  private void applyLastSelection() {
    if(selection != null) {
      while(!selection.equals(getSelectedPanel().getUserData())) {
        index++;
      }
      final TranslateTransition translateTransition = TransitionUtil.createTranslateByXTransition(this, 1, (index - 1) * (-scrollWidth));
      translateTransition.play();
    }
  }
}
