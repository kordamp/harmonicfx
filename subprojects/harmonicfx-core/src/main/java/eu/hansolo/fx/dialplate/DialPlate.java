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
package eu.hansolo.fx.dialplate;

import eu.hansolo.fx.fonts.Fonts;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;


/**
 * User: hansolo
 * Date: 28.06.16
 * Time: 08:54
 */
public class DialPlate extends Region {
    private static final double PREFERRED_WIDTH  = 410;
    private static final double PREFERRED_HEIGHT = 410;
    private static final double MINIMUM_WIDTH    = 100;
    private static final double MINIMUM_HEIGHT   = 100;
    private static final double MAXIMUM_WIDTH    = 1024;
    private static final double MAXIMUM_HEIGHT   = 1024;

    private double      size;
    private double      width;
    private double      height;
    private double      theta;
    private double      currentAngle;
    private Pane        pane;
    private Region      background;
    private Circle      touchOne;
    private Circle      touchTwo;
    private Circle      touchThree;
    private Circle      touchFour;
    private Circle      touchFive;
    private Circle      touchSix;
    private Circle      touchSeven;
    private Circle      touchEight;
    private Circle      touchNine;
    private Circle      touchZero;
    private Text        one;
    private Text        two;
    private Text        three;
    private Text        four;
    private Text        five;
    private Text        six;
    private Text        seven;
    private Text        eight;
    private Text        nine;
    private Text        zero;
    private Region      plate;
    private InnerShadow plateInnerShadow0;
    private InnerShadow plateInnerShadow1;
    private DropShadow  plateDropShadow2;
    private Group       shadowGroup;
    private Region      post;
    private InnerShadow postInnerShadow0;
    private InnerShadow postInnerShadow1;
    private DropShadow  postDropShadow2;
    private Rotate      plateRotate;
    private Timeline    timeline;


