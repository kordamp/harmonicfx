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

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Random;

/**
 * User: hansolo
 * Date: 04.04.16
 * Time: 10:46
 */
public class Main extends Application {
    private static final Random RND = new Random();
    private FitnessGauge   gauge;
    private long           lastTimerCall;
    private AnimationTimer timer;


    @Override public void init() {
        gauge = FitnessGaugeBuilder.create()
                                   .outerText("MOVE")
                                   .middleText("EXCERISE")
                                   .innerText("STAND")
                                   //.outerTextVisible(false)
                                   //.middleTextVisible(false)
                                   //.innerTextVisible(false)
                                   //.outerValue(125)
                                   //.middleValue(100)
                                   //.innerValue(100)
                                   .middleColor(Color.rgb(220, 200, 0))
                                   .build();

        lastTimerCall = System.nanoTime();
        timer         = new AnimationTimer() {
            @Override public void handle(long now) {
                if (now > lastTimerCall + 2_000_000_000l) {
                    gauge.setOuterValue(RND.nextInt(150));
                    gauge.setMiddleValue(RND.nextInt(150));
                    gauge.setInnerValue(RND.nextInt(150));
                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(gauge);
        pane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane);

        stage.setTitle("Fitness Gauge");
        stage.setScene(scene);
        stage.show();

        timer.start();
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
