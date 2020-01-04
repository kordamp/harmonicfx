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
package eu.hansolo.fx.trafficlight;

import javafx.beans.property.*;
import javafx.css.PseudoClass;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;


/**
 * User: hansolo
 * Date: 01.12.14
 * Time: 10:33
 */
public class TrafficLight extends Region {
    public static enum LightColor { RED, YELLOW, GREEN }

    private static final double        PREFERRED_WIDTH  = 70;
    private static final double        PREFERRED_HEIGHT = 204;
    private static final double        MINIMUM_WIDTH    = 7;
    private static final double        MINIMUM_HEIGHT   = 20;
    private static final double        MAXIMUM_WIDTH    = 700;
    private static final double        MAXIMUM_HEIGHT   = 2004;
    private static final double        ASPECT_RATIO     = PREFERRED_HEIGHT / PREFERRED_WIDTH;
    private static final PseudoClass   ON_PSEUDO_CLASS  = PseudoClass.getPseudoClass("on");

    private ObjectProperty<Color>      backgroundColor;
    private BooleanProperty            upperOn;
    private BooleanProperty            upperOverlayVisible;
    private ObjectProperty<LightColor> upperColor;
    private BooleanProperty            middleOn;
    private BooleanProperty            middleOverlayVisible;
    private ObjectProperty<LightColor> middleColor;
    private BooleanProperty            lowerOn;
    private BooleanProperty            lowerOverlayVisible;
    private ObjectProperty<LightColor> lowerColor;

    private double                     lightSize;
    private double                     width;
    private double                     height;
    private Region                     upperBkg;
    private Region                     upperLight;
    private Region                     upperOverlay;
    private Shape                      upperOverlayShape;
    private Canvas                     upperGrid;
    private GraphicsContext            upperCtx;
    private Region                     upperVisor;
    private Region                     middleBkg;
    private Region                     middleLight;
    private Region                     middleOverlay;
    private Shape                      middleOverlayShape;
    private Canvas                     middleGrid;
    private GraphicsContext            middleCtx;
    private Region                     middleVisor;
    private Region                     lowerBkg;
    private Region                     lowerLight;
    private Region                     lowerOverlay;
    private Shape                      lowerOverlayShape;
    private Canvas                     lowerGrid;
    private GraphicsContext            lowerCtx;
    private Region                     lowerVisor;
    private Pane                       pane;
    private InnerShadow                innerShadow10;
    private InnerShadow                innerShadow4;
    private InnerShadow                innerShadow3;
    private DropShadow                 dropShadow4;


