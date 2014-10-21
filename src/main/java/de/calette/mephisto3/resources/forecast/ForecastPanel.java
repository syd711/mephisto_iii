package de.calette.mephisto3.resources.forecast;

import callete.api.services.weather.Weather;
import de.calette.mephisto3.ui.weather.WeatherConditionMapper;
import de.calette.mephisto3.util.ComponentUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.text.SimpleDateFormat;

/**
 * A single forecast panel.
 */
public class ForecastPanel extends VBox {
  private static final String FORECAST_DATE_FORMAT = "dd";
  private static final String FORECAST_DAY_FORMAT = "EE";
  private static final SimpleDateFormat forecastDateFormat = new SimpleDateFormat(FORECAST_DATE_FORMAT);
  private static final SimpleDateFormat forecastDayFormat = new SimpleDateFormat(FORECAST_DAY_FORMAT);
  public static final int PADDING = 10;
  public static final int WIDTH = 120-(PADDING*2);

  public ForecastPanel(String label, Weather forecast) {
    super(5);
    setOpacity(0);
    setAlignment(Pos.TOP_LEFT);
    getStyleClass().add("forecast-panel");
    setMinWidth(WIDTH);
    setMinHeight(145);
    setPadding(new Insets(PADDING,PADDING,PADDING, PADDING));

    //title
    HBox titleBox = new HBox(5);
    titleBox.setAlignment(Pos.TOP_LEFT);
    String title = label;
    if(label == null) {
      title = forecastDayFormat.format(forecast.getForecastDate());
    }
    final Text day = new Text(title);
    day.getStyleClass().add("forecast-title-bold");
    titleBox.getChildren().add(day);
    if(label == null) {
      final Text date = new Text(forecastDateFormat.format(forecast.getForecastDate()));
      date.getStyleClass().add("forecast-title");
      titleBox.getChildren().add(date);
    }

    getChildren().add(titleBox);

    //forecast image
    final Canvas forecastImage = ComponentUtil.createImageCanvas(WeatherForecastResourceLoader.getResource("weather-cloud-sun.png"), 49, 36);
    HBox image = new HBox();
    image.setAlignment(Pos.CENTER);
    image.setMinWidth(WIDTH);
    image.getChildren().add(forecastImage);
    getChildren().add(image);

    //subtext
    Text forecastTemp = new Text(forecast.getLowTemp() + "/" + forecast.getHighTemp() + " °C");
    forecastTemp.getStyleClass().add("forecast-sub");
    getChildren().add(forecastTemp);

    Text condition = new Text(WeatherConditionMapper.getWeatherConditionText(forecast.getWeatherState()));
    condition.getStyleClass().add("forecast-sub");
    getChildren().add(condition);
  }
}
