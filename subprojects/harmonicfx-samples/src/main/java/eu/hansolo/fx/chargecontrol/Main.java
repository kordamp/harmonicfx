/*
 * Copyright 2014-2016 Gerrit Grunwald.
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
package eu.hansolo.fx.chargecontrol;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Random;


/**
 * User: hansolo
 * Date: 07.07.16
 * Time: 10:11
 */
public class Main extends Application {
    private static final Random         RND = new Random();
    private              ChargeControl  control;
    private              long           lastTimerCall;
    private              AnimationTimer timer;


    @Override public void init() {
        control = new ChargeControl();

        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(long now) {
                if (now > lastTimerCall + 2_000_000_000) {
                    control.setValue(RND.nextDouble());
                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(control);
        pane.setPadding(new Insets(20));

        Scene scene = new Scene(pane);

        stage.setTitle("ChargeControl");
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
