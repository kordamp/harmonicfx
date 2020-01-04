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
package eu.hansolo.fx.fitnessgauge;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.ScaleDirection;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.tools.ConicalGradient;
import javafx.geometry.VPos;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


/**
 * User: hansolo
 * Date: 04.04.16
 * Time: 10:45
 */
public class FitnessGauge extends Region {
    private static final double PREFERRED_WIDTH      = 250;
    private static final double PREFERRED_HEIGHT     = 250;
    private static final double MINIMUM_WIDTH        = 50;
    private static final double MINIMUM_HEIGHT       = 50;
    private static final double MAXIMUM_WIDTH        = 1024;
    private static final double MAXIMUM_HEIGHT       = 1024;
    private static final Color  DEFAULT_OUTER_COLOR  = Color.rgb(237, 22, 72);
    private static final Color  DEFAULT_MIDDLE_COLOR = Color.rgb(123, 238, 0);
    private static final Color  DEFAULT_INNER_COLOR  = Color.rgb(0, 212, 216);
    private double              size;
    private double              center;
    private Pane                pane;
    private Gauge               outerGauge;
    private Gauge               middleGauge;
    private Gauge               innerGauge;
    private Circle              outerDot;
    private Circle              fakeOuterDot;
    private Arc                 outerArc;
    private Circle              outerCircle;
    private Circle              middleDot;
    private Circle              fakeMiddleDot;
    private Arc                 middleArc;
    private Circle              middleCircle;
    private Circle              innerDot;
    private Circle              fakeInnerDot;
    private Arc                 innerArc;
    private Circle              innerCircle;
    private Text                outerText;
    private Text                middleText;
    private Text                innerText;
    private double              outerAngleStep;
    private double              middleAngleStep;
    private double              innerAngleStep;
    private DropShadow          shadow;
    private DropShadow          textShadow;
    private ConicalGradient     outerGradient;
    private ConicalGradient     middleGradient;
    private ConicalGradient     innerGradient;


    // ******************** Constructors **************************************
    public FitnessGauge() { this(100, 100, 100); }
    public FitnessGauge(final double MAX_OUTER_VALUE, final double MAX_MIDDLE_VALUE, final double MAX_INNER_VALUE) {
        getStyleClass().add("fitness-gauge");
        center = PREFERRED_WIDTH * 0.5;
        init();
        initGraphics(MAX_OUTER_VALUE, MAX_MIDDLE_VALUE, MAX_INNER_VALUE);
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

    private void initGraphics(final double MAX_OUTER_VALUE, final double MAX_MIDDLE_VALUE, final double MAX_INNER_VALUE) {
        shadow      = new DropShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.45), 0.01 * PREFERRED_WIDTH, 0, 0.01 * PREFERRED_WIDTH, 0);
        textShadow  = new DropShadow(BlurType.TWO_PASS_BOX, Color.BLACK, 1, 1, 0, 0);

        outerGauge  = createGauge(MAX_OUTER_VALUE, DEFAULT_OUTER_COLOR);
        middleGauge = createGauge(MAX_MIDDLE_VALUE, DEFAULT_MIDDLE_COLOR);
        innerGauge  = createGauge(MAX_INNER_VALUE, DEFAULT_INNER_COLOR);

        outerCircle = new Circle();
        outerCircle.setFill(null);

        middleCircle = new Circle();
        middleCircle.setFill(null);

        innerCircle = new Circle();
        innerCircle.setFill(null);

        outerArc = new Arc(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5, PREFERRED_WIDTH * 0.96, PREFERRED_WIDTH * 0.48, 90, 0);
        outerArc.setStrokeWidth(PREFERRED_WIDTH * 0.008);
        outerArc.setStrokeType(StrokeType.CENTERED);
        outerArc.setStrokeLineCap(StrokeLineCap.ROUND);
        outerArc.setFill(null);

        middleArc = new Arc(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5, PREFERRED_WIDTH * 0.96, PREFERRED_WIDTH * 0.48, 90, 0);
        middleArc.setStrokeWidth(PREFERRED_WIDTH * 0.008);
        middleArc.setStrokeType(StrokeType.CENTERED);
        middleArc.setStrokeLineCap(StrokeLineCap.ROUND);
        middleArc.setFill(null);

