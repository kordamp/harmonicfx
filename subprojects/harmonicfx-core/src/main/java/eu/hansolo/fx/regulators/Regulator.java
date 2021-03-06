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
package eu.hansolo.fx.regulators;

import eu.hansolo.fx.fonts.Fonts;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.CacheHint;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Locale;


public class Regulator extends Region {
    private static final double         PREFERRED_WIDTH  = 250;
    private static final double         PREFERRED_HEIGHT = 250;
    private static final double         MINIMUM_WIDTH    = 50;
    private static final double         MINIMUM_HEIGHT   = 50;
    private static final double         MAXIMUM_WIDTH    = 1024;
    private static final double         MAXIMUM_HEIGHT   = 1024;
    private static final double         BAR_START_ANGLE  = -130;
    private static final double         ANGLE_RANGE      = 280;
    private final        RegulatorEvent TARGET_SET_EVENT = new RegulatorEvent(RegulatorEvent.TARGET_SET);
    private double                      size;
    private Canvas                      barCanvas;
    private GraphicsContext             barCtx;
    private Shape                       ring;
    private Circle                      mainCircle;
    private Text                        text;
    private Circle                      indicator;
    private Region                      symbol;
    private StackPane                   iconPane;
    private FontIcon                    icon;
    private Pane                        pane;
    private DropShadow                  dropShadow;
    private InnerShadow                 highlight;
    private InnerShadow                 innerShadow;
    private Rotate                      indicatorRotate;
    private double                      scaleFactor;
    private DoubleProperty              minValue;
    private DoubleProperty              maxValue;
    private DoubleProperty              targetValue;
    private IntegerProperty             decimals;
    private StringProperty              unit;
    private ObjectProperty<Color>       symbolColor;
    private ObjectProperty<Color>       iconColor;
    private ObjectProperty<Color>       textColor;
    private ObjectProperty<Color>       barColor;
    private ObjectProperty<Color>       color;
    private String                      formatString;
    private double                      angleStep;


