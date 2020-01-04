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

import eu.hansolo.fx.segment.Element.State;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;


/**
 * User: hansolo
 * Date: 19.05.16
 * Time: 19:55
 */
public class Segment extends Region {
    public enum Character {
        EMPTY(new State[] { State.EMPTY, State.EMPTY, State.EMPTY,
                            State.EMPTY, State.EMPTY, State.EMPTY,
                            State.EMPTY, State.EMPTY, State.EMPTY,
                            State.EMPTY, State.EMPTY, State.EMPTY,
                            State.EMPTY, State.EMPTY, State.EMPTY }),
        C0(new State[]   { State.ROUND_UPPER_LEFT, State.SQUARE, State.ROUND_UPPER_RIGHT,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.ROUND_LOWER_LEFT, State.SQUARE, State.ROUND_LOWER_RIGHT }),
        C1(new State[]   { State.EMPTY, State.SQUARE, State.EMPTY,
                           State.SQUARE, State.SQUARE, State.EMPTY,
                           State.EMPTY, State.SQUARE, State.EMPTY,
                           State.EMPTY, State.SQUARE, State.EMPTY,
                           State.SQUARE, State.SQUARE, State.SQUARE }),
        C2(new State[]   { State.ROUND_UPPER_LEFT, State.SQUARE, State.ROUND_UPPER_RIGHT,
                           State.EMPTY, State.EMPTY, State.SQUARE,
                           State.TRIANGLE_LOWER_RIGHT, State.SQUARE, State.TRIANGLE_UPPER_LEFT,
                           State.SQUARE, State.EMPTY, State.EMPTY,
                           State.SQUARE, State.SQUARE, State.SQUARE }),
        C3(new State[]   { State.ROUND_UPPER_LEFT, State.SQUARE, State.ROUND_UPPER_RIGHT,
                           State.EMPTY, State.EMPTY, State.SQUARE,
                           State.EMPTY, State.SQUARE, State.NOT_TRIANGLE_RIGHT,
                           State.EMPTY, State.EMPTY, State.SQUARE,
                           State.ROUND_LOWER_LEFT, State.SQUARE, State.ROUND_LOWER_RIGHT }),
        C4(new State[]   { State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.SQUARE, State.SQUARE,
                           State.EMPTY, State.EMPTY, State.SQUARE,
                           State.EMPTY, State.EMPTY, State.SQUARE }),
        C5(new State[]   { State.SQUARE, State.SQUARE, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.EMPTY,
                           State.SQUARE, State.SQUARE, State.TRIANGLE_LOWER_LEFT,
                           State.EMPTY, State.EMPTY, State.SQUARE,
                           State.ROUND_LOWER_LEFT, State.SQUARE, State.ROUND_LOWER_RIGHT }),
        C6(new State[]   { State.ROUND_UPPER_LEFT, State.SQUARE, State.ROUND_UPPER_RIGHT,
                           State.SQUARE, State.EMPTY, State.EMPTY,
                           State.SQUARE, State.SQUARE, State.TRIANGLE_LOWER_LEFT,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.ROUND_LOWER_LEFT, State.SQUARE, State.ROUND_LOWER_RIGHT }),
        C7(new State[]   { State.SQUARE, State.SQUARE, State.SQUARE,
                           State.EMPTY, State.TRIANGLE_LOWER_RIGHT, State.TRIANGLE_UPPER_LEFT,
                           State.TRIANGLE_LOWER_RIGHT, State.TRIANGLE_UPPER_LEFT, State.EMPTY,
                           State.SQUARE, State.EMPTY, State.EMPTY,
                           State.SQUARE, State.EMPTY, State.EMPTY }),
        C8(new State[]   { State.ROUND_UPPER_LEFT, State.SQUARE, State.ROUND_UPPER_RIGHT,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.NOT_TRIANGLE_LEFT, State.SQUARE, State.NOT_TRIANGLE_RIGHT,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.ROUND_LOWER_LEFT, State.SQUARE, State.ROUND_LOWER_RIGHT }),
        C9(new State[]   { State.ROUND_UPPER_LEFT, State.SQUARE, State.ROUND_UPPER_RIGHT,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.TRIANGLE_UPPER_RIGHT, State.SQUARE, State.SQUARE,
                           State.EMPTY, State.EMPTY, State.SQUARE,
                           State.ROUND_LOWER_LEFT, State.SQUARE, State.ROUND_LOWER_RIGHT }),
        CA(new State[]   { State.ROUND_UPPER_LEFT, State.SQUARE, State.ROUND_UPPER_RIGHT,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.SQUARE, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE }),
        CB(new State[]   { State.SQUARE, State.SQUARE, State.ROUND_UPPER_RIGHT,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.SQUARE, State.NOT_TRIANGLE_RIGHT,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.SQUARE, State.ROUND_LOWER_RIGHT }),
        CC(new State[]   { State.ROUND_UPPER_LEFT, State.SQUARE, State.ROUND_UPPER_RIGHT,
                           State.SQUARE, State.EMPTY, State.EMPTY,
                           State.SQUARE, State.EMPTY, State.EMPTY,
                           State.SQUARE, State.EMPTY, State.EMPTY,
                           State.ROUND_LOWER_LEFT, State.SQUARE, State.ROUND_LOWER_RIGHT }),
        CD(new State[]   { State.SQUARE, State.SQUARE, State.ROUND_UPPER_RIGHT,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.SQUARE, State.ROUND_LOWER_RIGHT }),
        CE(new State[]   { State.SQUARE, State.SQUARE, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.EMPTY,
                           State.SQUARE, State.SQUARE, State.EMPTY,
                           State.SQUARE, State.EMPTY, State.EMPTY,
                           State.SQUARE, State.SQUARE, State.SQUARE }),
        CF(new State[]   { State.SQUARE, State.SQUARE, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.EMPTY,
                           State.SQUARE, State.SQUARE, State.EMPTY,
                           State.SQUARE, State.EMPTY, State.EMPTY,
                           State.SQUARE, State.EMPTY, State.EMPTY }),
        CG(new State[]   { State.ROUND_UPPER_LEFT, State.SQUARE, State.ROUND_UPPER_RIGHT,
                           State.SQUARE, State.EMPTY, State.EMPTY,
                           State.SQUARE, State.EMPTY, State.ROUND_UPPER_RIGHT,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.ROUND_LOWER_LEFT, State.SQUARE, State.ROUND_LOWER_RIGHT }),
        CH(new State[]   { State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.SQUARE, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE }),
        CI(new State[]   { State.SQUARE, State.SQUARE, State.SQUARE,
                           State.EMPTY, State.SQUARE, State.EMPTY,
                           State.EMPTY, State.SQUARE, State.EMPTY,
                           State.EMPTY, State.SQUARE, State.EMPTY,
                           State.SQUARE, State.SQUARE, State.SQUARE }),
        CJ(new State[]   { State.EMPTY, State.EMPTY, State.SQUARE,
                           State.EMPTY, State.EMPTY, State.SQUARE,
                           State.EMPTY, State.EMPTY, State.SQUARE,
                           State.EMPTY, State.EMPTY, State.SQUARE,
                           State.ROUND_LOWER_LEFT, State.SQUARE, State.ROUND_LOWER_RIGHT }),
        CK(new State[]   { State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.TRIANGLE_LOWER_RIGHT, State.TRIANGLE_UPPER_LEFT,
                           State.SQUARE, State.NOT_TRIANGLE_RIGHT, State.EMPTY,
                           State.SQUARE, State.TRIANGLE_UPPER_RIGHT, State.TRIANGLE_LOWER_LEFT,
                           State.SQUARE, State.EMPTY, State.SQUARE }),
        CL(new State[]   { State.SQUARE, State.EMPTY, State.EMPTY,
                           State.SQUARE, State.EMPTY, State.EMPTY,
                           State.SQUARE, State.EMPTY, State.EMPTY,
                           State.SQUARE, State.EMPTY, State.EMPTY,
                           State.SQUARE, State.SQUARE, State.SQUARE }),
        CM(new State[]   { State.TRIANGLE_LOWER_LEFT, State.EMPTY, State.TRIANGLE_LOWER_RIGHT,
                           State.SQUARE, State.NOT_TRIANGLE_TOP, State.SQUARE,
                           State.SQUARE, State.TRIANGLE_TOP, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE }),
        CN(new State[]   { State.TRIANGLE_LOWER_LEFT, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.TRIANGLE_LOWER_LEFT, State.SQUARE,
                           State.SQUARE, State.SQUARE, State.SQUARE,
                           State.SQUARE, State.TRIANGLE_UPPER_RIGHT, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.TRIANGLE_UPPER_RIGHT }),
        CO(new State[]   { State.SQUARE, State.SQUARE, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.SQUARE, State.SQUARE }),
        CP(new State[]   { State.SQUARE, State.SQUARE, State.ROUND_UPPER_RIGHT,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.SQUARE, State.ROUND_LOWER_RIGHT,
                           State.SQUARE, State.EMPTY, State.EMPTY,
                           State.SQUARE, State.EMPTY, State.EMPTY }),
        CQ(new State[]   { State.SQUARE, State.SQUARE, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.TRIANGLE_UPPER_RIGHT, State.SQUARE,
                           State.SQUARE, State.SQUARE, State.TRIANGLE_UPPER_RIGHT }),
        CR(new State[]   { State.SQUARE, State.SQUARE, State.ROUND_UPPER_RIGHT,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.SQUARE, State.NOT_TRIANGLE_RIGHT,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE }),
        CS(new State[]   { State.ROUND_UPPER_LEFT, State.SQUARE, State.ROUND_UPPER_RIGHT,
                           State.SQUARE, State.EMPTY, State.EMPTY,
                           State.ROUND_LOWER_LEFT, State.SQUARE, State.ROUND_UPPER_RIGHT,
                           State.EMPTY, State.EMPTY, State.SQUARE,
                           State.ROUND_LOWER_LEFT, State.SQUARE, State.ROUND_LOWER_RIGHT }),
        CT(new State[]   { State.SQUARE, State.SQUARE, State.SQUARE,
                           State.EMPTY, State.SQUARE, State.EMPTY,
                           State.EMPTY, State.SQUARE, State.EMPTY,
                           State.EMPTY, State.SQUARE, State.EMPTY,
                           State.EMPTY, State.SQUARE, State.EMPTY }),
        CU(new State[]   { State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.ROUND_LOWER_LEFT, State.SQUARE, State.ROUND_LOWER_RIGHT }),
        CV(new State[]   { State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.TRIANGLE_UPPER_RIGHT, State.NOT_TRIANGLE_TOP, State.TRIANGLE_UPPER_LEFT }),
        CW(new State[]   { State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.TRIANGLE_BOTTOM, State.SQUARE,
                           State.SQUARE, State.NOT_TRIANGLE_BOTTOM, State.SQUARE,
                           State.TRIANGLE_UPPER_LEFT, State.EMPTY, State.TRIANGLE_UPPER_RIGHT }),
        CX(new State[]   { State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.NOT_TRIANGLE_LEFT, State.SQUARE, State.NOT_TRIANGLE_RIGHT,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE }),
        CY(new State[]   { State.SQUARE, State.EMPTY, State.SQUARE,
                           State.SQUARE, State.EMPTY, State.SQUARE,
                           State.TRIANGLE_UPPER_RIGHT, State.NOT_TRIANGLE_TOP, State.TRIANGLE_UPPER_LEFT,
                           State.EMPTY, State.SQUARE, State.EMPTY,
                           State.EMPTY, State.SQUARE, State.EMPTY }),
        CZ(new State[]   { State.SQUARE, State.SQUARE, State.SQUARE,
                           State.EMPTY, State.TRIANGLE_LOWER_RIGHT, State.TRIANGLE_UPPER_LEFT,
                           State.TRIANGLE_LOWER_RIGHT, State.TRIANGLE_UPPER_LEFT, State.EMPTY,
                           State.SQUARE, State.EMPTY, State.EMPTY,
                           State.SQUARE, State.SQUARE, State.SQUARE }),
        CMINUS(new State[] { State.EMPTY, State.EMPTY, State.EMPTY,
                           State.EMPTY, State.EMPTY, State.EMPTY,
                           State.TRIANGLE_RIGHT, State.SQUARE, State.TRIANGLE_LEFT,
                           State.EMPTY, State.EMPTY, State.EMPTY,
                           State.EMPTY, State.EMPTY, State.EMPTY });

