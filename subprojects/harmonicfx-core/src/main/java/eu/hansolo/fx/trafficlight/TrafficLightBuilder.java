/*
 * Copyright (c) 2014. by Gerrit Grunwald
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package eu.hansolo.fx.trafficlight;

import javafx.beans.property.*;
import javafx.geometry.Dimension2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;

import java.util.HashMap;


/**
 * Created by hansolo on 01.12.14.
 */
public class TrafficLightBuilder <B extends TrafficLightBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected TrafficLightBuilder() {}


    // ******************** Methods *******************************************
    public static final TrafficLightBuilder create() {
        return new TrafficLightBuilder();
    }

    public final B backgroundColor(final Color COLOR) {
        properties.put("backgroundColor", new SimpleObjectProperty<>(COLOR));
        return (B) this;
    }

    public final B upperOn(final boolean ON) {
        properties.put("upperOn", new SimpleBooleanProperty(ON));
        return (B) this;
    }

    public final B upperColor(final TrafficLight.LightColor COLOR) {
        properties.put("upperColor", new SimpleObjectProperty<>(COLOR));
        return (B) this;
    }

    public final B upperOverlayVisible(final boolean OVERLAY_VISIBLE) {
        properties.put("upperOverlayVisible", new SimpleBooleanProperty(OVERLAY_VISIBLE));
        return (B) this;
    }

    public final B upperOverlayShape(final String PATH_STRING) {
        properties.put("upperOverlayShape", new SimpleStringProperty(PATH_STRING));
        return (B) this;
    }

    public final B middleOn(final boolean ON) {
        properties.put("middleOn", new SimpleBooleanProperty(ON));
        return (B) this;
    }

    public final B middleColor(final TrafficLight.LightColor COLOR) {
        properties.put("middleColor", new SimpleObjectProperty<>(COLOR));
        return (B) this;
    }

    public final B middleOverlayVisible(final boolean OVERLAY_VISIBLE) {
        properties.put("middleOverlayVisible", new SimpleBooleanProperty(OVERLAY_VISIBLE));
        return (B) this;
    }

    public final B middleOverlayShape(final String PATH_STRING) {
        properties.put("middleOverlayShape", new SimpleStringProperty(PATH_STRING));
        return (B) this;
    }

    public final B lowerOn(final boolean ON) {
        properties.put("lowerOn", new SimpleBooleanProperty(ON));
        return (B) this;
    }

    public final B lowerColor(final TrafficLight.LightColor COLOR) {
        properties.put("lowerColor", new SimpleObjectProperty<>(COLOR));
        return (B) this;
    }

    public final B lowerOverlayVisible(final boolean OVERLAY_VISIBLE) {
        properties.put("lowerOverlayVisible", new SimpleBooleanProperty(OVERLAY_VISIBLE));
        return (B) this;
    }

    public final B lowerOverlayShape(final String PATH_STRING) {
        properties.put("lowerOverlayShape", new SimpleStringProperty(PATH_STRING));
        return (B) this;
    }

    public final B prefSize(final double WIDTH, final double HEIGHT) {
        properties.put("prefSize", new SimpleObjectProperty<>(new Dimension2D(WIDTH, HEIGHT)));
        return (B)this;
    }
    public final B minSize(final double WIDTH, final double HEIGHT) {
        properties.put("minSize", new SimpleObjectProperty<>(new Dimension2D(WIDTH, HEIGHT)));
        return (B)this;
    }
    public final B maxSize(final double WIDTH, final double HEIGHT) {
        properties.put("maxSize", new SimpleObjectProperty<>(new Dimension2D(WIDTH, HEIGHT)));
        return (B)this;
    }

    public final B prefWidth(final double PREF_WIDTH) {
        properties.put("prefWidth", new SimpleDoubleProperty(PREF_WIDTH));
        return (B)this;
    }
    public final B prefHeight(final double PREF_HEIGHT) {
        properties.put("prefHeight", new SimpleDoubleProperty(PREF_HEIGHT));
        return (B)this;
    }

    public final B minWidth(final double MIN_WIDTH) {
        properties.put("minWidth", new SimpleDoubleProperty(MIN_WIDTH));
        return (B)this;
    }
    public final B minHeight(final double MIN_HEIGHT) {
        properties.put("minHeight", new SimpleDoubleProperty(MIN_HEIGHT));
        return (B)this;
    }

    public final B maxWidth(final double MAX_WIDTH) {
        properties.put("maxWidth", new SimpleDoubleProperty(MAX_WIDTH));
        return (B)this;
    }
    public final B maxHeight(final double MAX_HEIGHT) {
        properties.put("maxHeight", new SimpleDoubleProperty(MAX_HEIGHT));
        return (B)this;
    }

    public final B scaleX(final double SCALE_X) {
        properties.put("scaleX", new SimpleDoubleProperty(SCALE_X));
        return (B)this;
    }
    public final B scaleY(final double SCALE_Y) {
        properties.put("scaleY", new SimpleDoubleProperty(SCALE_Y));
        return (B)this;
    }

    public final B layoutX(final double LAYOUT_X) {
        properties.put("layoutX", new SimpleDoubleProperty(LAYOUT_X));
        return (B)this;
    }
    public final B layoutY(final double LAYOUT_Y) {
        properties.put("layoutY", new SimpleDoubleProperty(LAYOUT_Y));
        return (B)this;
    }

    public final B translateX(final double TRANSLATE_X) {
        properties.put("translateX", new SimpleDoubleProperty(TRANSLATE_X));
        return (B)this;
    }
    public final B translateY(final double TRANSLATE_Y) {
        properties.put("translateY", new SimpleDoubleProperty(TRANSLATE_Y));
        return (B)this;
    }

    public final B opacity(final double OPACITY) {
        properties.put("opacity", new SimpleDoubleProperty(clamp(0, 1, OPACITY)));
        return (B) this;
    }

    private double clamp(final double MIN, final double MAX, final double VALUE) {
        if (VALUE < MIN) return MIN;
        if (VALUE > MAX) return MAX;
        return VALUE;
    }


    public final TrafficLight build() {
        final TrafficLight CONTROL = new TrafficLight();
        properties.forEach((key, property) -> {
            if ("prefSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                CONTROL.setPrefSize(dim.getWidth(), dim.getHeight());
            } else if("minSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                CONTROL.setPrefSize(dim.getWidth(), dim.getHeight());
            } else if("maxSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                CONTROL.setPrefSize(dim.getWidth(), dim.getHeight());
            } else if("prefWidth".equals(key)) {
                CONTROL.setPrefWidth(((DoubleProperty) properties.get(key)).get());
            } else if("prefHeight".equals(key)) {
                CONTROL.setPrefHeight(((DoubleProperty) properties.get(key)).get());
            } else if("minWidth".equals(key)) {
                CONTROL.setMinWidth(((DoubleProperty) properties.get(key)).get());
            } else if("minHeight".equals(key)) {
                CONTROL.setMinHeight(((DoubleProperty) properties.get(key)).get());
            } else if("maxWidth".equals(key)) {
                CONTROL.setMaxWidth(((DoubleProperty) properties.get(key)).get());
            } else if("maxHeight".equals(key)) {
                CONTROL.setMaxHeight(((DoubleProperty) properties.get(key)).get());
            } else if("scaleX".equals(key)) {
                CONTROL.setScaleX(((DoubleProperty) properties.get(key)).get());
            } else if("scaleY".equals(key)) {
                CONTROL.setScaleY(((DoubleProperty) properties.get(key)).get());
            } else if ("layoutX".equals(key)) {
                CONTROL.setLayoutX(((DoubleProperty) properties.get(key)).get());
            } else if ("layoutY".equals(key)) {
                CONTROL.setLayoutY(((DoubleProperty) properties.get(key)).get());
            } else if ("translateX".equals(key)) {
                CONTROL.setTranslateX(((DoubleProperty) properties.get(key)).get());
            } else if ("translateY".equals(key)) {
                CONTROL.setTranslateY(((DoubleProperty) properties.get(key)).get());
            } else if ("opacity".equals(key)) {
                CONTROL.setOpacity(((DoubleProperty) properties.get(key)).get());
            } else if ("backgroundColor".equals(key)) {
                CONTROL.setBackgroundColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("upperOn".equals(key)) {
                CONTROL.setUpperOn(((BooleanProperty) properties.get(key)).get());
            } else if ("upperColor".equals(key)) {
                CONTROL.setUpperColor(((ObjectProperty<TrafficLight.LightColor>) properties.get(key)).get());
            } else if ("upperOverlayVisible".equals(key)) {
                CONTROL.setUpperOverlayVisible(((BooleanProperty) properties.get(key)).get());
            } else if ("middleOn".equals(key)) {
                CONTROL.setMiddleOn(((BooleanProperty) properties.get(key)).get());
            } else if ("middleColor".equals(key)) {
                CONTROL.setMiddleColor(((ObjectProperty<TrafficLight.LightColor>) properties.get(key)).get());
            } else if ("middleOverlayVisible".equals(key)) {
                CONTROL.setMiddleOverlayVisible(((BooleanProperty) properties.get(key)).get());
            } else if ("lowerOn".equals(key)) {
                CONTROL.setLowerOn(((BooleanProperty) properties.get(key)).get());
            } else if ("lowerColor".equals(key)) {
                CONTROL.setLowerColor(((ObjectProperty<TrafficLight.LightColor>) properties.get(key)).get());
            } else if ("lowerOverlayVisible".equals(key)) {
                CONTROL.setLowerOverlayVisible(((BooleanProperty) properties.get(key)).get());
            } else if ("upperOverlayShape".equals(key)) {
                CONTROL.setUpperOverlay(SVGPathParser.INSTANCE.processPath(FillRule.NON_ZERO, ((StringProperty) properties.get(key)).get()));
            } else if ("middleOverlayShape".equals(key)) {
                CONTROL.setMiddleOverlay(SVGPathParser.INSTANCE.processPath(FillRule.NON_ZERO, ((StringProperty) properties.get(key)).get()));
            } else if ("lowerOverlayShape".equals(key)) {
                CONTROL.setLowerOverlay(SVGPathParser.INSTANCE.processPath(FillRule.NON_ZERO, ((StringProperty) properties.get(key)).get()));
            }
        });

        return CONTROL;
    }
}
