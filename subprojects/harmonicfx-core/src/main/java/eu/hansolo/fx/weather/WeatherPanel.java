/*
 * Copyright 2013-2016 Gerrit Grunwald.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.hansolo.fx.weather;

import eu.hansolo.fx.fonts.Fonts;
import eu.hansolo.fx.weather.darksky.DarkSky.Condition;
import eu.hansolo.fx.weather.darksky.DarkSky.Unit;
import eu.hansolo.fx.weather.event.WeatherEventListener;
import javafx.application.Platform;
import javafx.beans.DefaultProperty;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;


/**
 * User: hansolo
 * Date: 01.10.16
 * Time: 04:30
 */
@DefaultProperty("children")
public class WeatherPanel extends Region {
    private static final DateTimeFormatter        DATE_FORMATTER     = DateTimeFormatter.ofPattern("EEE dd. MMM");
    private static final DateTimeFormatter        TIME_FORMATTER     = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final double                   PREFERRED_WIDTH    = 800;
    private static final double                   PREFERRED_HEIGHT   = 480;
    private static final double                   MINIMUM_WIDTH      = 80;
    private static final double                   MINIMUM_HEIGHT     = 48;
    private static final double                   MAXIMUM_WIDTH      = 8000;
    private static final double                   MAXIMUM_HEIGHT     = 4800;
    private static       double                   aspectRatio;
    private volatile     ScheduledFuture<?>       periodicTickTask;
    private static       ScheduledExecutorService periodicTickExecutorService;
    private              Location                 location;
    private              boolean                  keepAspect;
    private              double                   width;
    private              double                   height;
    private              WeatherSymbol            weatherSymbol;
    private              Label                    city;
    private              Label                    date;
    private              Label                    time;
    private              Label                    temperature;
    private              WeatherSymbol            pressureSymbol;
    private              Label                    pressure;
    private              Label                    pressureUnit;
    private              WeatherSymbol            humiditySymbol;
    private              Label                    humidity;
    private              Label                    humidityUnit;
    private              WeatherSymbol            windSpeedSymbol;
    private              Label                    windSpeed;
    private              Label                    windSpeedUnit;
    private              DayPanel                 dayPanel1;
    private              DayPanel                 dayPanel2;
    private              DayPanel                 dayPanel3;
    private              DayPanel                 dayPanel4;
    private              DayPanel                 dayPanel5;
    private              GridPane                 weatherGrid;
    private              AnchorPane               pane;
    private              Paint                    backgroundPaint;
    private              Paint                    borderPaint;
    private              double                   borderWidth;
    private              WeatherEventListener     weatherEventListener;


    // ******************** Constructors **************************************
    public WeatherPanel(final Location LOCATION) {
        getStylesheets().add(WeatherPanel.class.getResource("weatherfx.css").toExternalForm());
        aspectRatio      = PREFERRED_HEIGHT / PREFERRED_WIDTH;
        keepAspect       = false;
        backgroundPaint  = Color.TRANSPARENT;
        borderPaint      = Color.TRANSPARENT;
        borderWidth      = 0d;
        location         = LOCATION;

        initGraphics();
        initListener();
        registerListeners();

        scheduleTickTask();
    }


    // ******************** Initialization ************************************
    private void initGraphics() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 ||
            Double.compare(getWidth(), 0.0) <= 0 || Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        getStyleClass().add("weather-panel");

        weatherSymbol   = new WeatherSymbol(Condition.NONE, 250, Color.WHITE);

        temperature     = createLabel("0°", Fonts.robotoCondensedRegular(110), Color.WHITE, Pos.CENTER);

        city            = createLabel(location.getCity().replace("_", " "), Fonts.robotoLight(48), Color.WHITE, Pos.CENTER_RIGHT);
        date            = createLabel(DATE_FORMATTER.format(LocalDateTime.now()), Fonts.robotoThin(36), Color.WHITE, Pos.CENTER_RIGHT);
        time            = createLabel(TIME_FORMATTER.format(LocalDateTime.now()), Fonts.robotoThin(36), Color.WHITE, Pos.CENTER_RIGHT);