    // ******************** Constructors **************************************
    public Regulator() {
        getStylesheets().add(Regulator.class.getResource("regulator.css").toExternalForm());
        scaleFactor  = 1d;
        minValue     = new DoublePropertyBase(0) {
            @Override public void set(final double VALUE) {
                super.set(clamp(-Double.MAX_VALUE, maxValue.get(), VALUE));
                angleStep = ANGLE_RANGE / (maxValue.get() - minValue.get());
            }
            @Override public Object getBean() { return Regulator.this; }
            @Override public String getName() { return "minValue"; }
        };
        maxValue     = new DoublePropertyBase(100) {
            @Override public void set(final double VALUE) {
                super.set(clamp(minValue.get(), Double.MAX_VALUE, VALUE));
                angleStep = ANGLE_RANGE / (maxValue.get() - minValue.get());
            }
            @Override public Object getBean() { return Regulator.this; }
            @Override public String getName() { return "maxValue"; }
        };
        targetValue = new DoublePropertyBase(0) {
            @Override public void set(final double VALUE) { super.set(clamp(minValue.get(), maxValue.get(), VALUE)); }
            @Override public Object getBean() { return Regulator.this; }
            @Override public String getName() { return "targetValue"; }
        };
        decimals     = new IntegerPropertyBase(0) {
            @Override public void set(final int VALUE) {
                super.set(clamp(0, 2, VALUE));
                formatString = new StringBuilder("%.").append(Integer.toString(decimals.get())).append("f").append(getUnit()).toString();
                redraw();
            }
            @Override public Object getBean() { return Regulator.this; }
            @Override public String getName() { return "decimals"; }
        };
        unit         = new StringPropertyBase("") {
            @Override public void set(final String VALUE) {
                super.set(VALUE.equals("%") ? "%%" : VALUE);
                formatString = new StringBuilder("%.").append(Integer.toString(decimals.get())).append("f").append(get()).toString();
                redraw();
            }
            @Override public Object getBean() { return Regulator.this; }
            @Override public String getName() { return "unit"; }
        };
        symbolColor  = new ObjectPropertyBase<Color>(Color.TRANSPARENT) {
            @Override protected void invalidated() {
                set(null == get() ? Color.WHITE : get());
                redraw();
            }
            @Override public Object getBean() { return Regulator.this; }
            @Override public String getName() { return "symbolColor"; }
        };
        iconColor    = new ObjectPropertyBase<Color>(Color.TRANSPARENT) {
            @Override protected void invalidated() {
                set(null == get() ? Color.WHITE : get());
                redraw();
            }
            @Override public Object getBean() { return Regulator.this; }
            @Override public String getName() { return "iconColor"; }
        };
        textColor    = new ObjectPropertyBase<Color>(Color.WHITE) {
            @Override protected void invalidated() {
                set(null == get() ? Color.WHITE : get());
                redraw();
            }
            @Override public Object getBean() { return Regulator.this; }
            @Override public String getName() { return "textColor"; }
        };
        barColor     = new ObjectPropertyBase<Color>(Color.CYAN) {
            @Override protected void invalidated() {
                super.set(null == get() ? Color.CYAN : get());
                redraw();
            }
            @Override public Object getBean() { return Regulator.this; }
            @Override public String getName() { return "barColor"; }
        };
        color        = new ObjectPropertyBase<Color>(Color.rgb(66,71,79)) {
            @Override protected void invalidated() {
                super.set(null == get() ? Color.rgb(66,71,79) : get());
                redraw();
            }
            @Override public Object getBean() { return Regulator.this; }
            @Override public String getName() { return "color"; }
        };
        formatString = new StringBuilder("%.").append(Integer.toString(decimals.get())).append("f").append(unit.get()).toString();
        angleStep    = ANGLE_RANGE / (maxValue.get() - minValue.get());
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
        dropShadow  = new DropShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.65), PREFERRED_WIDTH * 0.016, 0.0, 0, PREFERRED_WIDTH * 0.028);
        highlight   = new InnerShadow(BlurType.TWO_PASS_BOX, Color.rgb(255, 255, 255, 0.2), PREFERRED_WIDTH * 0.008, 0.0, 0, PREFERRED_WIDTH * 0.008);
        innerShadow = new InnerShadow(BlurType.TWO_PASS_BOX, Color.rgb(0, 0, 0, 0.2), PREFERRED_WIDTH * 0.008, 0.0, 0, -PREFERRED_WIDTH * 0.008);
        highlight.setInput(innerShadow);
        dropShadow.setInput(highlight);

        barCanvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        barCtx    = barCanvas.getGraphicsContext2D();
        barCtx.setLineCap(StrokeLineCap.ROUND);
        barCtx.setStroke(barColor.get());

        double center = PREFERRED_WIDTH * 0.5;
        ring = Shape.subtract(new Circle(center, center, PREFERRED_WIDTH * 0.42),
                              new Circle(center, center, PREFERRED_WIDTH * 0.3));
        ring.setFill(color.get());
        ring.setEffect(dropShadow);

        mainCircle = new Circle();
        mainCircle.setFill(color.get().darker().darker());

        text = new Text(String.format(Locale.US, formatString, getTargetValue()));
        text.setFill(Color.WHITE);
        text.setTextOrigin(VPos.CENTER);

        indicatorRotate = new Rotate(-ANGLE_RANGE *  0.5, center, center);

        indicator = new Circle();
        indicator.setFill(color.get().darker());
        indicator.setStroke(color.get().darker().darker());
        indicator.setMouseTransparent(true);
        indicator.getTransforms().add(indicatorRotate);

        symbol = new Region();
        symbol.getStyleClass().setAll("symbol");
        symbol.setCacheHint(CacheHint.SPEED);

        icon = new FontIcon();
        icon.setTextOrigin(VPos.CENTER);

        iconPane = new StackPane(symbol, icon);

        pane = new Pane(barCanvas, ring, mainCircle, text, indicator, iconPane);
        pane.setPrefSize(PREFERRED_HEIGHT, PREFERRED_HEIGHT);
        pane.setBackground(new Background(new BackgroundFill(color.get().darker(), new CornerRadii(1024), Insets.EMPTY)));
        pane.setEffect(highlight);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        disabledProperty().addListener(o -> setOpacity(isDisabled() ? 0.4 : 1.0));
        targetValueProperty().addListener(o -> rotate(targetValue.get()));
        ring.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> { if (isDisabled()) return; touchRotate(e.getSceneX(), e.getSceneY()); });
        ring.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> { if (isDisabled()) return; touchRotate(e.getSceneX(), e.getSceneY()); });
        ring.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> { if (isDisabled()) return; fireEvent(TARGET_SET_EVENT); });
    }


    // ******************** Methods *******************************************
    public double getMinValue() { return minValue.get(); }
    public void setMinValue(final double VALUE) { minValue.set(VALUE); }
    public DoubleProperty minValueProperty() { return minValue; }

    public double getMaxValue() { return maxValue.get(); }
    public void setMaxValue(final double VALUE) { maxValue.set(VALUE); }
    public DoubleProperty maxValueProperty() { return maxValue; }

    public double getTargetValue() { return targetValue.get(); }
    public void setTargetValue(final double VALUE) { targetValue.set(VALUE); }
    public DoubleProperty targetValueProperty() { return targetValue; }

    public int getDecimals() { return decimals.get(); }
    public void setDecimals(final int VALUE) { decimals.set(VALUE); }
    public IntegerProperty decimalsProperty() { return decimals; }

    public String getUnit()  { return unit.get(); }
    public void setUnit(final String UNIT) { unit.set(UNIT); }
    public StringProperty unitProperty() { return unit; }

    public Color getSymbolColor() { return symbolColor.get(); }
    public void setSymbolColor(final Color COLOR) { symbolColor.set(COLOR); }
    public ObjectProperty<Color> symbolColorProperty() { return symbolColor; }

    public Color getIconColor() { return iconColor.get(); }
    public void setIconColor(final Color COLOR) { iconColor.set(COLOR); }
    public ObjectProperty<Color> iconColorProperty() { return iconColor; }

    public Color getTextColor() { return textColor.get(); }
    public void setTextColor(final Color COLOR) { textColor.set(COLOR); }
    public ObjectProperty<Color> textColorProperty() { return textColor; }

    public Color getBarColor() { return barColor.get(); }
    public void setBarColor(final Color COLOR) { barColor.set(COLOR); }
    public ObjectProperty<Color> barColorProperty() { return barColor; }

    public Color getColor() { return color.get(); }
    public void setColor(final Color COLOR) { color.set(COLOR); }
    public ObjectProperty<Color> colorProperty() { return color; }

    public void setSymbolPath(final double SCALE_X, final double SCALE_Y, final String PATH) {
        if (PATH.isEmpty()) {
            symbol.setVisible(false);
        } else {
            symbol.setStyle(new StringBuilder().append("-fx-scale-x:").append(clamp(0d, 1d, SCALE_X)).append(";")
                                               .append("-fx-scale-y:").append(clamp(0d, 1d, SCALE_Y)).append(";")
                                               .append("-fx-shape:\"").append(PATH).append("\";")
                                               .toString());
            symbol.setVisible(true);
        }
        symbol.setCache(false);
        resize();
        symbol.setCache(true);
    }

    public void setIcon(final Ikon ICON) {
        icon.setIconCode(ICON);
        resize();
    }

    private <T extends Number> T clamp(final T MIN, final T MAX, final T VALUE) {
        if (VALUE.doubleValue() < MIN.doubleValue()) return MIN;
        if (VALUE.doubleValue() > MAX.doubleValue()) return MAX;
        return VALUE;
    }

    private void adjustTextSize(final Text TEXT, final double MAX_WIDTH, double fontSize) {
        final String FONT_NAME = TEXT.getFont().getName();
        while (TEXT.getLayoutBounds().getWidth() > MAX_WIDTH && fontSize > 0) {
            fontSize -= 0.005;
            TEXT.setFont(new Font(FONT_NAME, fontSize));
        }
    }

    private void touchRotate(final double X, final double Y) {
        Point2D p      = sceneToLocal(X, Y);
        double  deltaX = p.getX() - (pane.getLayoutX() + size * 0.5);
        double  deltaY = p.getY() - (pane.getLayoutY() + size * 0.5);
        double  radius = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
        double  nx     = deltaX / radius;
        double  ny     = deltaY / radius;
        double  theta  = Math.atan2(ny, nx);
        theta         = Double.compare(theta, 0.0) >= 0 ? Math.toDegrees(theta) : Math.toDegrees((theta)) + 360.0;
        double angle  = (theta + 230) % 360;
        if (angle > 320 && angle < 360) {
            angle = 0;
        } else if (angle <= 320 && angle > ANGLE_RANGE) {
            angle = ANGLE_RANGE;
        }
        setTargetValue(angle / angleStep + minValue.get());
    }


    // ******************** Resizing ******************************************
    private void rotate(final double VALUE) {
        drawBar(barCtx, VALUE);
        indicatorRotate.setAngle((VALUE - minValue.get()) * angleStep - ANGLE_RANGE * 0.5);
        text.setText(String.format(Locale.US, formatString, VALUE));
        adjustTextSize(text, size * 0.48, size * 0.216);
        text.setLayoutX((size - text.getLayoutBounds().getWidth()) * 0.5);
    }

    private void drawBar(final GraphicsContext CTX, final double VALUE) {
        CTX.clearRect(0, 0, size, size);
        double barXY          = size * 0.04;
        double barWH          = size * 0.92;
        double barAngleExtend = (VALUE - minValue.get()) * angleStep;
        CTX.save();
        CTX.strokeArc(barXY, barXY, barWH, barWH, BAR_START_ANGLE, -barAngleExtend, ArcType.OPEN);
        CTX.restore();
    }

    private void resize() {
        double width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        double height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            pane.setMaxSize(size, size);
            pane.setPrefSize(size, size);
            pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

            barCanvas.setWidth(size);
            barCanvas.setHeight(size);
            barCtx.setLineWidth(size * 0.04);
            drawBar(barCtx, targetValue.get());

            double shadowRadius = clamp(1d, 2d, size * 0.004);
            dropShadow.setRadius(shadowRadius);
            dropShadow.setOffsetY(shadowRadius);
            highlight.setRadius(shadowRadius);
            highlight.setOffsetY(shadowRadius);
            innerShadow.setRadius(shadowRadius);
            innerShadow.setOffsetY(-shadowRadius);

            double center = size * 0.5;
            scaleFactor = size / PREFERRED_WIDTH;
            ring.setCache(false);
            ring.getTransforms().setAll(new Scale(scaleFactor, scaleFactor, 0, 0));
            ring.setCache(true);
            ring.setCacheHint(CacheHint.SPEED);

            mainCircle.setCache(false);
            mainCircle.setRadius(size * 0.3);
            mainCircle.setCenterX(center); mainCircle.setCenterY(center);
            mainCircle.setCache(true);
            mainCircle.setCacheHint(CacheHint.SPEED);

            text.setFont(Fonts.robotoMedium(size * 0.216));
            text.relocate((size - text.getLayoutBounds().getWidth()) * 0.5, size * 0.33);

            indicator.setRadius(size * 0.032);
            indicator.setCenterX(center);
            indicator.setCenterY(size * 0.148);

            indicatorRotate.setPivotX(center);
            indicatorRotate.setPivotY(center);

            icon.setIconSize((int) (size * 0.112));

            iconPane.setPrefSize(size * 0.112, size * 0.112);
            iconPane.relocate((size - iconPane.getPrefWidth()) * 0.5, size * 0.62);

            redraw();
        }
    }

    private void redraw() {
        pane.setBackground(new Background(new BackgroundFill(color.get().darker(), new CornerRadii(1024), Insets.EMPTY)));
        mainCircle.setFill(color.get().darker().darker());
        ring.setFill(color.get());
        indicator.setFill(color.get().darker());
        indicator.setStroke(color.get().darker().darker());
        symbol.setBackground(new Background(new BackgroundFill(symbolColor.get(), CornerRadii.EMPTY, Insets.EMPTY)));
        icon.setFill(iconColor.get());
        text.setFill(textColor.get());
        barCtx.setStroke(barColor.get());
        rotate(targetValue.get());
    }


    // ******************** Event Handling ************************************
    public void setOnTargetSet(final EventHandler<RegulatorEvent> HANDLER) { addEventHandler(RegulatorEvent.TARGET_SET, HANDLER); }
    public void removeOnTargetSet(final EventHandler<RegulatorEvent> HANDLER) { removeEventHandler(RegulatorEvent.TARGET_SET, HANDLER); }
}
