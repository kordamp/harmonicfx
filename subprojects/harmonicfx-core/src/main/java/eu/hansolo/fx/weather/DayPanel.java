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
import javafx.beans.DefaultProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


/**
 * User: hansolo
 * Date: 29.09.16
 * Time: 11:38
 */
@DefaultProperty("children")
public class DayPanel extends Region {
    private static final DateTimeFormatter DAY_FORMATTER    = DateTimeFormatter.ofPattern("EEEE");
    private static final double            PREFERRED_WIDTH  = 100;
    private static final double            PREFERRED_HEIGHT = 150;
    private static final double            MINIMUM_WIDTH    = 20;
    private static final double            MINIMUM_HEIGHT   = 30;
    private static final double            MAXIMUM_WIDTH    = 1000;
    private static final double            MAXIMUM_HEIGHT   = 1500;
    private static final double            ASPECT_RATIO     = PREFERRED_HEIGHT / PREFERRED_WIDTH;
    private              double            width;
    private              double            height;
    private              Condition         condition;
    private              LocalDate         date;
    private              double            minTemp;
    private              double            maxTemp;
    private              Text              dayText;
    private              WeatherSymbol     weatherSymbol;
    private              Text              minMaxText;
    private              Pane              pane;
    private              Color             foregroundColor;
    private              Paint             backgroundPaint;
    private              Paint             borderPaint;
    private              double            borderWidth;


    // ******************** Constructors **************************************
    public DayPanel() {
        this(Condition.NONE, LocalDate.now(), 0, 0);
    }
    public DayPanel(final Condition CONDITION, final LocalDate DATE, final double MIN_TEMP, final double MAX_TEMP) {
        condition       = CONDITION;
        date            = DATE;
        minTemp         = MIN_TEMP;
        maxTemp         = MAX_TEMP;
        foregroundColor = Color.WHITE;
        backgroundPaint = Color.TRANSPARENT;
        borderPaint     = Color.TRANSPARENT;
        borderWidth     = 0d;
        initGraphics();
        registerListeners();
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

        dayText = new Text();
        dayText.setTextOrigin(VPos.CENTER);
        dayText.setFill(foregroundColor);

        weatherSymbol = new WeatherSymbol(Condition.NONE, 72, foregroundColor);

        minMaxText = new Text();
        minMaxText.setTextOrigin(VPos.CENTER);
        minMaxText.setFill(foregroundColor);

        pane = new Pane(dayText, weatherSymbol, minMaxText);
        pane.setBackground(new Background(new BackgroundFill(backgroundPaint, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(borderPaint, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(borderWidth))));

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        // add listeners to your propertes like
        //value.addListener(o -> handleControlPropertyChanged("VALUE"));
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double HEIGHT) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public Condition getCondition() { return condition; }
    public void setCondition(final Condition CONDITION) { condition = CONDITION; resize(); }

    public LocalDate getDate() { return date; }
    public void setDate(final LocalDate DATE) { date = DATE; resize(); }

    public double getMinTemp() { return minTemp; }
    public void setMinTemp(final double MIN_TEMP) { minTemp = MIN_TEMP; resize(); }

    public double getMaxTemp() { return maxTemp; }
    public void setMaxTemp(final double MAX_TEMP) { maxTemp = MAX_TEMP; resize(); }

    public Color getForegroundColor() { return foregroundColor; }
    public void setForegroundColor(final Color COLOR) { foregroundColor = COLOR; resize(); }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        if (ASPECT_RATIO * width > height) {
            width = 1 / (ASPECT_RATIO / height);
        } else if (1 / (ASPECT_RATIO / height) > width) {
            height = ASPECT_RATIO * width;
        }

        if (width > 0 && height > 0) {
            pane.setMaxSize(width, height);
            pane.setPrefSize(width, height);
            pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            dayText.setFont(Fonts.robotoLight(0.13333333 * height));
            dayText.setText(DAY_FORMATTER.format(date));
            dayText.setX((width - dayText.getLayoutBounds().getWidth()) * 0.5);
            dayText.setY(height * 0.1);

            weatherSymbol.setCondition(condition);
            weatherSymbol.setPrefSize(0.72 * width, 0.72 * width);
            weatherSymbol.relocate((width - weatherSymbol.getPrefWidth()) * 0.5, (height - weatherSymbol.getPrefHeight()) * 0.5);

            minMaxText.setFont(Fonts.robotoLight(0.13333333 * height));
            minMaxText.setText(String.join("/", String.format(Locale.US, "%.0f°", minTemp), String.format(Locale.US, "%.0f°", maxTemp)));
            minMaxText.setX((width - minMaxText.getLayoutBounds().getWidth()) * 0.5);
            minMaxText.setY(height * 0.9);

            redraw();
        }
    }

    private void redraw() {
        pane.setBackground(new Background(new BackgroundFill(backgroundPaint, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(borderPaint, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(borderWidth / PREFERRED_WIDTH * height))));

        dayText.setFill(foregroundColor);
        weatherSymbol.setSymbolColor(foregroundColor);
        minMaxText.setFill(foregroundColor);
    }
}
