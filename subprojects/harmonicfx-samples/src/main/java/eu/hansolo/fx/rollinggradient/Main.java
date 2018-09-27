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
package eu.hansolo.fx.rollinggradient;

import eu.hansolo.fx.rollinggradient.RollingGradient.Direction;
import eu.hansolo.fx.rollinggradient.RollingGradient.Speed;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;


/**
 * User: hansolo
 * Date: 20.07.18
 * Time: 13:10
 */
public class Main extends Application {
    private Rectangle       rect;
    private RollingGradient gradient;


    @Override public void init() {
        rect     = new Rectangle(200, 20);
        gradient = RollingGradientBuilder.create()
                                         //.firstColor(Color.rgb(240, 0, 0))
                                         //.secondColor(Color.rgb(200, 0, 0))
                                         .smoothGradient(true)
                                         .direction(Direction.LEFT)
                                         .interval(Speed.NORMAL.getInterval())
                                         .shape(rect)
                                         //.period(5)
                                         //.onUpdateEvent(e -> rect.setFill(e.getGradient()))
                                         .start()
                                         .build();


        /*
        gradient = new RollingGradient(Color.rgb(200, 0, 0), Color.rgb(255, 0, 0));
        gradient.setFirstColor(Color.rgb(88, 154, 227));
        gradient.setSecondColor(Color.rgb(50, 130, 222));
        gradient.setSmoothGradient(true);
        gradient.setDirection(Direction.LEFT);
        gradient.setInterval(Speed.NORMAL);
        gradient.setShape(rect);
        gradient.setOnUpdateEvent(e -> rect.setFill(e.getGradient()));
        gradient.start();
        */
    }


    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(rect);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("RollingGradient");
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
