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
package eu.hansolo.fx.segment;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;


/**
 * User: hansolo
 * Date: 19.05.16
 * Time: 19:38
 */
public class Element extends Region {
    public  enum State {
        EMPTY(""),
        SQUARE("M 0 0 L 8.6 0 L 8.6 8.6 L 0 8.6 L 0 0 Z"),
        ROUND_UPPER_LEFT("M 8.6 8.6 L 8.6 8.6 L 8.6 0 C 3.85029 0 0 3.85029 0 8.6 L 8.6 8.6 Z"),
        ROUND_UPPER_RIGHT("M 0 8.6 L 0 8.6 L 0 0 C 4.74971 0 8.6 3.85029 8.6 8.6 L 0 8.6 Z"),
        ROUND_LOWER_RIGHT("M 0 0 L 0 0 L 0 8.6 C 4.74971 8.6 8.6 4.74971 8.6 0 L 0 0 Z"),
        ROUND_LOWER_LEFT("M 8.6 0 L 8.6 0 L 8.6 8.6 C 3.85029 8.6 0 4.74971 0 0 L 8.6 0 Z"),
        TRIANGLE_TOP("M 0 0 L 8.6 0 L 4.3 4.3 L 0 0 Z"),
        TRIANGLE_UPPER_RIGHT("M 0 0 L 8.6 0 L 8.6 8.6 L 0 0 Z"),
        TRIANGLE_RIGHT("M 8.6 0 L 8.6 8.6 L 4.3 4.3 L 8.6 0 Z"),
        TRIANGLE_LOWER_RIGHT("M 8.6 0 L 8.6 8.6 L 0 8.6 L 8.6 0 Z"),
        TRIANGLE_BOTTOM("M 0 8.6 L 4.3 4.3 L 8.6 8.6 L 0 8.6 Z"),
        TRIANGLE_LOWER_LEFT("M 0 0 L 8.6 8.6 L 0 8.6 L 0 0 Z"),
        TRIANGLE_LEFT("M 0 0 L 4.3 4.3 L 0 8.6 L 0 0 Z"),
        TRIANGLE_UPPER_LEFT("M 0 0 L 8.6 0 L 0 8.6 L 0 0 Z"),
        NOT_TRIANGLE_TOP("M 0 0 L 4.3 4.3 L 8.6 0 L 8.6 8.6 L 0 8.6 L 0 0 Z"),
        NOT_TRIANGLE_RIGHT("M 0 0 L 8.6 0 L 4.3 4.3 L 8.6 8.6 L 0 8.6 L 0 0 Z"),
        NOT_TRIANGLE_BOTTOM("M 0 0 L 8.6 0 L 8.6 8.6 L 4.3 4.3 L 0 8.6 L 0 0 Z"),
        NOT_TRIANGLE_LEFT("M 0 0 L 8.6 0 L 8.6 8.6 L 0 8.6 L 4.3 4.3 L 0 0 Z");

        public final String SVG_PATH_STRING;

        State(final String SVG_PATH_STRING) {
            this.SVG_PATH_STRING = SVG_PATH_STRING;
        }
    }
    private static final double                PREFERRED_WIDTH  = 86;
    private static final double                PREFERRED_HEIGHT = 86;
    private static final double                MINIMUM_WIDTH    = 8.6;
    private static final double                MINIMUM_HEIGHT   = 8.6;
    private static final double                MAXIMUM_WIDTH    = 1024;
    private static final double                MAXIMUM_HEIGHT   = 1024;
    private              double                size;
    private              SVGPath               path;
    private              StackPane             pane;
    private              ObjectProperty<State> state;
    private              Color                 _color;
    private              ObjectProperty<Color> color;


    // ******************** Constructors **************************************
    public Element() {
        this(State.EMPTY);
    }
    public Element(final State STATE) {
        getStylesheets().add(Element.class.getResource("segment.css").toExternalForm());
        getStyleClass().add("element");

        state = new ObjectPropertyBase<State>(State.EMPTY) {
            @Override protected void invalidated() {
                path.setContent(get().SVG_PATH_STRING);
                switch(get()) {
                    case TRIANGLE_TOP   : pane.setAlignment(Pos.TOP_CENTER);    break;
                    case TRIANGLE_RIGHT : pane.setAlignment(Pos.CENTER_RIGHT);  break;
                    case TRIANGLE_BOTTOM: pane.setAlignment(Pos.BOTTOM_CENTER); break;
                    case TRIANGLE_LEFT  : pane.setAlignment(Pos.CENTER_LEFT);   break;
                    default             : pane.setAlignment(Pos.CENTER);        break;
                }
            }
            @Override public Object getBean() { return Element.this; }
            @Override public String getName() { return "state"; }
        };

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
        path = new SVGPath();
        path.getStyleClass().add("path");
        path.setContent(getState().SVG_PATH_STRING);

        pane = new StackPane(path);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
    }


    // ******************** Methods *******************************************
    public State getState() { return state.get(); }
    public void setState(final State STATE) { state.set(STATE); }
    public ObjectProperty<State> stateProperty() { return state; }

    public Color getColor() { return null == color ? _color : color.get(); }
    public void setColor(final Color COLOR) {
        if (null == color) {
            _color = COLOR;
            path.setFill(_color);
        } else {
            color.set(COLOR);
        }
    }
    public ObjectProperty<Color> colorProperty() {
        if (null == color) {
            color = new ObjectPropertyBase<Color>(_color) {
                @Override protected void invalidated() { path.setFill(_color); }
                @Override public Object getBean() { return Element.this; }
                @Override public String getName() { return "color";}
            };
            _color = null;
        }
        return color;
    }


    // ******************** Resizing ******************************************
    private void resize() {
        double width = getWidth() - getInsets().getLeft() - getInsets().getRight();
        double height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size = width < height ? width : height;

        if (width > 0 && height > 0) {
            pane.setMaxSize(size, size);
            pane.setPrefSize(size, size);
            pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

            double factor = size / 8.6;
            path.setScaleX(factor);
            path.setScaleY(factor);
        }
    }
}