        pressureSymbol  = new WeatherSymbol(Condition.PRESSURE, 28, Color.WHITE);
        pressure        = createLabel("0", Fonts.robotoThin(20), Color.WHITE, Pos.CENTER_RIGHT);
        pressureUnit    = createLabel("mb", Fonts.robotoThin(14), Color.WHITE, Pos.CENTER_RIGHT);
        pressureUnit.setPadding(new Insets(4, 0, 0, 0));

        humiditySymbol  = new WeatherSymbol(Condition.HUMIDITY, 28, Color.WHITE);
        humidity        = createLabel("0", Fonts.robotoThin(20), Color.WHITE, Pos.CENTER_RIGHT);
        humidityUnit    = createLabel("%", Fonts.robotoThin(14), Color.WHITE, Pos.CENTER_RIGHT);
        humidityUnit.setPadding(new Insets(4, 0, 0, 0));

        windSpeedSymbol = new WeatherSymbol(Condition.WIND, 28, Color.WHITE);
        windSpeed       = createLabel("0", Fonts.robotoThin(20), Color.WHITE, Pos.CENTER_RIGHT);
        windSpeedUnit   = createLabel("kph", Fonts.robotoThin(14), Color.WHITE, Pos.CENTER_RIGHT);
        windSpeedUnit.setPadding(new Insets(4, 0, 0, 0));

        dayPanel1 = new DayPanel(location.getCondition(), LocalDate.now().plusDays(1), 0, 0);
        dayPanel2 = new DayPanel(location.getCondition(), LocalDate.now().plusDays(2), 0, 0);
        dayPanel3 = new DayPanel(location.getCondition(), LocalDate.now().plusDays(3), 0, 0);
        dayPanel4 = new DayPanel(location.getCondition(), LocalDate.now().plusDays(4), 0, 0);
        dayPanel5 = new DayPanel(location.getCondition(), LocalDate.now().plusDays(5), 0, 0);

        HBox forecastBox = new HBox(dayPanel1, dayPanel2, dayPanel3, dayPanel4, dayPanel5);
        forecastBox.getStyleClass().add("forecast-box");
        forecastBox.setFillHeight(true);
        HBox.setHgrow(dayPanel1, Priority.ALWAYS);
        HBox.setHgrow(dayPanel2, Priority.ALWAYS);
        HBox.setHgrow(dayPanel3, Priority.ALWAYS);
        HBox.setHgrow(dayPanel4, Priority.ALWAYS);
        HBox.setHgrow(dayPanel5, Priority.ALWAYS);

        weatherGrid = new GridPane();
        weatherGrid.add(pressureSymbol, 0, 0);
        weatherGrid.add(pressure, 1, 0);
        weatherGrid.add(pressureUnit, 2, 0);
        weatherGrid.add(humiditySymbol, 0, 1);
        weatherGrid.add(humidity, 1, 1);
        weatherGrid.add(humidityUnit, 2, 1);
        weatherGrid.add(windSpeedSymbol, 0, 2);
        weatherGrid.add(windSpeed, 1, 2);
        weatherGrid.add(windSpeedUnit, 2, 2);

        GridPane.setHalignment(pressure, HPos.RIGHT);
        GridPane.setHalignment(pressureUnit, HPos.RIGHT);
        GridPane.setHalignment(humidity, HPos.RIGHT);
        GridPane.setHalignment(humidityUnit, HPos.RIGHT);
        GridPane.setHalignment(windSpeed, HPos.RIGHT);
        GridPane.setHalignment(windSpeedUnit, HPos.RIGHT);

        AnchorPane.setTopAnchor(weatherSymbol, 20d);
        AnchorPane.setLeftAnchor(weatherSymbol, 20d);

        AnchorPane.setTopAnchor(city, 10d);
        AnchorPane.setRightAnchor(city, 20d);

        AnchorPane.setTopAnchor(date, 80d);
        AnchorPane.setRightAnchor(date, 20d);

        AnchorPane.setTopAnchor(time, 125d);
        AnchorPane.setRightAnchor(time, 20d);

        AnchorPane.setTopAnchor(temperature, 80d);
        AnchorPane.setLeftAnchor(temperature, 360d);