        public final State[] STATES;

        Character(final State[] STATES) {
            this.STATES = STATES;
        }
    }

    private static final double                    PREFERRED_WIDTH  = 26;
    private static final double                    PREFERRED_HEIGHT = 43.20300752;
    private static final double                    MINIMUM_WIDTH    = 26;
    private static final double                    MINIMUM_HEIGHT   = 43.20300752;
    private static final double                    MAXIMUM_WIDTH    = 1330;
    private static final double                    MAXIMUM_HEIGHT   = 2210;
    private static       double                    aspectRatio;
    private              double                    width;
    private              double                    height;
    private              Element                   e00;
    private              Element                   e10;
    private              Element                   e20;
    private              Element                   e01;
    private              Element                   e11;
    private              Element                   e21;
    private              Element                   e02;
    private              Element                   e12;
    private              Element                   e22;
    private              Element                   e03;
    private              Element                   e13;
    private              Element                   e23;
    private              Element                   e04;
    private              Element                   e14;
    private              Element                   e24;
    private              GridPane                  pane;
    private              ObjectProperty<Character> character;
    private              ObjectProperty<Color>     color;


    // ******************** Constructors **************************************
    public Segment() {
        this(Character.EMPTY);
    }
    public Segment(final Character CHARACTER) {
        getStylesheets().add(Segment.class.getResource("segment.css").toExternalForm());
        getStyleClass().add("segment");
        aspectRatio = PREFERRED_HEIGHT / PREFERRED_WIDTH;
        character   = new ObjectPropertyBase<Character>(CHARACTER) {
            @Override protected void invalidated() { resize(); }
            @Override public Object getBean() { return Segment.this; }
            @Override public String getName() { return "character"; }
        };
        color       = new ObjectPropertyBase<Color>() {
            @Override protected void invalidated() {
                final Color COLOR = get();
                pane.setBackground(new Background(new BackgroundFill(COLOR.deriveColor(0, 0, -0.8, 1), CornerRadii.EMPTY, Insets.EMPTY)));
                e00.setColor(COLOR);
                e10.setColor(COLOR);
                e20.setColor(COLOR);
                e01.setColor(COLOR);
                e11.setColor(COLOR);
                e21.setColor(COLOR);
                e02.setColor(COLOR);
                e12.setColor(COLOR);
                e22.setColor(COLOR);
                e03.setColor(COLOR);
                e13.setColor(COLOR);
                e23.setColor(COLOR);
                e04.setColor(COLOR);
                e14.setColor(COLOR);
                e24.setColor(COLOR);
            }
            @Override public Object getBean() { return Segment.this; }
            @Override public String getName() { return "color"; }
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
        e00 = new Element();
        e10 = new Element();
        e20 = new Element();
        e01 = new Element();
        e11 = new Element();
        e21 = new Element();
        e02 = new Element();
        e12 = new Element();
        e22 = new Element();
        e03 = new Element();
        e13 = new Element();
        e23 = new Element();
        e04 = new Element();
        e14 = new Element();
        e24 = new Element();

        pane = new GridPane();
        pane.add(e00, 0, 0);
        pane.add(e10, 1, 0);
        pane.add(e20, 2, 0);
        pane.add(e01, 0, 1);
        pane.add(e11, 1, 1);
        pane.add(e21, 2, 1);
        pane.add(e02, 0, 2);
        pane.add(e12, 1, 2);
        pane.add(e22, 2, 2);
        pane.add(e03, 0, 3);
        pane.add(e13, 1, 3);
        pane.add(e23, 2, 3);
        pane.add(e04, 0, 4);
        pane.add(e14, 1, 4);
        pane.add(e24, 2, 4);
        pane.setHgap(2);
        pane.setVgap(2);
        pane.setPadding(new Insets(2));
        pane.getStyleClass().add("background");

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        // add listeners to your propertes like
        //value.addListener(o -> handleControlPropertyChanged("VALUE"));
    }


    // ******************** Methods *******************************************
    public Character getCharacter() { return character.get(); }
    public void setCharacter(final Character CHARACTER) { character.set(CHARACTER); }
    public ObjectProperty<Character> characterProperty() { return character; }

    public Color getColor() { return color.get(); }
    public void setColor(final Color COLOR) { color.set(COLOR); }
    public ObjectProperty<Color> colorProperty() { return color; }


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

            double spacer = width * 0.0075188;
            pane.setHgap(spacer);
            pane.setVgap(spacer);
            pane.setPadding(new Insets(spacer));

            double elementSize = width * 0.32330827;

            e00.setPrefSize(elementSize, elementSize);
            e10.setPrefSize(elementSize, elementSize);
            e20.setPrefSize(elementSize, elementSize);
            e01.setPrefSize(elementSize, elementSize);
            e11.setPrefSize(elementSize, elementSize);
            e21.setPrefSize(elementSize, elementSize);
            e02.setPrefSize(elementSize, elementSize);
            e12.setPrefSize(elementSize, elementSize);
            e22.setPrefSize(elementSize, elementSize);
            e03.setPrefSize(elementSize, elementSize);
            e13.setPrefSize(elementSize, elementSize);
            e23.setPrefSize(elementSize, elementSize);
            e04.setPrefSize(elementSize, elementSize);
            e14.setPrefSize(elementSize, elementSize);
            e24.setPrefSize(elementSize, elementSize);

            redraw();
        }
    }

    private void redraw() {
        e00.setState(getCharacter().STATES[0]);
        e10.setState(getCharacter().STATES[1]);
        e20.setState(getCharacter().STATES[2]);
        e01.setState(getCharacter().STATES[3]);
        e11.setState(getCharacter().STATES[4]);
        e21.setState(getCharacter().STATES[5]);
        e02.setState(getCharacter().STATES[6]);
        e12.setState(getCharacter().STATES[7]);
        e22.setState(getCharacter().STATES[8]);
        e03.setState(getCharacter().STATES[9]);
        e13.setState(getCharacter().STATES[10]);
        e23.setState(getCharacter().STATES[11]);
        e04.setState(getCharacter().STATES[12]);
        e14.setState(getCharacter().STATES[13]);
        e24.setState(getCharacter().STATES[14]);
    }
}
