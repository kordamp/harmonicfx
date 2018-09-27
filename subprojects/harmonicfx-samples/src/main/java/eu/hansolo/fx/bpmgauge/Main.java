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
package eu.hansolo.fx.bpmgauge;

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
 * Date: 10.05.16
 * Time: 19:56
 */
public class Main extends Application {
    private static final Random         RND = new Random();
    private              BpmGauge       control;
    private              long           lastTimerCall;
    private              AnimationTimer timer;


    @Override public void init() {
        control       = new BpmGauge(47);
        control.setName("HarmonicFX");
        lastTimerCall = System.nanoTime();
        timer         = new AnimationTimer() {
            @Override public void handle(long now) {
                if (now > lastTimerCall + 5_000_000_000l) {
                    control.setBpm(RND.nextInt(160) + 40);
                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(control);
        pane.setPadding(new Insets(20));
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(50, 50, 50), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane);

        stage.setTitle("BPM Gauge");
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
