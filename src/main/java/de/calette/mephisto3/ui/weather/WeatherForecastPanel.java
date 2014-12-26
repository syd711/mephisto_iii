package de.calette.mephisto3.ui.weather;

import callete.api.services.weather.Weather;
import de.calette.mephisto3.util.ComponentUtil;
import de.calette.mephisto3.util.TransitionUtil;
import javafx.animation.ParallelTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.text.SimpleDateFormat;

/**
 * A single forecast panel.
 */
public class WeatherForecastPanel extends VBox {
  private static final String FORECAST_DAY_FORMAT = "EE, dd.";
  private static final SimpleDateFormat forecastDayFormat = new SimpleDateFormat(FORECAST_DAY_FORMAT);

  private Text tempLabel;
  private Text titleLabel;
  private ImageView img;

  public WeatherForecastPanel(Weather forecast) {
    super(3);
    getStyleClass().add("forecast-panel");
    setAlignment(Pos.TOP_CENTER);

    setPadding(new Insets(0, 10, 10, 10));
    String day = forecastDayFormat.format(forecast.getForecastDate());
    titleLabel = ComponentUtil.createText(day, "default-white-16", this);
    img = new ImageView(new Image(WeatherConditionMapper.getWeatherForecastIcon(forecast), 55, 55, false, true));
    getChildren().add(img);

    tempLabel = ComponentUtil.createText(forecast.getHighTemp() + "/" + forecast.getLowTemp() + " °C", "forecast-temp", this);
  }

  public void setForecast(final Weather forecast) {
    final Image image = new Image(WeatherConditionMapper.getWeatherForecastIcon(forecast));

    ParallelTransition pt = new ParallelTransition(
            TransitionUtil.createOutFader(tempLabel),
            TransitionUtil.createOutFader(titleLabel),
            TransitionUtil.createOutFader(img)
    );
    pt.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        String day = forecastDayFormat.format(forecast.getForecastDate());
        titleLabel.setText(day);
        img.setImage(image);
        tempLabel.setText(forecast.getHighTemp() + "/" + forecast.getLowTemp() + " °C");

        ParallelTransition inFader = new ParallelTransition(
                TransitionUtil.createInFader(tempLabel),
                TransitionUtil.createInFader(titleLabel),
                TransitionUtil.createInFader(img)
        );
        inFader.play();
      }
    });
    pt.play();
  }
}
