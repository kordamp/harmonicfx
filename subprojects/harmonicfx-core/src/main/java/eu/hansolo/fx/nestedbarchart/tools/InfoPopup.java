/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2013-2020 The original authors
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
package eu.hansolo.fx.nestedbarchart.tools;

import eu.hansolo.fx.fonts.Fonts;
import eu.hansolo.fx.nestedbarchart.Item;
import eu.hansolo.fx.nestedbarchart.event.SelectionEvent;
import eu.hansolo.fx.nestedbarchart.series.Series;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.Locale;


public class InfoPopup extends Popup {
    private HBox            hBox;
    private Text            seriesNameText;
    private Text            seriesValueText;
    private Text            itemNameText;
    private Text            itemValueText;
    private FadeTransition  fadeIn;
    private FadeTransition  fadeOut;
    private PauseTransition delay;
    private long            timeout;
    private int             decimals;
    private String          unit;
    private String          formatString;


    // ******************** Constructors **************************************
    public InfoPopup() {
        super();
        timeout  = 4000;
        decimals = 0;
        unit     = "";
        formatString = new StringBuilder("%.").append(decimals).append("f ").append(unit).toString();
        init();
        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void init() {
        setAutoFix(true);

        fadeIn = new FadeTransition(Duration.millis(200), hBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(0.75);

        fadeOut = new FadeTransition(Duration.millis(200), hBox);
        fadeOut.setFromValue(0.75);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> hide());

        delay = new PauseTransition(Duration.millis(timeout));
        delay.setOnFinished(e -> animatedHide());
    }

    private void initGraphics() {
        Font regularFont = Fonts.latoRegular(10);
        Font lightFont   = Fonts.latoLight(10);

        Text seriesText = new Text("SERIES");
        seriesText.setFill(Color.WHITE);
        seriesText.setFont(regularFont);

        seriesNameText = new Text("-");
        seriesNameText.setFill(Color.WHITE);
        seriesNameText.setFont(lightFont);

        Text seriesSumText = new Text("SUM");
        seriesSumText.setFill(Color.WHITE);
        seriesSumText.setFont(regularFont);

        seriesValueText = new Text("-");
        seriesValueText.setFill(Color.WHITE);
        seriesValueText.setFont(lightFont);

        Text itemText = new Text("ITEM");
        itemText.setFill(Color.WHITE);
        itemText.setFont(regularFont);

        itemNameText = new Text("-");
        itemNameText.setFill(Color.WHITE);
        itemNameText.setFont(lightFont);

        Text valueText = new Text("VALUE");
        valueText.setFill(Color.WHITE);
        valueText.setFont(regularFont);

        itemValueText = new Text("-");
        itemValueText.setFill(Color.WHITE);
        itemValueText.setFont(lightFont);

        Line line = new Line(0, 0, 0, 56);
        line.setStroke(Color.WHITE);

        VBox vBoxTitles = new VBox(2, seriesText, seriesSumText, itemText, valueText);
        vBoxTitles.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(itemText, new Insets(5, 0, 0, 0));

        VBox vBoxValues = new VBox(2, seriesNameText, seriesValueText, itemNameText, itemValueText);
        vBoxValues.setAlignment(Pos.CENTER_RIGHT);
        VBox.setMargin(itemNameText, new Insets(5, 0, 0, 0));
        HBox.setHgrow(vBoxValues, Priority.ALWAYS);

        hBox = new HBox(5, vBoxTitles, line, vBoxValues);
        hBox.setPrefSize(120, 66);
        hBox.setPadding(new Insets(5, 5, 5, 5));
        hBox.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.75), new CornerRadii(3), Insets.EMPTY)));

        getContent().addAll(hBox);
    }

    private void registerListeners() { }


    // ******************** Methods *******************************************
    public void animatedShow(final Window WINDOW) {
        show(WINDOW);
        fadeIn.play();
        delay.playFromStart();
    }
    public void animatedHide() { fadeOut.play(); }

    public void setTimeout(final long TIMEOUT) {
        timeout = Helper.clamp(0, 10000, TIMEOUT);
    }

    public void setDecimals(final int DECIMALS) {
        decimals     = Helper.clamp(0, 6, DECIMALS);
        formatString = new StringBuilder("%.").append(decimals).append("f ").append(unit).toString();
    }

    public void setUnit(final String UNIT) {
        unit         = UNIT;
        formatString = new StringBuilder("%.").append(decimals).append("f ").append(unit).toString();
    }

    public void update(final SelectionEvent EVENT) {
        Series series = EVENT.getSeries();
        Item   item   = EVENT.getItem();
        if (null != series) {
            seriesNameText.setText(EVENT.getSeries().getName());
            seriesValueText.setText(String.format(Locale.US, formatString, EVENT.getSeries().getSumOfAllItems()));
        }
        if (null != item) {
            itemNameText.setText(null == EVENT.getItem() ? "-" : EVENT.getItem().getName());
            itemValueText.setText(null == EVENT.getItem() ? "-" : String.format(Locale.US, formatString, EVENT.getItem().getValue()));
        }
    }
}