        AnchorPane.setTopAnchor(weatherGrid, 180d);
        AnchorPane.setRightAnchor(weatherGrid, 20d);

        AnchorPane.setLeftAnchor(forecastBox, 20d);
        AnchorPane.setRightAnchor(forecastBox, 20d);
        AnchorPane.setBottomAnchor(forecastBox, 20d);

        pane = new AnchorPane(weatherSymbol, city, date, time, temperature, weatherGrid, forecastBox);
        pane.setBackground(new Background(new BackgroundFill(backgroundPaint, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(borderPaint, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(borderWidth))));

        getChildren().setAll(pane);
    }

    private void initListener() {
        weatherEventListener = evt -> Platform.runLater(() -> {
            Location location = evt.getLocation();
            city.setText(location.getCity().replace("_", " "));
            date.setText(DATE_FORMATTER.format(LocalDateTime.now()));
            temperature.setText(String.format(Locale.US, "%.0f°", location.getTemperature()));
            pressure.setText(String.format(Locale.US, "%.0f", location.getPressure()));
            humidity.setText(String.format(Locale.US, "%.0f", location.getHumidity() * 100.0));
            windSpeed.setText(String.format(Locale.US, "%.0f", location.getWindSpeed()));
            weatherSymbol.setCondition(evt.getLocation().getCondition());

            Unit unit = location.getUnit();
            switch(unit) {
                case SI : windSpeedUnit.setText("mps"); break;
                case CA : windSpeedUnit.setText("kph"); break;
                case UK2: windSpeedUnit.setText("mph"); break;
                case US : windSpeedUnit.setText("mph"); break;
            }

            if (location.getForecast().size() < 5) return;
            dayPanel1.setDate(LocalDate.now().plusDays(1));
            dayPanel1.setCondition(location.getForecast().get(0).getCondition());
            dayPanel1.setMinTemp(location.getForecast().get(0).getTemperatureMin());
            dayPanel1.setMaxTemp(location.getForecast().get(0).getTemperatureMax());

            dayPanel2.setDate(LocalDate.now().plusDays(2));
            dayPanel2.setCondition(location.getForecast().get(1).getCondition());
            dayPanel2.setMinTemp(location.getForecast().get(1).getTemperatureMin());
            dayPanel2.setMaxTemp(location.getForecast().get(1).getTemperatureMax());

            dayPanel3.setDate(LocalDate.now().plusDays(3));
            dayPanel3.setCondition(location.getForecast().get(2).getCondition());
            dayPanel3.setMinTemp(location.getForecast().get(2).getTemperatureMin());
            dayPanel3.setMaxTemp(location.getForecast().get(2).getTemperatureMax());

            dayPanel4.setDate(LocalDate.now().plusDays(4));
            dayPanel4.setCondition(location.getForecast().get(3).getCondition());
            dayPanel4.setMinTemp(location.getForecast().get(3).getTemperatureMin());
            dayPanel4.setMaxTemp(location.getForecast().get(3).getTemperatureMax());

            dayPanel5.setDate(LocalDate.now().plusDays(5));
            dayPanel5.setCondition(location.getForecast().get(4).getCondition());
            dayPanel5.setMinTemp(location.getForecast().get(4).getTemperatureMin());
            dayPanel5.setMaxTemp(location.getForecast().get(4).getTemperatureMax());
        });
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        location.setOnWeatherEvent(weatherEventListener);
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double HEIGHT) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public Location getLocation() { return location; }
    public void setLocation(final Location LOCATION) {
        location.removeWeatherEventListener(weatherEventListener);
        location = LOCATION;
        location.setOnWeatherEvent(weatherEventListener);
        resize();
    }

    private Label createLabel(final String TEXT, final Font FONT, final Color FILL, final Pos ALIGNMENT) {
        Label label = new Label(TEXT);
        label.setFont(FONT);
        label.setTextFill(FILL);
        label.setAlignment(ALIGNMENT);
        return label;
    }

    private void updateTime() {
        time.setText(TIME_FORMATTER.format(LocalDateTime.now()));
    }


