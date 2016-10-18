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
package eu.hansolo.fx.colorselector;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;


/**
 * User: hansolo
 * Date: 29.02.16
 * Time: 08:29
 */
public class Main extends Application {

    private ColorSelector colorSelector;

    @Override public void init() {
        Stop[] stops = { new Stop(0.0, Color.rgb(255,255,0)),
                          new Stop(0.125, Color.rgb(255,0,0)),
                          new Stop(0.375, Color.rgb(255,0,255)),
                          new Stop(0.5, Color.rgb(0,0,255)),
                          new Stop(0.625, Color.rgb(0,255,255)),
                          new Stop(0.875, Color.rgb(0,255,0)),
                          new Stop(1.0, Color.rgb(255,255,0)) };

        colorSelector = new ColorSelector(stops);
        colorSelector.setPrefSize(400, 400);

        colorSelector.selectedColorProperty().addListener(o -> System.out.println(colorSelector.getSelectedColor()));
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(colorSelector);
        pane.setPadding(new Insets(10));
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(31, 31, 31), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane);

        stage.setTitle("Color Selector");
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
