/*
 * Copyright 2013-2018 Gerrit Grunwald.
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
package eu.hansolo.fx.chargecontrol;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;


/**
 * User: hansolo
 * Date: 07.07.16
 * Time: 09:19
 */
public class ChargeControl extends Region {
    private static final double PREFERRED_WIDTH  = 306;
    private static final double PREFERRED_HEIGHT = 66;
    private static final double MINIMUM_WIDTH    = 153;
    private static final double MINIMUM_HEIGHT   = 33;
    private static final double MAXIMUM_WIDTH    = 918;
    private static final double MAXIMUM_HEIGHT   = 198;
    private        Gauge    model;
    private        Region[] bars;
    private static double   aspectRatio;
    private        double   width;
    private        double   height;
    private        HBox     pane;
    private        Paint    backgroundPaint;
    private        Paint    borderPaint;
    private        double   borderWidth;


    // ******************** Constructors **************************************
    public ChargeControl() {
        getStylesheets().add(ChargeControl.class.getResource("chargecontrol.css").toExternalForm());
        getStyleClass().add("charge-control");
        aspectRatio     = PREFERRED_HEIGHT / PREFERRED_WIDTH;
        backgroundPaint = Color.TRANSPARENT;
        borderPaint     = Color.TRANSPARENT;
        borderWidth     = 0;
        model           = GaugeBuilder.create().maxValue(1.0).animated(true).build();
        init();
        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void init() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 ||
            Double.compare(getWidth(), 0.0) <= 0 || Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        if (Double.compare(getMinWidth(), 0.0) <= 0 || Double.compare(getMinHeight(), 0.0) <= 0) {
            setMinSize(MINIMUM_WIDTH, MINIMUM_HEIGHT);
        }

        if (Double.compare(getMaxWidth(), 0.0) <= 0 || Double.compare(getMaxHeight(), 0.0) <= 0) {
            setMaxSize(MAXIMUM_WIDTH, MAXIMUM_HEIGHT);
        }
    }

    private void initGraphics() {
        bars = new Region[] { createBar(20, "gray-bar"),
                              createBar(24, "gray-bar"),
                              createBar(28, "gray-bar"),
                              createBar(32, "gray-bar"),
                              createBar(36, "gray-bar"),
                              createBar(40, "gray-bar"),
                              createBar(44, "gray-bar"),
                              createBar(48, "gray-bar"),
                              createBar(52, "gray-bar"),
                              createBar(56, "gray-bar"),
                              createBar(60, "gray-bar"),
                              createBar(64, "gray-bar")};

        pane = new HBox(bars);
        pane.setSpacing(PREFERRED_WIDTH * 0.01960784);
        pane.setAlignment(Pos.BOTTOM_CENTER);
        pane.setFillHeight(false);
        pane.setBackground(new Background(new BackgroundFill(backgroundPaint, new CornerRadii(1024), Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(borderPaint, BorderStrokeStyle.SOLID, new CornerRadii(1024), new BorderWidths(borderWidth))));

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        model.currentValueProperty().addListener(o -> handleControlPropertyChanged("VALUE"));
    }


    // ******************** Methods *******************************************
    private void handleControlPropertyChanged(final String PROPERTY) {
        if ("VALUE".equals(PROPERTY)) {
            int chargedBars = (int) (model.getCurrentValue() * 13);
            for (int i = 0 ; i < 12 ; i++) {
                if (i < chargedBars) {
                    if (i < 2) {
                        bars[i].getStyleClass().setAll("red-bar");
                    } else if (i < 9) {
                        bars[i].getStyleClass().setAll("orange-bar");
                    } else {
                        bars[i].getStyleClass().setAll("green-bar");
                    }
                } else {
                    bars[i].getStyleClass().setAll("gray-bar");
                }
            }
        }
    }

    public double getValue() { return model.getValue(); }
    public void setValue(final double VALUE) { model.setValue(VALUE); }

    private Region createBar(final double HEIGHT, final String STYLE_CLASS) {
        Region region = new Region();
        region.setPrefSize(20, HEIGHT);
        region.getStyleClass().setAll(STYLE_CLASS);
        return region;
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        if (aspectRatio * width > height) {
            width = 1 / (aspectRatio / height);
        } else if (1 / (aspectRatio / height) > width) {
            height = aspectRatio * width;
        }

        if (width > 0 && height > 0) {
            pane.setMaxSize(width, height);
            pane.setPrefSize(width, height);
            pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);
            pane.setSpacing(width * 0.01960784);
            for (int i = 0 ; i < 12 ; i++) {
                bars[i].setPrefSize(0.3030303 * height, (0.3030303 * height + i * 0.06060606 * height));
            }
        }
    }
}
