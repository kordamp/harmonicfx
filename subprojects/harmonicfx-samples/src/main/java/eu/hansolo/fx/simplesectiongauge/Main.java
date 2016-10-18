/*
 * Copyright 2013-2016 Gerrit Grunwald.
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
package eu.hansolo.fx.simplesectiongauge;

import eu.hansolo.medusa.Section;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Random;


/**
 * User: hansolo
 * Date: 25.07.16
 * Time: 08:33
 */
public class Main extends Application {
    private static final Random RND = new Random();
    private SimpleSectionGauge gauge;
    private long               lastTimerCall;
    private AnimationTimer     timer;

    @Override public void init() {
        gauge = new SimpleSectionGauge("Title", "unit",
                                       0, 100,
                                       Color.rgb(69, 106, 207), Color.rgb(150, 150, 150, 0.25),
                                       true,
                                       new Section(0, 33, Color.rgb(69, 207, 109)), new Section(33, 66, Color.rgb(239, 215, 80)), new Section(66, 100, Color.rgb(239, 96, 80)));
        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(long now) {
                if (now > lastTimerCall + 2_000_000_000) {
                    gauge.setValue(RND.nextDouble() * 100);
                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(gauge);
        pane.setPadding(new Insets(20));

        Scene scene = new Scene(pane);

        stage.setTitle("SimpleSectionGauge");
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
