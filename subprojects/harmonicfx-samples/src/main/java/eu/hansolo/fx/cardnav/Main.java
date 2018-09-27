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
package eu.hansolo.fx.cardnav;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.kordamp.ikonli.material.Material;


/**
 * User: hansolo
 * Date: 21.08.16
 * Time: 08:23
 */
public class Main extends Application {
    private Card    card0;
    private Card    card1;
    private Card    card2;
    private Card    card3;
    private CardBox cardBox;


    @Override public void init() {
        card0 = new Card("Private");
        card0.getStyleClass().add("card0");

        card1 = new Card("Office");
        card1.getStyleClass().add("card1");

        card2 = new Card("Family");
        card2.getStyleClass().add("card2");

        card3 = new Card("Shopping");
        card3.getStyleClass().add("card3");

        cardBox = new CardBox(card0, card1, card2, card3);
        cardBox.setMenuIkon(Material.MENU);
        cardBox.setCloseIkon(Material.CLOSE);
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(cardBox);

        Scene scene = new Scene(pane);
        scene.getStylesheets().add(Main.class.getResource("styles.css").toExternalForm());

        stage.setTitle("Card Navigation");
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