        innerArc = new Arc(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5, PREFERRED_WIDTH * 0.96, PREFERRED_WIDTH * 0.48, 90, 0);
        innerArc.setStrokeWidth(PREFERRED_WIDTH * 0.008);
        innerArc.setStrokeType(StrokeType.CENTERED);
        innerArc.setStrokeLineCap(StrokeLineCap.ROUND);
        innerArc.setFill(null);

        fakeOuterDot = new Circle();
        fakeOuterDot.setStroke(null);

        fakeMiddleDot = new Circle();
        fakeMiddleDot.setStroke(null);

        fakeInnerDot = new Circle();
        fakeInnerDot.setStroke(null);

        outerDot = new Circle();
        outerDot.setStroke(null);
        outerDot.setVisible(false);
        outerDot.setEffect(shadow);

        middleDot = new Circle();
        middleDot.setStroke(null);
        middleDot.setVisible(false);
        middleDot.setEffect(shadow);

        innerDot = new Circle();
        innerDot.setStroke(null);
        innerDot.setVisible(false);
        innerDot.setEffect(shadow);

        outerText = new Text("");
        outerText.setTextOrigin(VPos.CENTER);
        outerText.setEffect(textShadow);

        middleText = new Text("");
        middleText.setTextOrigin(VPos.CENTER);
        middleText.setEffect(textShadow);

        innerText = new Text("");
        innerText.setTextOrigin(VPos.CENTER);
        innerText.setEffect(textShadow);

