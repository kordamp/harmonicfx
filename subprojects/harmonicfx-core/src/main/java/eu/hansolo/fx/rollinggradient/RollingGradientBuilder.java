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
package eu.hansolo.fx.rollinggradient;

import eu.hansolo.fx.rollinggradient.RollingGradient.Direction;
import eu.hansolo.fx.rollinggradient.event.UpdateEventListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import java.util.HashMap;


public class RollingGradientBuilder<B extends RollingGradientBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected RollingGradientBuilder() {}


    // ******************** Methods *******************************************
    public static final RollingGradientBuilder create() {
        return new RollingGradientBuilder();
    }

    public final B firstColor(final Color COLOR) {
        properties.put("firstColor", new SimpleObjectProperty(COLOR));
        return (B)this;
    }

    public final B secondColor(final Color COLOR) {
        properties.put("secondColor", new SimpleObjectProperty(COLOR));
        return (B)this;
    }

    public final B smoothGradient(final boolean SMOOTH) {
        properties.put("smoothGradient", new SimpleBooleanProperty(SMOOTH));
        return (B)this;
    }

    public final B direction(final Direction DIRECTION) {
        properties.put("direction", new SimpleObjectProperty(DIRECTION));
        return (B)this;
    }

    public final B period(final int PERIOD) {
        properties.put("period", new SimpleIntegerProperty(PERIOD));
        return (B)this;
    }

    public final B interval(final long INTERVAL) {
        properties.put("interval", new SimpleLongProperty(INTERVAL));
        return (B)this;
    }

    public final B shape(final Shape SHAPE) {
        properties.put("shape", new SimpleObjectProperty<>(SHAPE));
        return (B)this;
    }

    public final B start() {
        properties.put("start", null);
        return (B)this;
    }

    public final B onUpdateEvent(final UpdateEventListener LISTENER) {
        properties.put("onUpdateEvent", new SimpleObjectProperty<>(LISTENER));
        return (B)this;
    }

    public final RollingGradient build() {
        final RollingGradient GRADIENT = new RollingGradient();
        for (String key : properties.keySet()) {
            if ("firstColor".equals(key)) {
                GRADIENT.setFirstColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("secondColor".equals(key)) {
                GRADIENT.setSecondColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if("smoothGradient".equals(key)) {
                GRADIENT.setSmoothGradient(((BooleanProperty) properties.get(key)).get());
            } else if("direction".equals(key)) {
                GRADIENT.setDirection(((ObjectProperty<Direction>) properties.get(key)).get());
            } else if("period".equals(key)) {
                GRADIENT.setPeriod(((IntegerProperty) properties.get(key)).get());
            } else if ("interval".equals(key)) {
                GRADIENT.setInterval(((LongProperty) properties.get(key)).get());
            } else if ("shape".equals(key)) {
                GRADIENT.setShape(((ObjectProperty<Shape>) properties.get(key)).get());
            } else if ("onUpdateEvent".equals(key)) {
                GRADIENT.setOnUpdateEvent(((ObjectProperty<UpdateEventListener>) properties.get(key)).get());
            } else if ("start".equals(key)) {
                GRADIENT.start();
            }
        }
        return GRADIENT;
    }
}
