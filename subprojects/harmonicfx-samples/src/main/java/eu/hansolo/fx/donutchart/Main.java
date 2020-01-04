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
package eu.hansolo.fx.donutchart;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;

import java.util.Random;


/**
 * User: hansolo
 * Date: 17.02.17
 * Time: 15:31
 */
public class Main extends Application {
    private static final Random RND = new Random();
    private DonutChart     graph;
    private long           lastTimerCall;
    private AnimationTimer timer;
    private ChartData      chartData1;
    private ChartData      chartData2;
    private ChartData      chartData3;
    private ChartData      chartData4;


    @Override public void init() {
        chartData1 = new ChartData("Switzerland", 24.0, Color.LIGHTBLUE);
        chartData2 = new ChartData("Germany", 10.0, Color.LIME);
        chartData3 = new ChartData("Belgium", 12.0, Color.CRIMSON);
        chartData4 = new ChartData("Singapore", 13.0, Color.MAGENTA);

        graph      = new DonutChart(chartData1, chartData2, chartData3, chartData4);
        graph.setBackground(new Background(new BackgroundFill(Color.rgb(90, 90, 90), CornerRadii.EMPTY, Insets.EMPTY)));
        graph.setBarBorderColor(Color.rgb(255, 255, 255, 0.1));
        graph.setTextColor(Color.WHITE);

        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + 3_000_000_000l) {
                    chartData1.setValue(RND.nextDouble() * 30);
                    chartData2.setValue(RND.nextDouble() * 30);
                    chartData3.setValue(RND.nextDouble() * 30);
                    chartData4.setValue(RND.nextDouble() * 30);
                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(graph);
        pane.setPadding(new Insets(10));
        pane.setBackground(new Background(new BackgroundFill(Color.rgb(40, 40, 40), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane);

        stage.setTitle("Donut Chart");
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
