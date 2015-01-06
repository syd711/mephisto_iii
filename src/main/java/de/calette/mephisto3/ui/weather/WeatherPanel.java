package de.calette.mephisto3.ui.weather;

import callete.api.Callete;
import callete.api.services.impl.resources.SlideShowImpl;
import callete.api.services.resources.SlideShow;
import callete.api.services.weather.Weather;
import de.calette.mephisto3.control.ServiceController;
import de.calette.mephisto3.control.ServiceState;
import de.calette.mephisto3.ui.ControllablePanel;
import de.calette.mephisto3.ui.ServiceScroller;
import de.calette.mephisto3.util.ComponentUtil;
import de.calette.mephisto3.util.Executor;
import de.calette.mephisto3.util.SlideshowPanel;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 */
public class WeatherPanel extends ControllablePanel {
  private final static Logger LOG = LoggerFactory.getLogger(WeatherPanel.class);

  private SlideshowPanel slideshowPanel = new SlideshowPanel();
  private ServiceScroller scroller = new ServiceScroller();
  private List<WeatherForecastPanel> forecastPanels = new ArrayList<>();

  private Weather activeWeather;
  private static Map<String, SlideShow> cachedSlideShows = new HashMap<>();

  private ImageView weatherIconView;
  private Label cityLabel;
  private Text degreeLabel;
  private Text tempLabel;
  private HBox busyIndicator;

  public WeatherPanel() {
    for(Weather w : Callete.getWeatherService().getWeather()) {
      getSlideShow(w.getCity());
    }
  }

  @Override
  public void pushed(ServiceState serviceState) {
    ServiceController.getInstance().fireControlEvent(KeyCode.UP);
  }

  @Override
  public void showPanel() {
    if(busyIndicator == null) {
      buildUI();
    }
    
    busyIndicator.setOpacity(1);
    super.showPanel();
    scroller.showScroller();
    activeWeather = (Weather) ServiceController.getInstance().getServiceState().getSelection();

    cityLabel.getStyleClass().clear();
    cityLabel.getStyleClass().addAll("label", "weather-city");
    cityLabel.setText(activeWeather.getCity());

    Executor.run(new Runnable() {
      @Override
      public void run() {
        String city = activeWeather.getCity();
        SlideShow slideShow = getSlideShow(city);
        slideshowPanel.setSlideShow(slideShow);
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            cityLabel.getStyleClass().clear();
            cityLabel.getStyleClass().addAll("label", "weather-city-active");
            slideshowPanel.startSlideShow();
            busyIndicator.setOpacity(0);
          }
        });
      }
    });
  }

  @Override
  public void hidePanel() {
    super.hidePanel();
    scroller.hideScroller();
    slideshowPanel.stopSlideShow();
  }

  @Override
  protected void serviceStateChanged(ServiceState serviceState) {
    this.activeWeather = (Weather) serviceState.getSelection();
    final Image image = new Image(WeatherConditionMapper.getWeatherForecastIcon(activeWeather), 55, 55, false, true);

    String city = activeWeather.getCity();
    SlideShow slideShow = getSlideShow(city);
    slideshowPanel.setSlideShow(slideShow);
    slideshowPanel.startSlideShow();

    ParallelTransition transition = new ParallelTransition(
        TransitionUtil.createOutFader(weatherIconView),
        TransitionUtil.createOutFader(cityLabel),
        TransitionUtil.createOutFader(degreeLabel),
        TransitionUtil.createOutFader(tempLabel)
    );
    transition.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        weatherIconView.setImage(image);
        cityLabel.setText(activeWeather.getCity());
        degreeLabel.setText(activeWeather.getTemp() + "°C");
        tempLabel.setText(activeWeather.getHighTemp() + "/" + activeWeather.getLowTemp() + " °C");

        ParallelTransition pt = new ParallelTransition(
            TransitionUtil.createInFader(weatherIconView),
            TransitionUtil.createInFader(cityLabel),
            TransitionUtil.createInFader(degreeLabel),
            TransitionUtil.createInFader(tempLabel)
        );
        pt.play();
      }
    });
    transition.play();

    Iterator<Weather> iterator = activeWeather.getForecast().iterator();
    iterator.next();
    Iterator<WeatherForecastPanel> panelIterator = forecastPanels.iterator();
    while(iterator.hasNext()) {
      panelIterator.next().setForecast(iterator.next());
    }
  }

  // ------------------- Helper ----------------

  private void buildUI() {
    Weather weather = Callete.getWeatherService().getWeatherAt(1);
    if(weather == null) {
      return;
    }
    
    busyIndicator = new HBox();
    ComponentUtil.createLabel("Lade Bildergallerie...", "weather-busy-indicator", busyIndicator);
    busyIndicator.setPadding(new Insets(90, 30, 0, 30));

    ProgressIndicator pi = new ProgressIndicator();
    pi.setMaxHeight(25);
    busyIndicator.getChildren().add(pi);

    getChildren().add(busyIndicator);

    getChildren().add(slideshowPanel);

    VBox root = new VBox();
    cityLabel = ComponentUtil.createCustomLabel("", "weather-city", root);
    cityLabel.setPadding(new Insets(20, 30, 0, 30));

    VBox spacer = new VBox();
    spacer.setMinHeight(205);
    root.getChildren().add(spacer);

    root.getChildren().add(scroller);

    StackPane status = new StackPane();
    status.setMinHeight(120);
    status.getStyleClass().add("weather-status-panel");

    HBox weatherStatus = new HBox();
    status.getChildren().add(weatherStatus);
    weatherStatus.setPadding(new Insets(10, 0, 0, 0));

    VBox locationDetailsBox = new VBox(0);
    locationDetailsBox.setMinWidth(300);
    locationDetailsBox.setAlignment(Pos.TOP_CENTER);
    weatherStatus.getChildren().add(locationDetailsBox);

    HBox row1 = new HBox(25);
    row1.setAlignment(Pos.TOP_CENTER);
    locationDetailsBox.getChildren().addAll(row1);

    degreeLabel = ComponentUtil.createText(weather.getTemp() + "°C", "weather-degree", row1);
    VBox imgWrapper = new VBox();
    imgWrapper.setAlignment(Pos.TOP_CENTER);
    imgWrapper.setPadding(new Insets(14, 0, 0, 0));
    weatherIconView = new ImageView(new Image(WeatherConditionMapper.getWeatherForecastIcon(weather), 55, 55, false, true));
    imgWrapper.getChildren().add(weatherIconView);
    tempLabel = ComponentUtil.createText(weather.getHighTemp() + "/" + weather.getLowTemp() + " °C", "forecast-temp", imgWrapper);
    row1.getChildren().add(imgWrapper);

    Iterator<Weather> iterator = weather.getForecast().iterator();
    iterator.next();
    while(iterator.hasNext()) {
      WeatherForecastPanel forecastPanel = new WeatherForecastPanel(iterator.next());
      forecastPanel.setMinWidth(95);
      weatherStatus.getChildren().add(forecastPanel);
      forecastPanels.add(forecastPanel);
    }

    root.getChildren().add(status);
    getChildren().add(root);
  }

  private SlideShow getSlideShow(String city) {
    if(!cachedSlideShows.containsKey(city)) {
      LOG.info("Loading image gallery for " + city + " into cache.");
      cachedSlideShows.put(city, new SlideShowImpl(new File("slideshows/" + city.toLowerCase() + "/"), true));
    }
    return cachedSlideShows.get(city);
  }
}
