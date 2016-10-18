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
package eu.hansolo.fx.submitbutton;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome.FontAwesome;


/**
 * User: hansolo
 * Date: 22.08.16
 * Time: 08:23
 */
public class Main extends Application {
    private SubmitButton   button;
    private double         progress;
    private long           lastTimerCall;
    private boolean        toggle;
    private AnimationTimer timer;

    @Override public void init() {
        button = new SubmitButton();
        button.setSuccessIkon(FontAwesome.CHECK);
        button.setFailIkon(FontAwesome.CLOSE);
        button.setColor(Color.web("#34495e"));
        button.setOnMousePressed(e -> timer.start());
        button.statusProperty().addListener(o -> System.out.println(button.getStatus()));

        progress      = 0;
        lastTimerCall = System.nanoTime();
        timer         = new AnimationTimer() {
            @Override public void handle(long now) {
                if (now > lastTimerCall + 50_000_000l) {
                    progress += 0.005;
                    button.setProgress(progress);
                    lastTimerCall = now;
                    if (toggle) {
                        if (progress > 0.75) {
                            progress = 0;
                            button.setFailed();
                            timer.stop();
                            toggle ^= true;
                        }
                    } else {
                        if (progress > 1) {
                            progress = 0;
                            timer.stop();
                            toggle ^= true;
                        }
                    }
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(button);
        pane.setPadding(new Insets(20));

        Scene scene = new Scene(pane);

        stage.setTitle("Submit Button");
        stage.setScene(scene);
        stage.show();
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