    // ******************** Constructors **************************************
    public DialPlate() {
        getStylesheets().add(DialPlate.class.getResource("dialplate.css").toExternalForm());
        getStyleClass().add("dial-plate");
        currentAngle = 0;
        timeline = new Timeline();
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
        background = new Region();
        background.getStyleClass().setAll("background");

        touchOne   = createCircle();
        touchTwo   = createCircle();
        touchThree = createCircle();
        touchFour  = createCircle();
        touchFive  = createCircle();
        touchSix   = createCircle();
        touchSeven = createCircle();
        touchEight = createCircle();
        touchNine  = createCircle();
        touchZero  = createCircle();

        one   = createText("1");
        two   = createText("2");
        three = createText("3");
        four  = createText("4");
        five  = createText("5");
        six   = createText("6");
        seven = createText("7");
        eight = createText("8");
        nine  = createText("9");
        zero  = createText("0");

        plateRotate = new Rotate(currentAngle);

        plate = new Region();
        plate.setMouseTransparent(true);
        plate.getStyleClass().setAll("plate");
        plate.getTransforms().add(plateRotate);

        plateInnerShadow0 = new InnerShadow();
        plateInnerShadow0.setOffsetX(0);
        plateInnerShadow0.setOffsetY(2.0);
        plateInnerShadow0.setRadius(2.0 / PREFERRED_WIDTH * PREFERRED_WIDTH);
        plateInnerShadow0.setColor(Color.rgb(255, 255, 255, 0.65));
        plateInnerShadow0.setBlurType(BlurType.TWO_PASS_BOX);
        plateInnerShadow1 = new InnerShadow();
        plateInnerShadow1.setOffsetX(0);
        plateInnerShadow1.setOffsetY(-2.0);
        plateInnerShadow1.setRadius(2.0 / PREFERRED_WIDTH * PREFERRED_WIDTH);
        plateInnerShadow1.setColor(Color.rgb(0, 0, 0, 0.5));
        plateInnerShadow1.setBlurType(BlurType.TWO_PASS_BOX);
        plateInnerShadow1.setInput(plateInnerShadow0);
        plateDropShadow2 = new DropShadow();
        plateDropShadow2.setOffsetX(0);
        plateDropShadow2.setOffsetY(3.0);
        plateDropShadow2.setRadius(3.0 / PREFERRED_WIDTH * PREFERRED_WIDTH);
        plateDropShadow2.setColor(Color.rgb(0, 0, 0, 0.5));
        plateDropShadow2.setBlurType(BlurType.TWO_PASS_BOX);
        plateDropShadow2.setInput(plateInnerShadow1);

        shadowGroup = new Group(plate);
        shadowGroup.setEffect(plateDropShadow2);

        post = new Region();
        post.getStyleClass().setAll("post");

        postInnerShadow0 = new InnerShadow();
        postInnerShadow0.setOffsetX(0);
        postInnerShadow0.setOffsetY(3.0);
        postInnerShadow0.setRadius(3.0 / PREFERRED_WIDTH * PREFERRED_WIDTH);
        postInnerShadow0.setColor(Color.rgb(255, 255, 255, 0.65));
        postInnerShadow0.setBlurType(BlurType.TWO_PASS_BOX);
        postInnerShadow1 = new InnerShadow();
        postInnerShadow1.setOffsetX(0);
        postInnerShadow1.setOffsetY(-3.0);
        postInnerShadow1.setRadius(3.0 / PREFERRED_WIDTH * PREFERRED_WIDTH);
        postInnerShadow1.setColor(Color.rgb(0, 0, 0, 0.5));
        postInnerShadow1.setBlurType(BlurType.TWO_PASS_BOX);
        postInnerShadow1.setInput(postInnerShadow0);
        postDropShadow2 = new DropShadow();
        postDropShadow2.setOffsetX(0);
        postDropShadow2.setOffsetY(4.0);
        postDropShadow2.setRadius(4.0 / PREFERRED_WIDTH* PREFERRED_WIDTH);
        postDropShadow2.setColor(Color.rgb(0, 0, 0, 0.5));
        postDropShadow2.setBlurType(BlurType.TWO_PASS_BOX);
        postDropShadow2.setInput(postInnerShadow1);
        post.setEffect(postDropShadow2);

        pane = new Pane(background,
                        touchOne, touchTwo, touchThree, touchFour, touchFive, touchSix, touchSeven, touchEight, touchNine, touchZero,
                        one, two, three, four, five, six, seven, eight, nine, zero,
                        shadowGroup,
                        post);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        touchOne.setOnDragDetected(e -> handleDial(e, 85, 1));
        touchOne.setOnMouseDragged(e -> handleDial(e, 85, 1));
        touchOne.setOnMouseReleased(e -> handleDial(e, 85, 1));
        touchTwo.setOnDragDetected(e -> handleDial(e, 110, 2));
        touchTwo.setOnMouseDragged(e -> handleDial(e, 110, 2));
        touchTwo.setOnMouseReleased(e -> handleDial(e, 110, 2));
        touchThree.setOnDragDetected(e -> handleDial(e, 135, 3));
        touchThree.setOnMouseDragged(e -> handleDial(e, 135, 3));
        touchThree.setOnMouseReleased(e -> handleDial(e, 135, 3));
        touchFour.setOnDragDetected(e -> handleDial(e, 160, 4));
        touchFour.setOnMouseDragged(e -> handleDial(e, 160, 4));
        touchFour.setOnMouseReleased(e -> handleDial(e, 160, 4));
        touchFive.setOnDragDetected(e -> handleDial(e, 185, 5));
        touchFive.setOnMouseDragged(e -> handleDial(e, 185, 5));
        touchFive.setOnMouseReleased(e -> handleDial(e, 185, 5));
        touchSix.setOnDragDetected(e -> handleDial(e, 210, 6));
        touchSix.setOnMouseDragged(e -> handleDial(e, 210, 6));
        touchSix.setOnMouseReleased(e -> handleDial(e, 210, 6));
        touchSeven.setOnDragDetected(e -> handleDial(e, 235, 7));
        touchSeven.setOnMouseDragged(e -> handleDial(e, 235, 7));
        touchSeven.setOnMouseReleased(e -> handleDial(e, 235, 7));
        touchEight.setOnDragDetected(e -> handleDial(e, 260, 8));
        touchEight.setOnMouseDragged(e -> handleDial(e, 260, 8));
        touchEight.setOnMouseReleased(e -> handleDial(e, 260, 8));
        touchNine.setOnDragDetected(e -> handleDial(e, 285, 9));
        touchNine.setOnMouseDragged(e -> handleDial(e, 285, 9));
        touchNine.setOnMouseReleased(e -> handleDial(e, 285, 9));
        touchZero.setOnDragDetected(e -> handleDial(e, 310, 0));
        touchZero.setOnMouseDragged(e -> handleDial(e, 310, 0));
        touchZero.setOnMouseReleased(e -> handleDial(e, 310, 0));
    }


    // ******************** Methods *******************************************
    private Circle createCircle() {
        Circle circle = new Circle();
        circle.getStyleClass().add("touch");
        return circle;
    }

    private Text createText(final String TEXT) {
        Text text = new Text(TEXT);
        text.setTextOrigin(VPos.CENTER);
        text.setMouseTransparent(true);
        text.getStyleClass().setAll("text");
        text.setFont(Fonts.din(48));
        return text;
    }

    private void handleDial(final MouseEvent EVENT, final double MAX_ANGLE, final int NUMBER) {
        final EventType TYPE = EVENT.getEventType();

        if (MouseEvent.MOUSE_DRAGGED == TYPE) {
            Point2D point = sceneToLocal(EVENT.getSceneX(), EVENT.getSceneY());
            touchRotate(point.getX(), point.getY(), MAX_ANGLE);
        } else if (MouseEvent.MOUSE_RELEASED == TYPE) {
            KeyValue kv0 = new KeyValue(plateRotate.angleProperty(), currentAngle, Interpolator.EASE_BOTH);
            KeyValue kv1 = new KeyValue(plateRotate.angleProperty(), 0, Interpolator.EASE_BOTH);
            KeyFrame kf0 = new KeyFrame(Duration.ZERO, kv0);
            KeyFrame kf1 = new KeyFrame(Duration.millis(2 * MAX_ANGLE), kv1);
            timeline.getKeyFrames().setAll(kf0, kf1);
            timeline.play();
            timeline.setOnFinished(e -> {
                currentAngle = 0;
                plateRotate.setAngle(currentAngle);
                fireEvent(new DialEvent(DialEvent.NUMBER_DIALED, NUMBER));
            });
        }
    }

