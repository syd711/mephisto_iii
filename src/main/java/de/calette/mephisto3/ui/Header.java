package de.calette.mephisto3.ui;

import callete.api.Callete;
import callete.api.services.weather.Weather;
import callete.api.services.weather.WeatherService;
import de.calette.mephisto3.Mephisto3;
import de.calette.mephisto3.resources.ResourceLoader;
import de.calette.mephisto3.resources.weather.WeatherQuickInfoResourceLoader;
import de.calette.mephisto3.ui.weather.WeatherConditionMapper;
import de.calette.mephisto3.util.ComponentUtil;
import de.calette.mephisto3.util.Executor;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
  public static final String DATE_FORMAT = "EEEE, dd. MMMM yyyy";
  public static final String TIME_FORMAT = "HH:mm";
  public static final String CSS_CLASS = "header-text";
  public static final int TOP_TEXT_MARGIN = 2;

  private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.GERMAN);
  private SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.GERMAN);

  public Header() {
    setMaxWidth(Mephisto3.WIDTH);
    setPadding(new Insets(5, 5, 10, 5));

    //left box
    HBox topLeft = new HBox(5);
    VBox textBox = new VBox();
    textBox.setPadding(new Insets(TOP_TEXT_MARGIN,0,0,0));
    final Text weatherInfoText = new Text();
    weatherInfoText.getStyleClass().add(CSS_CLASS);
    weatherInfoText.setOpacity(0);
    textBox.getChildren().add(weatherInfoText);

    final Canvas weatherImageCanvas = ComponentUtil.createImageCanvas(WeatherQuickInfoResourceLoader.getResource("snow-rainy.png"), 32, 32);
    weatherImageCanvas.setOpacity(0);
    topLeft.getChildren().add(weatherImageCanvas);
    topLeft.getChildren().add(textBox);

    setLeft(topLeft);

    //center box
    final HBox dateBox = new HBox(5);
    dateBox.setPadding(new Insets(TOP_TEXT_MARGIN, 0, 0, 0));
    dateBox.setAlignment(Pos.BASELINE_RIGHT);
    dateBox.setOpacity(0);
    final Text dateText = new Text("");
    dateText.getStyleClass().add("header-text");
    dateBox.getChildren().add(dateText);
    setCenter(dateBox);

    //right box
    final HBox timeBox = new HBox(10);
    timeBox.setOpacity(0);
    VBox timeIconBox = new VBox();
    timeIconBox.setPadding(new Insets(7, 0, 0, 20));
    Canvas timeCanvas = ComponentUtil.createImageCanvas(ResourceLoader.getResource("time.png"), 16, 16);
    timeIconBox.getChildren().add(timeCanvas);
    timeBox.getChildren().add(timeIconBox);
    final Text timeText = new Text("");
    timeText.getStyleClass().add("header-text");
    VBox timeTextBox = new VBox();
    timeTextBox.setPadding(new Insets(TOP_TEXT_MARGIN, 5, 0, 0));
    timeTextBox.getChildren().add(timeText);
    timeBox.getChildren().add(timeTextBox);

    setRight(timeBox);

    //apply datetime timer
    new Timer().schedule(new TimerTask() {
      @Override
      public void run() {
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            String date = dateFormat.format(new Date());
            String time = timeFormat.format(new Date());
            dateText.setText(date);
            timeText.setText(time);
            if(dateBox.getOpacity() == 0) {
              TransitionUtil.createInFader(dateBox).play();
              TransitionUtil.createInFader(timeBox).play();
            }
          }
        });
      }
    }, 0, 60000);


    Executor.run(new Runnable() {
      @Override
      public void run() {
        //apply weather timer
        new Timer().schedule(new TimerTask() {
          @Override
          public void run() {
            Platform.runLater(new Runnable() {
              @Override
              public void run() {
                Weather weather = Callete.getWeatherService().getWeatherAt(1);
                weatherInfoText.setText(weather.getTemp() + " °C");

                weatherImageCanvas.getGraphicsContext2D().clearRect(0, 0, 32, 32);
                String url = WeatherConditionMapper.getWeatherQuickInfoIcon(weather);
                ImageView weatherImage = new ImageView(new Image(url, 32, 32, false, true));
                weatherImageCanvas.getGraphicsContext2D().drawImage(weatherImage.getImage(), 0, 0);

                if(weatherInfoText.getOpacity() == 0) {
                  TransitionUtil.createInFader(weatherInfoText).play();
                  TransitionUtil.createInFader(weatherImageCanvas).play();
                }
              }
            });
          }
        }, 0, WeatherService.DEFAULT_REFRESH_INTERVAL);
      }
    });
  }
}
