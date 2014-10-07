package de.calette.mephisto3.ui;

import callete.api.Callete;
import callete.api.services.weather.Weather;
import de.calette.mephisto3.resources.weather.WeatherQuickInfoResourceLoader;
import de.calette.mephisto3.util.ComponentUtil;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The UI header with datetime and temperature.
 */
public class Header extends BorderPane {
  public static final String DATE_FORMAT = "EEEE, dd. MMMM yyyy   HH:mm";

  private SimpleDateFormat simpleDateFormat;

  public Header() {
    setPadding(new Insets(6, 10, 10, 10));
    HBox topLeft = new HBox();
    final Text weatherInfoText = new Text();
    weatherInfoText.setId("header-weather");
    weatherInfoText.setOpacity(0);
    topLeft.getChildren().add(weatherInfoText);
    setLeft(topLeft);

    final Canvas imageCanvas = ComponentUtil.createImageCanvas(WeatherQuickInfoResourceLoader.getResource("weather-rain.png"), 32, 32);
    imageCanvas.setOpacity(0);
    topLeft.getChildren().add(imageCanvas);

    simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.GERMAN);
    final Text dateTimeText = new Text("");
    dateTimeText.setOpacity(0);
    dateTimeText.setId("header-datetime");
    setRight(dateTimeText);

    //apply datetime timer
    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            String dateTime = simpleDateFormat.format(new Date());
            dateTimeText.setText(dateTime);
            if(dateTimeText.getOpacity() == 0) {
              TransitionUtil.createInFader(dateTimeText).play();
            }
          }
        });
      }
    }, 0, 60000);

    //apply weather timer
    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            Weather weather = Callete.getWeatherService().getWeatherAt(1);
            weatherInfoText.setText(weather.getTemp() + " °C");
            if(weatherInfoText.getOpacity() == 0) {
              TransitionUtil.createInFader(weatherInfoText).play();
              TransitionUtil.createInFader(imageCanvas).play();
            }
          }
        });
      }
    }, 0, 60000);
  }
}
