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
package eu.hansolo.fx.slidecheckbox;

import eu.hansolo.fx.slidecheckbox.SlideCheckBox;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


/**
 * Created by
 * User: hansolo
 * Date: 30.08.13
 * Time: 15:32
 */

public class Main extends Application {

    @Override public void start(Stage stage) {
        SlideCheckBox checkBox = new SlideCheckBox();
        //checkBox.setScaleX(0.5);
        //checkBox.setScaleY(0.5);

        StackPane pane = new StackPane();
        pane.getChildren().addAll(checkBox);

        Scene scene = new Scene(pane, 200, 100);

        stage.setScene(scene);
        stage.setTitle("JavaFX FridayFun XVIII");
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
