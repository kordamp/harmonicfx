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
package eu.hansolo.fx.gradientgauge;

import eu.hansolo.medusa.Fonts;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.tools.ConicalGradient;
import eu.hansolo.medusa.tools.Helper;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
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
import javafx.scene.paint.Stop;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;

import java.util.Locale;


/**
 * User: hansolo
 * Date: 01.06.16
 * Time: 10:54
 */
public class GradientGauge extends Region {
    private static final double     PREFERRED_WIDTH  = 320;
    private static final double     PREFERRED_HEIGHT = 320;
    private static final double     MINIMUM_WIDTH    = 50;
    private static final double     MINIMUM_HEIGHT   = 50;
    private static final double     MAXIMUM_WIDTH    = 1024;
    private static final double     MAXIMUM_HEIGHT   = 1024;
    private Gauge           model;
    private double          size;
    private double          center;
    private Circle          innerCircle;
    private Arc             backgroundRing;
    private Arc             bar;
    private Text            valueText;
    private Pane            pane;
    private Paint           backgroundPaint;
    private Paint           borderPaint;
    private double          borderWidth;
    private ConicalGradient gradient;
    private DropShadow      dropShadow;
    private Color           barColor;


    // ******************** Constructors **************************************
    public GradientGauge() {
        this(0, 0, 100, "");
    }
    public GradientGauge(final String UNIT) {
        this(0, 0, 100, UNIT);
    }
    public GradientGauge(final double VALUE, final double MIN_VALUE, final double MAX_VALUE, final String UNIT) {
        model = GaugeBuilder.create()
                            .animated(true)
                            .animationDuration(2000)
                            .angleRange(320)
                            .startAngle(340)
                            .minValue(MIN_VALUE)
                            .maxValue(MAX_VALUE)
                            .barColor(Color.CYAN)
                            .valueColor(Color.WHITE)
                            .value(VALUE)
                            .shadowsEnabled(false)
                            .build();

        backgroundPaint = Color.rgb(46, 75, 76);
        borderPaint     = Color.TRANSPARENT;
        borderWidth     = 0d;
        barColor        = model.getBarColor();

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
        innerCircle = new Circle(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5, PREFERRED_WIDTH * 0.375);
        innerCircle.setFill(model.getBarColor().deriveColor(0, 1, 0.08, 1));

        valueText = new Text(String.format(Locale.US, "%.0f", model.getValue()));
        valueText.setFill(model.getValueColor());
        valueText.setFont(Fonts.robotoBold(PREFERRED_WIDTH * 0.20625));
        valueText.setTextOrigin(VPos.CENTER);
        valueText.relocate(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.46875);

        backgroundRing = new Arc(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5,
                      PREFERRED_WIDTH * 0.43125, PREFERRED_HEIGHT * 0.43125,
                      -70, 320);
        backgroundRing.setFill(null);
        backgroundRing.setStroke(model.getBarColor().deriveColor(0, 1, 0.15, 1));
        backgroundRing.setStrokeLineCap(StrokeLineCap.BUTT);
        backgroundRing.setStrokeWidth(PREFERRED_WIDTH * 0.1375);

        gradient = new ConicalGradient();

        dropShadow = new DropShadow(BlurType.TWO_PASS_BOX, model.getBarColor(), 10, 0, 0, 0);

        bar = new Arc(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5,
                      PREFERRED_WIDTH * 0.43125, PREFERRED_HEIGHT * 0.43125,
                      -110, -model.getAngleStep() * model.getValue());
        bar.setFill(null);
        bar.setStroke(gradient.getImagePattern(new Rectangle(0, 0, PREFERRED_WIDTH, PREFERRED_HEIGHT)));
        bar.setStrokeWidth(PREFERRED_WIDTH * 0.1375);
        bar.setStrokeLineCap(StrokeLineCap.BUTT);
        bar.setEffect(model.isShadowsEnabled() ? dropShadow : null);

        pane = new Pane(innerCircle, valueText, backgroundRing, bar);
        pane.setBackground(new Background(new BackgroundFill(backgroundPaint, new CornerRadii(1024), Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(borderPaint, BorderStrokeStyle.SOLID, new CornerRadii(1024), new BorderWidths(borderWidth))));

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        model.currentValueProperty().addListener(o -> handleControlPropertyChanged("VALUE"));
        model.barColorProperty().addListener(o -> resize());
        model.valueColorProperty().addListener(o -> valueText.setFill(model.getValueColor()));
        model.shadowsEnabledProperty().addListener(o -> bar.setEffect(model.isShadowsEnabled() ? dropShadow : null));
    }


    // ******************** Methods *******************************************
    private void handleControlPropertyChanged(final String PROPERTY) {
        if ("VALUE".equals(PROPERTY)) {
            if (model.isAnimated() && model.getAnimationDuration() < 2000) model.setAnimationDuration(2000);
            double fraction = model.getCurrentValue() / model.getRange();
            double r        = barColor.getRed();
            double g        = barColor.getGreen();
            double b        = barColor.getBlue();

            gradient.setStops(0.55555556, new Stop(0.00, Color.color(r, g, b, 0)),
                              new Stop(fraction, Color.color(r, g, b)),
                              new Stop(1.0, Color.color(r, g, b, 0)));

            bar.setLength(-model.getAngleStep() * model.getCurrentValue());
            bar.setStroke(gradient.getImagePattern(pane.getBoundsInLocal()));

            valueText.setText(String.format(Locale.US, "%.0f", model.getCurrentValue()));
            Helper.adjustTextSize(valueText, size * 0.3, size * 0.3);
            valueText.relocate((size - valueText.getLayoutBounds().getWidth()) * 0.5, (size - valueText.getLayoutBounds().getHeight()) * 0.5);
        }
    }

    public Gauge getModel() { return model; }

    public Color getBarColor() { return model.getBarColor(); }
    public void setBarColor(final Color COLOR) { model.setBarColor(COLOR); }

    public Color getValueColor() { return model.getValueColor(); }
    public void setValueColor(final Color COLOR) { model.setValueColor(COLOR); }

    public boolean isShadowsEnabled() { return model.isShadowsEnabled(); }
    public void setShadowsEnabled(final boolean ENABLED) { model.setShadowsEnabled(ENABLED); }


    // ******************** Resizing ******************************************
    private void resize() {
        double width = getWidth() - getInsets().getLeft() - getInsets().getRight();
        double height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size = width < height ? width : height;

        if (width > 0 && height > 0) {
            pane.setMaxSize(size, size);
            pane.setPrefSize(size, size);
            pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

            center = size * 0.5;

            barColor = model.getBarColor();

            innerCircle.setCenterX(center);
            innerCircle.setCenterY(center);
            innerCircle.setRadius(size * 0.455);
            innerCircle.setRadius(size * 0.455);

            valueText.setFont(Fonts.robotoCondensedBold(size * 0.3));

            backgroundRing.setCenterX(center);
            backgroundRing.setCenterY(center);
            backgroundRing.setRadiusX(size * 0.375);
            backgroundRing.setRadiusY(size * 0.375);
            backgroundRing.setStrokeWidth(size * 0.16);

            dropShadow = new DropShadow(BlurType.TWO_PASS_BOX, model.getBarColor(), 10, 0, 0, 0);

            dropShadow.setRadius(size * 0.03125);

            bar.setCenterX(center);
            bar.setCenterY(center);
            bar.setRadiusX(size * 0.375);
            bar.setRadiusY(size * 0.375);
            bar.setStrokeWidth(size * 0.16);
            bar.setStroke(gradient.getImagePattern(pane.getBoundsInLocal()));

            redraw();
        }
    }

    private void redraw() {
        backgroundPaint = model.getBarColor().deriveColor(0, 0.4, 0.3, 1);

        dropShadow.setColor(model.getBarColor());

        innerCircle.setFill(model.getBarColor().deriveColor(0, 1, 0.08, 1));
        valueText.setFill(model.getValueColor());
        backgroundRing.setStroke(model.getBarColor().deriveColor(0, 1, 0.15, 1));

        pane.setBackground(new Background(new BackgroundFill(backgroundPaint, new CornerRadii(1024), Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(borderPaint, BorderStrokeStyle.SOLID, new CornerRadii(1024), new BorderWidths(borderWidth / PREFERRED_WIDTH * size))));

        valueText.setText(String.format(Locale.US, "%.0f", model.getCurrentValue()));
        valueText.relocate((size - valueText.getLayoutBounds().getWidth()) * 0.5, (size - valueText.getLayoutBounds().getHeight()) * 0.5);

        bar.setLength(-model.getAngleStep() * model.getCurrentValue());
    }
}
