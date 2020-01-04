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
package eu.hansolo.fx.nestedbarchart;

import eu.hansolo.fx.nestedbarchart.series.Series;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;

import static eu.hansolo.fx.nestedbarchart.MaterialDesignColor.*;


/**
 * User: hansolo
 * Date: 28.12.17
 * Time: 15:17
 */
public class Main extends Application {
    private NestedBarChart chart;

    @Override public void init() {
        Item p1Q1 = new Item(16, "Product 1", CYAN_700.get());
        Item p2Q1 = new Item(8, "Product 2", CYAN_500.get());
        Item p3Q1 = new Item(4, "Product 3", CYAN_300.get());
        Item p4Q1 = new Item(2, "Product 4", CYAN_100.get());

        Item p1Q2 = new Item(12, "Product 1", PURPLE_700.get());
        Item p2Q2 = new Item(5, "Product 2", PURPLE_500.get());
        Item p3Q2 = new Item(3, "Product 3", PURPLE_300.get());
        Item p4Q2 = new Item(1, "Product 4", PURPLE_100.get());

        Item p1Q3 = new Item(14, "Product 1", PINK_700.get());
        Item p2Q3 = new Item(7, "Product 2", PINK_500.get());
        Item p3Q3 = new Item(3.5, "Product 3", PINK_300.get());
        Item p4Q3 = new Item(1.75, "Product 4", PINK_100.get());

        Item p1Q4 = new Item(18, "Product 1", AMBER_700.get());
        Item p2Q4 = new Item(9, "Product 2", AMBER_500.get());
        Item p3Q4 = new Item(4.5, "Product 3", AMBER_300.get());
        Item p4Q4 = new Item(2.25, "Product 4", AMBER_100.get());

        Series<Item> q1 = new Series<>("1st Quarter", Color.TRANSPARENT, CYAN_900.get(), p1Q1, p2Q1, p3Q1, p4Q1);
        Series<Item> q2 = new Series<>("2nd Quarter", Color.TRANSPARENT, PURPLE_900.get(), p1Q2, p2Q2, p3Q2, p4Q2);
        Series<Item> q3 = new Series<>("3rd Quarter", Color.TRANSPARENT, PINK_900.get(), p1Q3, p2Q3, p3Q3, p4Q3);
        Series<Item> q4 = new Series<>("4th Quarter", Color.TRANSPARENT, AMBER_900.get(), p1Q4, p2Q4, p3Q4, p4Q4);


        chart = new NestedBarChart(q1, q2, q3, q4);

        chart.setOnSelectionEvent(e -> System.out.println(e));
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(chart);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("Nested Bar Chart");
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
