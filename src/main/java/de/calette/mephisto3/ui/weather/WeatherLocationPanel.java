package de.calette.mephisto3.ui.weather;

import callete.api.services.weather.Weather;
import de.calette.mephisto3.Mephisto3;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class WeatherLocationPanel extends StackPane {
  public static final String DATE_FORMAT = "dd. MMMM yyyy";
  public static final String TIME_FORMAT = "hh:mm";
  private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
  private SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);

  private List<WeatherForecastPanel> forecastPanelList = new ArrayList<>();


  public WeatherLocationPanel(Weather weather) {
    VBox root = new VBox(12);
    root.setPadding(new Insets(0, 20, 0, 20));
    root.setMinWidth(Mephisto3.WIDTH);

    HBox topPanel = new HBox(30);
    topPanel.setAlignment(Pos.TOP_LEFT);

    //top left
    Text degree = new Text(weather.getTemp() + " °C");
    degree.getStyleClass().add("weather-degree");

    topPanel.getChildren().add(degree);
    root.getChildren().add(topPanel);

    //top right
    VBox topDetailPanel = new VBox();
    topDetailPanel.setAlignment(Pos.CENTER_LEFT);
    topPanel.getChildren().add(topDetailPanel);

    Text location = new Text(weather.getCity() + ", " + weather.getCountry());
    location.getStyleClass().add("weather-city");
    topDetailPanel.getChildren().add(location);

    Text date = new Text(dateFormat.format(weather.getForecastDate()));
    date.getStyleClass().add("weather-date");
    topDetailPanel.getChildren().add(date);

    //middle
    HBox center = new HBox(70);
    center.setAlignment(Pos.TOP_LEFT);
    center.setPadding(new Insets(0,0,10,0));
    root.getChildren().add(center);

    createCenterBox(center, weather.getLowTemp() + "/" + weather.getHighTemp() + " °C", "min/max");
    createCenterBox(center, "Aufgang: " + timeFormat.format(weather.getSunrise()) + " Uhr", "Untergang: " + timeFormat.format(weather.getSunset()) + " Uhr");
    createCenterBox(center, "Luftfeuchtigkeit: " + weather.getHumidity() + "%", "Bedingung: " + WeatherConditionMapper.getWeatherConditionText(weather.getWeatherState()));


    //forecast
    HBox south = new HBox(10);
    root.getChildren().add(south);

    for (int i = 0; i < weather.getForecast().size(); i++) {
      Weather forecast = weather.getForecast().get(i);
      String dateString = null;
      if (i == 0) {
        dateString = "Heute";
      }
      if (i == 1) {
        dateString = "Morgen";
      }

      final WeatherForecastPanel forecastPanel = new WeatherForecastPanel(dateString, forecast);
      south.getChildren().add(forecastPanel);
      forecastPanelList.add(forecastPanel);
    }

    getChildren().add(root);
  }


  // ------------------------ Helper ------------------------------

  private VBox createCenterBox(HBox root, String value1, String value2) {
    VBox box = new VBox(5);
//    tempsBox.setStyle("-fx-border-color: white;");
    box.setAlignment(Pos.TOP_LEFT);
    root.getChildren().add(box);

    Text valueText = new Text(value1);
    valueText.getStyleClass().add("weather-center");
    box.getChildren().add(valueText);
    Text labelText = new Text(value2);
    labelText.getStyleClass().add("weather-center");
    box.getChildren().add(labelText);
    return box;
  }
}