    // ******************** Constructors **************************************
    public TrafficLight() {
        getStylesheets().add(TrafficLight.class.getResource("trafficlight.css").toExternalForm());
        getStyleClass().add("traffic-light");

        backgroundColor      = new SimpleObjectProperty<>(this, "backgroundColor", Color.web("#505050"));
        upperOn              = new BooleanPropertyBase(false) {
            @Override protected void invalidated() { upperLight.pseudoClassStateChanged(ON_PSEUDO_CLASS, get()); }
            @Override public Object getBean() { return this; }
            @Override public String getName() { return "upperOn"; }
        };
        upperOverlayVisible  = new SimpleBooleanProperty(this, "upperOverlayVisible", false);
        upperColor           = new SimpleObjectProperty<>(this, "upperColor", LightColor.RED);
        middleOn             = new BooleanPropertyBase(false) {
            @Override protected void invalidated() { middleLight.pseudoClassStateChanged(ON_PSEUDO_CLASS, get()); }
            @Override public Object getBean() { return this; }
            @Override public String getName() { return "middleOn"; }
        };
        middleOverlayVisible = new SimpleBooleanProperty(this, "middleOverlayOn", false);
        middleColor          = new SimpleObjectProperty<>(this, "middleColor", LightColor.YELLOW);
        lowerOn              = new BooleanPropertyBase(false) {
            @Override protected void invalidated() { lowerLight.pseudoClassStateChanged(ON_PSEUDO_CLASS, get()); }
            @Override public Object getBean() { return this; }
            @Override public String getName() { return "lowerOn"; }
        };
        lowerOverlayVisible  = new SimpleBooleanProperty(this, "lowerOverlayVisible", false);
        lowerColor           = new SimpleObjectProperty<>(this, "lowerColor", LightColor.GREEN);

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
        innerShadow10 = new InnerShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, 0.85), 10, 0, 0, 0);
        innerShadow4  = new InnerShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, 0.65), 4, 0, 0, 0);
        innerShadow3  = new InnerShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, 0.65), 3, 0, 0, 1);
        dropShadow4   = new DropShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, 0.65), 4, 0, 0, 4);

        upperBkg = new Region();
        upperBkg.getStyleClass().setAll("light-bkg");
        upperBkg.setEffect(dropShadow4);

        upperLight = new Region();
        upperLight.getStyleClass().setAll("upper-light");
        upperLight.setEffect(innerShadow10);

        upperOverlay = new Region();
        upperOverlay.getStyleClass().setAll("upper-overlay");
        upperOverlay.setVisible(false);

        upperGrid = new Canvas();
        upperCtx = upperGrid.getGraphicsContext2D();

        upperVisor = new Region();
        upperVisor.getStyleClass().setAll("visor");
        upperVisor.setEffect(innerShadow3);

        middleBkg = new Region();
        middleBkg.getStyleClass().setAll("light-bkg");
        middleBkg.setEffect(dropShadow4);

        middleLight = new Region();
        middleLight.getStyleClass().setAll("middle-light");
        middleLight.setEffect(innerShadow10);

        middleGrid = new Canvas();
        middleCtx = middleGrid.getGraphicsContext2D();

        middleVisor = new Region();
        middleVisor.getStyleClass().setAll("visor");
        middleVisor.setEffect(innerShadow3);

        middleOverlay = new Region();
        middleOverlay.getStyleClass().setAll("middle-overlay");
        middleOverlay.setVisible(false);

        lowerBkg = new Region();
        lowerBkg.getStyleClass().setAll("light-bkg");
        lowerBkg.setEffect(dropShadow4);

        lowerLight = new Region();
        lowerLight.getStyleClass().setAll("lower-light");
        lowerLight.setEffect(innerShadow10);

        lowerOverlay = new Region();
        lowerOverlay.getStyleClass().setAll("lower-overlay");
        lowerOverlay.setVisible(false);

        lowerGrid = new Canvas();
        lowerCtx = lowerGrid.getGraphicsContext2D();

        lowerVisor = new Region();
        lowerVisor.getStyleClass().setAll("visor");
        lowerVisor.setEffect(innerShadow3);

        pane = new Pane();
        pane.getChildren().addAll(upperBkg, upperLight, upperOverlay, upperGrid, upperVisor,
                                  middleBkg, middleLight, middleOverlay, middleGrid, middleVisor,
                                  lowerBkg, lowerLight, lowerOverlay, lowerGrid, lowerVisor);
        pane.getStyleClass().add("background");
        pane.setEffect(innerShadow10);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(observable -> resize());
        heightProperty().addListener(observable -> resize());
        backgroundColor.addListener(o -> handleControlPropertyChanged("BACKGROUND_COLOR"));
        upperOverlayVisible.addListener(o -> upperOverlay.setVisible(upperOverlayVisible.get()));
        upperColor.addListener(o -> handleControlPropertyChanged("UPPER_COLOR"));
        middleOverlayVisible.addListener(o -> middleOverlay.setVisible(middleOverlayVisible.get()));
        middleColor.addListener(o -> handleControlPropertyChanged("MIDDLE_COLOR"));
        lowerOverlayVisible.addListener(o -> lowerOverlay.setVisible(lowerOverlayVisible.get()));
        lowerColor.addListener(o -> handleControlPropertyChanged("LOWER_COLOR"));
    }


    // ******************** Methods *******************************************
    private void handleControlPropertyChanged(final String PROPERTY) {
        if ("BACKGROUND_COLOR".equals(PROPERTY)) {
            setStyle("-background-color:" + backgroundColor.get().toString().replace("0x", "#") + ";");
        } else if ("UPPER_COLOR".equals(PROPERTY)) {
            switch(upperColor.get()) {
                case RED:
                    setStyle("-upper-color-off: -RED-OFF;" +
                             "-upper-color-on : -RED-ON;");
                    break;
                case YELLOW:
                    setStyle("-upper-color-off: -YELLOW-OFF;" +
                             "-upper-color-on : -YELLOW-ON;");
                    break;

                case GREEN:
                    setStyle("-upper-color-off: -GREEN-OFF;" +
                             "-upper-color-on : -GREEN-ON;");
                    break;
            }
        } else if ("MIDDLE_COLOR".equals(PROPERTY)) {
            switch(middleColor.get()) {
                case RED:
                    setStyle("-middle-color-off: -RED-OFF;" +
                             "-middle-color-on : -RED-ON;");
                    break;
                case YELLOW:
                    setStyle("-middle-color-off: -YELLOW-OFF;" +
                             "-middle-color-on : -YELLOW-ON;");
                    break;

                case GREEN:
                    setStyle("-middle-color-off: -GREEN-OFF;" +
                             "-middle-color-on : -GREEN-ON;");
                    break;
            }
        } else if ("LOWER_COLOR".equals(PROPERTY)) {
            switch(lowerColor.get()) {
                case RED:
                    setStyle("-lower-color-off: -RED-OFF;" +
                             "-lower-color-on : -RED-ON;");
                    break;
                case YELLOW:
                    setStyle("-lower-color-off: -YELLOW-OFF;" +
                             "-lower-color-on : -YELLOW-ON;");
                    break;

                case GREEN:
                    setStyle("-lower-color-off: -GREEN-OFF;" +
                             "-lower-color-on : -GREEN-ON;");
                    break;
            }
        }
    }

    public Color getBackgroundColor() { return backgroundColor.get(); }
    public void setBackgroundColor(final Color COLOR) { backgroundColor.set(COLOR); }
    public ObjectProperty<Color> backgroundColorProperty() { return backgroundColor; }

    public boolean isUpperOn() { return upperOn.get(); }
    public void setUpperOn(final boolean ON) { upperOn.set(ON); }
    public BooleanProperty upperOnProperty() { return upperOn; }

    public boolean isUpperOverlayVisible() { return upperOverlayVisible.get(); }
    public void setUpperOverlayVisible(final boolean VISIBLE) { upperOverlayVisible.set(VISIBLE); }
    public BooleanProperty upperOverlayVisibleProperty() { return upperOverlayVisible; }

    public LightColor getUpperColor() { return upperColor.get(); }
    public void setUpperColor(final LightColor LIGHT_COLOR) { upperColor.set(LIGHT_COLOR); }
    public ObjectProperty<LightColor> upperColorProperty() { return upperColor; }

    public boolean isMiddleOn() { return middleOn.get(); }
    public void setMiddleOn(final boolean ON) { middleOn.set(ON); }
    public BooleanProperty middleOnProperty() { return middleOn; }

    public boolean isMiddleOverlayVisible() { return middleOverlayVisible.get(); }
    public void setMiddleOverlayVisible(final boolean VISIBLE) { middleOverlayVisible.set(VISIBLE); }
    public BooleanProperty middleOverlayVisibleProperty() { return middleOverlayVisible; }

    public LightColor getMiddleColor() { return middleColor.get(); }
    public void setMiddleColor(final LightColor LIGHT_COLOR) { middleColor.set(LIGHT_COLOR); }
    public ObjectProperty<LightColor> middleColorProperty() { return middleColor; }

    public boolean isLowerOn() { return lowerOn.get(); }
    public void setLowerOn(final boolean ON) { lowerOn.set(ON); }
    public BooleanProperty lowerOnProperty() { return lowerOn; }

    public boolean isLowerOverlayVisible() { return lowerOverlayVisible.get(); }
    public void setLowerOverlayVisible(final boolean VISIBLE) { lowerOverlayVisible.set(VISIBLE); }
    public BooleanProperty lowerOverlayVisibleProperty()  { return lowerOverlayVisible; }

    public LightColor getLowerColor() { return lowerColor.get(); }
    public void setLowerColor(final LightColor LIGHT_COLOR) { lowerColor.set(LIGHT_COLOR); }
    public ObjectProperty<LightColor> lowerColorProperty() { return lowerColor; }

    public void setUpperOverlay(final Shape SHAPE) {
        upperOverlay.setShape(SHAPE);
        upperOverlayShape = SHAPE;
    }
    public void setMiddleOverlay(final Shape SHAPE) {
        middleOverlay.setShape(SHAPE);
        middleOverlayShape = SHAPE;
    }
    public void setLowerOverlay(final Shape SHAPE) {
        lowerOverlay.setShape(SHAPE);
        lowerOverlayShape = SHAPE;
    }


    // ******************** Canvas ********************************************
    private void drawGrid(final GraphicsContext CTX) {
        CTX.clearRect(0, 0, lightSize, lightSize);
        CTX.setStroke(Color.rgb(90, 90, 90, 0.2));
        CTX.setLineWidth(0.75);
        for (int x = 0 ; x < lightSize ; x += 3) {
            CTX.strokeLine(x, 0, lightSize, lightSize - x);
            CTX.strokeLine(0, lightSize - x, x, lightSize);
            CTX.strokeLine(lightSize - x, 0, 0, lightSize - x);
            CTX.strokeLine(lightSize, lightSize - x, lightSize - x, lightSize);
        }
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth();
        height = getHeight();

        if (ASPECT_RATIO * width > height) {
            width = 1 / (ASPECT_RATIO / height);
        } else if (1 / (ASPECT_RATIO / height) > width) {
            height = ASPECT_RATIO * width;
        }

        if (width > 0 && height > 0) {
            lightSize          = width * 0.68571;
            double bkgSize     = width * 0.8;
            double visorWidth  = width * 0.8;
            double visorHeight = width * 0.42857;

            innerShadow10.setRadius(0.14286 * width);
            innerShadow4.setRadius(0.05714 * width);

            innerShadow3.setRadius(0.04286 * width);
            innerShadow3.setOffsetY(0.01429 * width);

            dropShadow4.setRadius(0.05714 * width);
            dropShadow4.setOffsetY(0.05714 * width);

            pane.setMaxSize(width, height);
            pane.setPrefSize(width, height);
            pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            upperBkg.setPrefSize(bkgSize, bkgSize);
            upperBkg.relocate((width - bkgSize) * 0.5, height * 0.04412);
            upperBkg.setStyle("-fx-background-insets: 0, " + (width * 0.04286) + ";");
            upperLight.setPrefSize(lightSize, lightSize);
            upperLight.relocate((width - lightSize) * 0.5, height * 0.06373);
            if (null != upperOverlayShape) upperOverlay.setShape(upperOverlayShape);
            upperOverlay.setPrefSize(lightSize, lightSize);
            upperOverlay.relocate((width - lightSize) * 0.5, height * 0.06373);
            upperGrid.setWidth(lightSize);
            upperGrid.setHeight(lightSize);
            upperGrid.relocate((width - lightSize) * 0.5, height * 0.06373);
            drawGrid(upperCtx);
            upperGrid.setClip(new Circle(width * 0.345, height * 0.1175, lightSize * 0.5));
            upperVisor.setPrefSize(visorWidth, visorHeight);
            upperVisor.relocate((width - visorWidth) * 0.5, height * 0.04412);

            middleBkg.setPrefSize(bkgSize, bkgSize);
            middleBkg.relocate((width - bkgSize) * 0.5, height * 0.35784);
            middleBkg.setStyle("-fx-background-insets: 0, " + (width * 0.04286) + ";");
            middleLight.setPrefSize(lightSize, lightSize);
            middleLight.relocate((width - lightSize) * 0.5, height * 0.37745);
            if (null != middleOverlayShape) middleOverlay.setShape(middleOverlayShape);
            middleOverlay.setPrefSize(lightSize, lightSize);
            middleOverlay.relocate((width - lightSize) * 0.5, height * 0.37745);
            middleGrid.setWidth(lightSize);
            middleGrid.setHeight(lightSize);
            middleGrid.relocate((width - lightSize) * 0.5, height * 0.37745);
            drawGrid(middleCtx);
            middleGrid.setClip(new Circle(width * 0.345, height * 0.1175, lightSize * 0.5));
            middleVisor.setPrefSize(visorWidth, visorHeight);
            middleVisor.relocate((width - visorWidth) * 0.5, height * 0.35784);

            lowerBkg.setPrefSize(bkgSize, bkgSize);
            lowerBkg.relocate((width - bkgSize) * 0.5, height * 0.67157);
            lowerBkg.setStyle("-fx-background-insets: 0, " + (width * 0.04286) + ";");
            lowerLight.setPrefSize(lightSize, lightSize);
            lowerLight.relocate((width - lightSize) * 0.5, height * 0.69118);
            if (null != lowerOverlayShape) lowerOverlay.setShape(lowerOverlayShape);
            lowerOverlay.setPrefSize(lightSize, lightSize);
            lowerOverlay.relocate((width - lightSize) * 0.5, height * 0.69118);
            lowerGrid.setWidth(lightSize);
            lowerGrid.setHeight(lightSize);
            lowerGrid.relocate((width - lightSize) * 0.5, height * 0.69118);
            drawGrid(lowerCtx);
            lowerGrid.setClip(new Circle(width * 0.345, height * 0.1175, lightSize * 0.5));
            lowerVisor.setPrefSize(visorWidth, visorHeight);
            lowerVisor.relocate((width - visorWidth) * 0.5, height * 0.67157);
        }
    }
}