    // ******************** Scheduled tasks ***********************************
    private synchronized static void enableTickExecutorService() {
        if (null == periodicTickExecutorService) {
            periodicTickExecutorService = new ScheduledThreadPoolExecutor(1, getThreadFactory("ClockTick", true));
        }
    }
    private synchronized void scheduleTickTask() {
        enableTickExecutorService();
        stopTask(periodicTickTask);
        periodicTickTask = periodicTickExecutorService.scheduleAtFixedRate(() -> Platform.runLater(() -> updateTime()), 0, 1, TimeUnit.SECONDS);
    }

    public static final ThreadFactory getThreadFactory(final String THREAD_NAME, final boolean IS_DAEMON) {
        return runnable -> {
            Thread thread = new Thread(runnable, THREAD_NAME);
            thread.setDaemon(IS_DAEMON);
            return thread;
        };
    }

    public static final void stopTask(ScheduledFuture<?> task) {
        if (null == task) return;
        task.cancel(true);
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        if (keepAspect) {
            if (aspectRatio * width > height) {
                width = 1 / (aspectRatio / height);
            } else if (1 / (aspectRatio / height) > width) {
                height = aspectRatio * width;
            }
        }

        double size = width < height ? width : height;

        if (width > 0 && height > 0) {
            pane.setMaxSize(width, height);
            pane.setPrefSize(width, height);
            pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            weatherSymbol.setPrefSize(size * 0.52083333, size * 0.52083333);

            temperature.setFont(Fonts.robotoRegular(size * 0.22916667));

            if (width > height) {
                temperature.setPrefWidth(Control.USE_COMPUTED_SIZE);
                AnchorPane.setTopAnchor(temperature, height * 0.166666);
                AnchorPane.setLeftAnchor(temperature, width * 0.45);
            } else {
                temperature.setPrefWidth(width);
                AnchorPane.setTopAnchor(temperature, height * 0.45);
                AnchorPane.setLeftAnchor(temperature, 0d);
            }

            city.setFont(Fonts.robotoLight(size * 0.1));

            date.setFont(Fonts.robotoThin(size * 0.075));
            AnchorPane.setTopAnchor(date, size * 0.16666667);

            time.setFont(Fonts.robotoThin(size * 0.075));
            AnchorPane.setTopAnchor(time, size * 0.26041667);

            pressure.setFont(Fonts.robotoThin(size * 0.04166667));
            pressureUnit.setFont(Fonts.robotoThin(size * 0.02916667));
            pressureUnit.setPadding(new Insets(size * 0.00833333, 0, 0, 0));

            humidity.setFont(Fonts.robotoThin(size * 0.04166667));
            humidityUnit.setFont(Fonts.robotoThin(size * 0.02916667));
            humidityUnit.setPadding(new Insets(size * 0.00833333, 0, 0, 0));

            windSpeed.setFont(Fonts.robotoThin(size * 0.04166667));
            windSpeedUnit.setFont(Fonts.robotoThin(size * 0.02916667));
            windSpeedUnit.setPadding(new Insets(size * 0.00833333, 0, 0, 0));

            AnchorPane.setTopAnchor(weatherGrid, size * 0.4);
            weatherGrid.setVgap(size * 0.01041667);
            weatherGrid.setHgap(size * 0.01041667);

            pressureSymbol.setPrefSize(size * 0.05833333, size * 0.05833333);
            humiditySymbol.setPrefSize(size * 0.05833333, size * 0.05833333);
            windSpeedSymbol.setPrefSize(size * 0.05833333, size * 0.05833333);

            dayPanel1.setPrefSize(size * 0.20833333, size * 0.3125);
            dayPanel2.setPrefSize(size * 0.20833333, size * 0.3125);
            dayPanel3.setPrefSize(size * 0.20833333, size * 0.3125);
            dayPanel4.setPrefSize(size * 0.20833333, size * 0.3125);
            dayPanel5.setPrefSize(size * 0.20833333, size * 0.3125);

            redraw();
        }
    }

    private void redraw() {
        pane.setBackground(new Background(new BackgroundFill(backgroundPaint, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(borderPaint, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(borderWidth / PREFERRED_WIDTH * height))));
    }
}