        pane = new Pane(outerCircle, middleCircle, innerCircle,
                        outerArc, middleArc, innerArc,
                        fakeOuterDot, fakeMiddleDot, fakeInnerDot,
                        outerDot, middleDot, innerDot,
                        outerText, middleText, innerText);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        outerGauge.currentValueProperty().addListener(o -> redraw());
        middleGauge.currentValueProperty().addListener(o -> redraw());
        innerGauge.currentValueProperty().addListener(o -> redraw());
    }


    // ******************** Methods *******************************************
    private Gauge createGauge(final double TARGET, final Color COLOR) {
        return GaugeBuilder.create()
                          .animated(true)
                          .animationDuration(1000)
                          .minValue(0)
                          .maxValue(TARGET)
                          .gradientBarEnabled(true)
                          .gradientBarStops(new Stop(0.0, COLOR),
                                            new Stop(0.01, COLOR),
                                            new Stop(0.75, COLOR.deriveColor(-10, 1, 1, 1)),
                                            new Stop(1.0, COLOR.deriveColor(-20, 1, 1, 1)))
                          .barColor(COLOR)
                          .build();
    }

    public double getOuterMaxValue() { return outerGauge.getMaxValue(); }
    public void setOuterMaxValue(final double VALUE) { outerGauge.setMaxValue(clamp(0d, Double.MAX_VALUE, VALUE)); }

    public double getOuterValue() { return outerGauge.getCurrentValue(); }
    public void setOuterValue(final double VALUE) { outerGauge.setValue(VALUE); }

    public double getMiddleMaxValue() { return middleGauge.getMaxValue(); }
    public void setMiddleMaxValue(final double VALUE) { middleGauge.setMaxValue(clamp(0d, Double.MAX_VALUE, VALUE)); }

    public double getMiddleValue() { return middleGauge.getCurrentValue(); }
    public void setMiddleValue(final double VALUE) { middleGauge.setValue(VALUE); }

    public double getInnerMaxValue() { return innerGauge.getMaxValue(); }
    public void setInnerMaxValue(final double VALUE) { innerGauge.setMaxValue(clamp(0d, Double.MAX_VALUE, VALUE)); }

    public double getInnerValue() { return innerGauge.getCurrentValue(); }
    public void setInnerValue(final double VALUE) { innerGauge.setValue(VALUE); }

    public Color getOuterColor() { return outerGauge.getBarColor(); }
    public void setOuterColor(final Color COLOR) {
        outerGauge.setBarColor(COLOR);
        createGradient(outerGauge, COLOR);
        resize();
    }

    public Color getMiddleColor() { return middleGauge.getBarColor(); }
    public void setMiddleColor(final Color COLOR) {
        middleGauge.setBarColor(COLOR);
        createGradient(middleGauge, COLOR);
        resize();
    }

    public Color getInnerColor() { return innerGauge.getBarColor(); }
    public void setInnerColor(final Color COLOR) {
        innerGauge.setBarColor(COLOR);
        createGradient(innerGauge, COLOR);
        resize();
    }

    public String getOuterText() { return outerText.getText(); }
    public void setOuterText(final String TEXT) { outerText.setText(TEXT.toUpperCase()); }

    public String getMiddleText() { return middleText.getText(); }
    public void setMiddleText(final String TEXT) { middleText.setText(TEXT.toUpperCase()); }

    public String getInnerText() { return innerText.getText(); }
    public void setInnerText(final String TEXT) { innerText.setText(TEXT.toUpperCase()); }

    public boolean isOuterTextVisible() { return outerText.isVisible(); }
    public void setOuterTextVisible(final boolean VISIBLE) { outerText.setVisible(VISIBLE); }

    public boolean isMiddleTextVisible() { return middleText.isVisible(); }
    public void setMiddleTextVisible(final boolean VISIBLE) { middleText.setVisible(VISIBLE); }

    public boolean isInnerTextVisible() { return innerText.isVisible(); }
    public void setInnerTextVisible(final boolean VISIBLE) { innerText.setVisible(VISIBLE); }

    private void createGradient(final Gauge GAUGE, final Color COLOR) {
        GAUGE.setGradientBarStops(new Stop(0.0, COLOR),
                                  new Stop(0.01, COLOR),
                                  new Stop(0.75, COLOR.deriveColor(-10, 1, 1, 1)),
                                  new Stop(1.0, COLOR.deriveColor(-20, 1, 1, 1)));
    }

    private static final <T extends Number> T clamp(final T MIN, final T MAX, final T VALUE) {
        if (VALUE.doubleValue() < MIN.doubleValue()) return MIN;
        if (VALUE.doubleValue() > MAX.doubleValue()) return MAX;
        return VALUE;
    }


    // ******************** Resizing ******************************************
    private void resize() {
        double width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        double height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size          = width < height ? width : height;

        if (width > 0 && height > 0) {
            // Use for square controls where width == height
            pane.setMaxSize(size, size);
            pane.setPrefSize(size, size);
            pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

            center = size * 0.5;

            outerCircle.setCenterX(center);
            outerCircle.setCenterY(center);
            outerCircle.setRadius(size * 0.44);
            outerCircle.setStrokeWidth(size * 0.11);

            middleCircle.setCenterX(center);
            middleCircle.setCenterY(center);
            middleCircle.setRadius(size * 0.32);
            middleCircle.setStrokeWidth(size * 0.11);

            innerCircle.setCenterX(center);
            innerCircle.setCenterY(center);
            innerCircle.setRadius(size * 0.20);
            innerCircle.setStrokeWidth(size * 0.11);

            outerArc.setCenterX(center);
            outerArc.setCenterY(center);
            outerArc.setRadiusX(size * 0.44);
            outerArc.setRadiusY(size * 0.44);
            outerArc.setStrokeWidth(size * 0.11);

            middleArc.setCenterX(center);
            middleArc.setCenterY(center);
            middleArc.setRadiusX(size * 0.32);
            middleArc.setRadiusY(size * 0.32);
            middleArc.setStrokeWidth(size * 0.11);

            innerArc.setCenterX(center);
            innerArc.setCenterY(center);
            innerArc.setRadiusX(size * 0.20);
            innerArc.setRadiusY(size * 0.20);
            innerArc.setStrokeWidth(size * 0.11);

            shadow.setRadius(0.03 * size);
            shadow.setOffsetX(0.03 * size);

            outerCircle.setStroke(Color.color(getOuterColor().getRed(), getOuterColor().getGreen(), getOuterColor().getBlue(), 0.13));
            middleCircle.setStroke(Color.color(getMiddleColor().getRed(), getMiddleColor().getGreen(), getMiddleColor().getBlue(), 0.13));
            innerCircle.setStroke(Color.color(getInnerColor().getRed(), getInnerColor().getGreen(), getInnerColor().getBlue(), 0.13));

            Rectangle bounds = new Rectangle(0, 0, size, size);

            outerGradient = new ConicalGradient(center, center, ScaleDirection.CLOCKWISE, outerGauge.getGradientBarStops());
            outerArc.setStroke(outerGradient.getImagePattern(bounds));
            outerArc.setLength(outerGauge.getCurrentValue() * outerAngleStep);

            middleGradient = new ConicalGradient(center, center, ScaleDirection.CLOCKWISE, middleGauge.getGradientBarStops());
            middleArc.setStroke(middleGradient.getImagePattern(bounds));
            middleArc.setLength(middleGauge.getCurrentValue() * middleAngleStep);

            innerGradient = new ConicalGradient(center, center, ScaleDirection.CLOCKWISE, innerGauge.getGradientBarStops());
            innerArc.setStroke(innerGradient.getImagePattern(bounds));
            innerArc.setLength(innerGauge.getCurrentValue() * innerAngleStep);

            fakeOuterDot.setRadius(size * 0.055);
            fakeOuterDot.setFill(outerGauge.getGradientBarStops().get(0).getColor());
            fakeMiddleDot.setRadius(size * 0.055);
            fakeMiddleDot.setFill(middleGauge.getGradientBarStops().get(0).getColor());
            fakeInnerDot.setRadius(size * 0.055);
            fakeInnerDot.setFill(innerGauge.getGradientBarStops().get(0).getColor());

            outerDot.setRadius(size * 0.055);
            outerDot.setFill(outerGauge.getGradientBarStops().get(3).getColor());
            middleDot.setRadius(size * 0.055);
            middleDot.setFill(middleGauge.getGradientBarStops().get(3).getColor());
            innerDot.setRadius(size * 0.055);
            innerDot.setFill(innerGauge.getGradientBarStops().get(3).getColor());

            fakeOuterDot.setCenterX(center + outerArc.getRadiusX() * Math.sin(Math.toRadians(180 - outerGauge.getCurrentValue() * outerAngleStep)));
            fakeOuterDot.setCenterY(center + outerArc.getRadiusY() * Math.cos(Math.toRadians(180 - outerGauge.getCurrentValue() * outerAngleStep)));
            fakeMiddleDot.setCenterX(center + middleArc.getRadiusX() * Math.sin(Math.toRadians(180 - middleGauge.getCurrentValue() * middleAngleStep)));
            fakeMiddleDot.setCenterY(center + middleArc.getRadiusY() * Math.cos(Math.toRadians(180 - middleGauge.getCurrentValue() * middleAngleStep)));
            fakeInnerDot.setCenterX(center + innerArc.getRadiusX() * Math.sin(Math.toRadians(180 - innerGauge.getCurrentValue() * innerAngleStep)));
            fakeInnerDot.setCenterY(center + innerArc.getRadiusY() * Math.cos(Math.toRadians(180 - innerGauge.getCurrentValue() * innerAngleStep)));

            outerDot.setCenterX(center + outerArc.getRadiusX() * Math.sin(Math.toRadians(180 - outerGauge.getCurrentValue() * outerAngleStep)));
            outerDot.setCenterY(center + outerArc.getRadiusY() * Math.cos(Math.toRadians(180 - outerGauge.getCurrentValue() * outerAngleStep)));
            middleDot.setCenterX(center + middleArc.getRadiusX() * Math.sin(Math.toRadians(180 - middleGauge.getCurrentValue() * middleAngleStep)));
            middleDot.setCenterY(center + middleArc.getRadiusY() * Math.cos(Math.toRadians(180 - middleGauge.getCurrentValue() * middleAngleStep)));
            innerDot.setCenterX(center + innerArc.getRadiusX() * Math.sin(Math.toRadians(180 - innerGauge.getCurrentValue() * innerAngleStep)));
            innerDot.setCenterY(center + innerArc.getRadiusY() * Math.cos(Math.toRadians(180 - innerGauge.getCurrentValue() * innerAngleStep)));

            outerText.setFill(outerGauge.getBarColor());
            outerText.setFont(Font.font(size * 0.06));
            outerText.relocate(size * 0.4 - outerText.getLayoutBounds().getWidth(), size * 0.021);

            middleText.setFill(middleGauge.getBarColor());
            middleText.setFont(Font.font(size * 0.06));
            middleText.relocate(size * 0.4 - middleText.getLayoutBounds().getWidth(), size * 0.14);

            innerText.setFill(innerGauge.getBarColor());
            innerText.setFont(Font.font(size * 0.06));
            innerText.relocate(size * 0.4 - innerText.getLayoutBounds().getWidth(), size * 0.26);

            redraw();
        }
    }

    private void redraw() {
        outerAngleStep      = -360d / outerGauge.getRange();
        middleAngleStep     = -360d / middleGauge.getRange();
        innerAngleStep      = -360d / innerGauge.getRange();

        double outerValue   = outerGauge.getCurrentValue();
        double middleValue  = middleGauge.getCurrentValue();
        double innerValue   = innerGauge.getCurrentValue();

        double outerAngle   = outerValue * outerAngleStep;
        double middleAngle  = middleValue * middleAngleStep;
        double innerAngle   = innerValue * innerAngleStep;

        double outerRotate  = outerAngle  < -360 ? outerAngle  + 360 : 0;
        double middleRotate = middleAngle < -360 ? middleAngle + 360 : 0;
        double innerRotate  = innerAngle  < -360 ? innerAngle  + 360 : 0;

        outerArc.setRotate(-outerRotate);
        middleArc.setRotate(-middleRotate);
        innerArc.setRotate(-innerRotate);

        outerArc.setLength(clamp(-360d, 0d, outerAngle));
        middleArc.setLength(clamp(-360d, 0d, middleAngle));
        innerArc.setLength(clamp(-360d, 0d, innerAngle));

        outerDot.setRotate(-outerAngle);
        middleDot.setRotate(-middleAngle);
        innerDot.setRotate(-innerAngle);

        outerDot.setVisible(outerAngle   < -325 ? true : false);
        middleDot.setVisible(middleAngle < -325 ? true : false);
        innerDot.setVisible(innerAngle   < -325 ? true : false);

        if (outerAngle < -360) {
            fakeOuterDot.setCenterX(center + outerArc.getRadiusX() * Math.sin(Math.toRadians(180 + outerAngle)));
            fakeOuterDot.setCenterY(center + outerArc.getRadiusY() * Math.cos(Math.toRadians(180 + outerAngle)));
        } else {
            fakeOuterDot.setCenterX(center + outerArc.getRadiusX() * Math.sin(Math.toRadians(180)));
            fakeOuterDot.setCenterY(center + outerArc.getRadiusY() * Math.cos(Math.toRadians(180)));
        }
        if (middleAngle < -360) {
            fakeMiddleDot.setCenterX(center + middleArc.getRadiusX() * Math.sin(Math.toRadians(180 + middleAngle)));
            fakeMiddleDot.setCenterY(center + middleArc.getRadiusY() * Math.cos(Math.toRadians(180 + middleAngle)));
        } else {
            fakeMiddleDot.setCenterX(center + middleArc.getRadiusX() * Math.sin(Math.toRadians(180)));
            fakeMiddleDot.setCenterY(center + middleArc.getRadiusY() * Math.cos(Math.toRadians(180)));
        }
        if (innerAngle < -360) {
            fakeInnerDot.setCenterX(center + innerArc.getRadiusX() * Math.sin(Math.toRadians(180 + innerAngle)));
            fakeInnerDot.setCenterY(center + innerArc.getRadiusY() * Math.cos(Math.toRadians(180 + innerAngle)));
        } else {
            fakeInnerDot.setCenterX(center + innerArc.getRadiusX() * Math.sin(Math.toRadians(180)));
            fakeInnerDot.setCenterY(center + innerArc.getRadiusY() * Math.cos(Math.toRadians(180)));
        }

        outerDot.setCenterX(center + outerArc.getRadiusX() * Math.sin(Math.toRadians(180 + outerAngle)));
        outerDot.setCenterY(center + outerArc.getRadiusY() * Math.cos(Math.toRadians(180 + outerAngle)));
        middleDot.setCenterX(center + middleArc.getRadiusX() * Math.sin(Math.toRadians(180 + middleAngle)));
        middleDot.setCenterY(center + middleArc.getRadiusY() * Math.cos(Math.toRadians(180 + middleAngle)));
        innerDot.setCenterX(center + innerArc.getRadiusX() * Math.sin(Math.toRadians(180 + innerAngle)));
        innerDot.setCenterY(center + innerArc.getRadiusY() * Math.cos(Math.toRadians(180 + innerAngle)));
    }
}