    private double getTheta(double x, double y) {
        double deltaX = x - (width * 0.5);
        double deltaY = y - (height * 0.5);
        double radius = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
        double nx     = deltaX / radius;
        double ny     = deltaY / radius;
        double theta  = Math.atan2(ny, nx);
        return Double.compare(theta, 0.0) >= 0 ? Math.toDegrees(theta) : Math.toDegrees((theta)) + 360.0;
    }

    private void touchRotate(final double X, final double Y, final double MAX_ANGLE) {
        double oldTheta = theta;
        theta = getTheta(X, Y) + MAX_ANGLE - 5;
        //if (Double.compare(theta, oldTheta) < 0) {
        //    currentAngle = 0;
        //} else {
            currentAngle = theta % 360 > MAX_ANGLE ? MAX_ANGLE : theta % 360;
        //}
        plateRotate.setAngle(currentAngle);
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size = width < height ? width : height;

        if (width > 0 && height > 0) {
            pane.setMaxSize(size, size);
            pane.setPrefSize(size, size);
            pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

            background.setPrefSize(size, size);

            double radius = 0.07804878048780488 * size;

            touchOne.setCenterX(0.62134146 * size);
            touchOne.setCenterY(0.13658537 * size);
            touchOne.setRadius(radius);

            touchTwo.setCenterX(0.45661707 * size);
            touchTwo.setCenterY(0.11916829 * size);
            touchTwo.setRadius(radius);

            touchThree.setCenterX(0.29996341 * size);
            touchThree.setCenterY(0.17299756 * size);
            touchThree.setRadius(radius);

            touchFour.setCenterX(0.18073659 * size);
            touchFour.setCenterY(0.2879878 * size);
            touchFour.setRadius(radius);

            touchFive.setCenterX(0.12127805 * size);
            touchFive.setCenterY(0.44259268 * size);
            touchFive.setRadius(radius);

            touchSix.setCenterX(0.13272927 * size);
            touchSix.setCenterY(0.60783902 * size);
            touchSix.setRadius(radius);

            touchSeven.setCenterX(0.2129439 * size);
            touchSeven.setCenterY(0.75276585 * size);
            touchSeven.setRadius(radius);

            touchEight.setCenterX(0.34689024 * size);
            touchEight.setCenterY(0.8502122 * size);
            touchEight.setRadius(radius);

            touchNine.setCenterX(0.50947073 * size);
            touchNine.setCenterY(0.88192195 * size);
            touchNine.setRadius(radius);

            touchZero.setCenterX(0.67021951 * size);
            touchZero.setCenterY(0.84194878 * size);
            touchZero.setRadius(radius);

            Font font = Fonts.din(size * 0.11707317);

            one.setFont(font);
            one.setTranslateX(0.595 * size);
            one.setTranslateY(0.15 * size);

            two.setFont(font);
            two.setTranslateX(0.42926829 * size);
            two.setTranslateY(0.135 * size);

            three.setFont(font);
            three.setTranslateX(0.275 * size);
            three.setTranslateY(0.19 * size);

            four.setFont(font);
            four.setTranslateX(0.15 * size);
            four.setTranslateY(0.305 * size);

            five.setFont(font);
            five.setTranslateX(0.091 * size);
            five.setTranslateY(0.461 * size);

            six.setFont(font);
            six.setTranslateX(0.10243902 * size);
            six.setTranslateY(0.625 * size);

            seven.setFont(font);
            seven.setTranslateX(0.185 * size);
            seven.setTranslateY(0.775 * size);

            eight.setFont(font);
            eight.setTranslateX(0.32 * size);
            eight.setTranslateY(0.87 * size);

            nine.setFont(font);
            nine.setTranslateX(0.48 * size);
            nine.setTranslateY(0.90 * size);

            zero.setFont(font);
            zero.setTranslateX(0.64 * size);
            zero.setTranslateY(0.86 * size);

            plate.setPrefSize(size, size);
            plateInnerShadow0.setRadius(2.0 / PREFERRED_WIDTH * size);
            plateInnerShadow1.setRadius(2.0 / PREFERRED_WIDTH * size);
            plateDropShadow2.setRadius(3.0 / PREFERRED_WIDTH * size);

            plateRotate.setPivotX(size * 0.5);
            plateRotate.setPivotY(size * 0.5);

            post.setPrefSize(0.21599582579077745 * size, 0.23934043326028964 * size);
            post.setTranslateX(0.7406503072599085 * size);
            post.setTranslateY(0.5658536585365853 * size);

            postInnerShadow0.setRadius(3.0 / PREFERRED_WIDTH * size);
            postInnerShadow1.setRadius(3.0 / PREFERRED_WIDTH * size);
            postDropShadow2.setRadius(4.0 / PREFERRED_WIDTH * size);
        }
    }
}
