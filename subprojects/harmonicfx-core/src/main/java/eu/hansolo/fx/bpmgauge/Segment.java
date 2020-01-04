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
package eu.hansolo.fx.bpmgauge;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;


/**
 * User: hansolo
 * Date: 12.05.16
 * Time: 08:04
 */
public class Segment extends Region {
    private static final Duration        ANIMATION_DURATION = new Duration(100);
    private static final double          PREFERRED_WIDTH    = 23;
    private static final double          PREFERRED_HEIGHT   = 13;
    private static final double          MINIMUM_WIDTH      = 23;
    private static final double          MINIMUM_HEIGHT     = 13;
    private static final double          MAXIMUM_WIDTH      = 2300;
    private static final double          MAXIMUM_HEIGHT     = 1300;
    private static final double          ASPECT_RATIO       = PREFERRED_HEIGHT / PREFERRED_WIDTH;
    private              double          width;
    private              double          height;
    private              MoveTo          moveTo;
    private              LineTo          upperLeft;
    private              LineTo          upperRight;
    private              LineTo          lowerRight;
    private              LineTo          lowerLeft;
    private              Path            path;
    private              Pane            pane;
    private              Timeline        timeline;
    private              BooleanProperty active;


    // ******************** Constructors **************************************
    public Segment() {
        this(false);
    }
    public Segment(final boolean ACTIVE) {
        getStylesheets().add(Segment.class.getResource("styles.css").toExternalForm());
        getStyleClass().add("segment");
        timeline = new Timeline();
        active   = new BooleanPropertyBase(ACTIVE) {
            @Override public Object getBean() { return Segment.this; }
            @Override public String getName() { return "active"; }
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
        moveTo     = new MoveTo();
        upperLeft  = new LineTo();
        upperRight = new LineTo();
        lowerRight = new LineTo();
        lowerLeft  = new LineTo();

        path = new Path();
        path.getElements().add(moveTo);
        path.getElements().add(upperLeft);
        path.getElements().add(upperRight);
        path.getElements().add(lowerRight);
        path.getElements().add(lowerLeft);
        path.getElements().add(new ClosePath());

        path.getStyleClass().add("segment");

        pane = new Pane(path);
        pane.getStyleClass().add("segment");

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        activeProperty().addListener(o -> { if (isActive()) activate(); else deactivate(); });
    }


    // ******************** Methods *******************************************
    public boolean isActive() { return active.get(); }
    public void setActive(final boolean ACTIVE) { active.set(ACTIVE); }
    public BooleanProperty activeProperty() { return active; }

    private void activate() {
        timeline.stop();

        KeyValue kvUpperLeftX1  = new KeyValue(upperLeft.xProperty(), width * 0.56521739);
        KeyValue kvUpperLeftY1  = new KeyValue(upperLeft.yProperty(), 0);
        KeyValue kvUpperRightX1 = new KeyValue(upperRight.xProperty(), width);
        KeyValue kvUpperRightY1 = new KeyValue(upperRight.yProperty(), 0);
        KeyFrame kf1            = new KeyFrame(ANIMATION_DURATION, kvUpperLeftX1, kvUpperLeftY1, kvUpperRightX1, kvUpperRightY1);

        timeline.getKeyFrames().setAll(kf1);
        timeline.play();
    }
    private void deactivate() {
        timeline.stop();

        KeyValue kvUpperLeftX1  = new KeyValue(upperLeft.xProperty(), width * 0.30434783);
        KeyValue kvUpperLeftY1  = new KeyValue(upperLeft.yProperty(), height * 0.46153846);
        KeyValue kvUpperRightX1 = new KeyValue(upperRight.xProperty(), width * 0.73913043);
        KeyValue kvUpperRightY1 = new KeyValue(upperRight.yProperty(), height * 0.46153846);
        KeyFrame kf1            = new KeyFrame(ANIMATION_DURATION, kvUpperLeftX1, kvUpperLeftY1, kvUpperRightX1, kvUpperRightY1);

        timeline.getKeyFrames().setAll(kf1);
        timeline.play();
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        if (ASPECT_RATIO * width > height) {
            width = 1 / (ASPECT_RATIO / height);
        } else if (1 / (ASPECT_RATIO / height) > width) {
            height = ASPECT_RATIO * width;
        }

        if (width > 0 && height > 0) {
            pane.setMaxSize(width, height);
            pane.setPrefSize(width, height);
            pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            if (isActive()) {
                upperLeft.setX(width * 0.56521739); upperLeft.setY(0);
                upperRight.setX(width); upperRight.setY(0);
            } else {
                upperLeft.setX(width * 0.30434783); upperLeft.setY(height * 0.46153846);
                upperRight.setX(width * 0.73913043); upperRight.setY(height * 0.46153846);
            }
            moveTo.setX(0); moveTo.setY(height);
            lowerLeft.setX(0); lowerLeft.setY(height);
            lowerRight.setX(width * 0.43478261); lowerRight.setY(height);
        }
    }
}
