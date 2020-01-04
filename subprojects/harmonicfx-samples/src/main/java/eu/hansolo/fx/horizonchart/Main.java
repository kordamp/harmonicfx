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
package eu.hansolo.fx.horizonchart;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * User: hansolo
 * Date: 11.11.17
 * Time: 09:03
 */
public class Main extends Application {
    private static final Random         RND       = new Random();
    private static       int            noOfNodes = 0;
    private              HorizonChart   chartBand1;
    private              HorizonChart   chartBand2;
    private              HorizonChart   chartBand3;
    private              HorizonChart   chartBand4;
    private              long           lastTimerCall;
    private              AnimationTimer timer;


    @Override public void init() {
        /*
        // Convert a list of JavaFX XYChart.Data<X, Y extends NumberY items to a List<Data>
        List<XYChart.Data<String, Number>> xyChartDataList = new ArrayList<>(noOfValues);
        for (int i = 0 ; i < noOfValues ; i++) {
            dList.add(new XYChart.Data<>(Integer.toString(i), Math.abs(Math.cos(i/100.0) + (RND.nextDouble() - 0.5) / 10.0)));
        }
        List<Data> convertedList = Helper.convertFromXYChartData(xyChartDataList);
        */

        // Generate some random data
        int        noOfValues = 1500;
        List<Data> dataList   = new ArrayList<>(noOfValues);
        for (int i = 0 ; i < noOfValues; i++) {
            double value = Math.abs(Math.cos(i/100.0) + (RND.nextDouble() - 0.5) / 10.0); // Only positive data
            dataList.add(new Data("P" + i, value));
        }

        // Create a series of the generated data
        Series series = new Series(dataList, "My data series");


        // Add a listener to the series to get info about selected data
        series.setOnSeriesEvent(e -> {
            if (SeriesEventType.SELECT_DATA == e.getType()) {
                System.out.println(e.getSeries().getName() + " -> " + e.getData().toString());
            }
        });


        // Just a bottom border to separate the charts visually
        Border bottomBorder = new Border(new BorderStroke(Color.TRANSPARENT, Color.TRANSPARENT, Color.BLACK, Color.TRANSPARENT,
                                                          BorderStrokeStyle.NONE, BorderStrokeStyle. NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
                                                          CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0), Insets.EMPTY));


        // Color definitions for positive colors
        Color[] positiveColors = { Color.web("#FEE090"), Color.web("#FDAE61"), Color.web("#F46D43"), Color.web("#D73027") };


        // Create Horizon chart with 1 band for our series
        chartBand1 = new HorizonChart(1, series);
        chartBand1.setPrefSize(800, 120);
        chartBand1.setPositiveColors(positiveColors[0], positiveColors[1]);
        chartBand1.setBorder(bottomBorder);


        // Create Horizon chart with 2 bands for our series
        chartBand2 = new HorizonChart(2, series);
        chartBand2.setPositiveColors(positiveColors[0], positiveColors[1]);
        chartBand2.setPrefSize(800, 60);
        chartBand2.setBorder(bottomBorder);


        // Create Horizon chart with 3 bands for our series
        chartBand3 = new HorizonChart(3, series);
        chartBand3.setPositiveColors(positiveColors[0], positiveColors[2]);
        chartBand3.setPrefSize(800, 40);
        chartBand3.setBorder(bottomBorder);


        // Create Horizon chart with 4 bands for our series
        chartBand4 = new HorizonChart(4, series);
        chartBand4.setPositiveColors(positiveColors[0], positiveColors[3]);
        chartBand4.setPrefSize(800, 30);
        chartBand4.setBorder(bottomBorder);


        // Update the charts every 5 seconds
        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + 5_000_000_000l) {
                    for (int i = 0 ; i < noOfValues; i++) {
                        double value = Math.abs(Math.cos(i/100.0) + (RND.nextDouble() - 0.5) / 10.0); // Only positive data
                        dataList.get(i).setY(value);
                    }
                    // Redraw all charts
                    series.fireSeriesEvent(new SeriesEvent(null, SeriesEventType.REDRAW));

                    // Redraw a single chart
                    //chartBand1.redraw();

                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        // Arrange all four charts in a GridPane
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(10));
        pane.add(new Label("1 band (120px)"), 0, 0);
        pane.add(chartBand1, 1, 0);
        pane.add(new Label("2 band (60px)"), 0, 1);
        pane.add(chartBand2, 1, 1);
        pane.add(new Label("3 band (40px)"), 0, 2);
        pane.add(chartBand3, 1, 2);
        pane.add(new Label("4 band (30px)"), 0, 3);
        pane.add(chartBand4, 1, 3);

        Scene scene = new Scene(pane);

        stage.setTitle("Horizon Chart");
        stage.setScene(scene);
        stage.show();

        // Calculate number of nodes
        calcNoOfNodes(pane);
        System.out.println(noOfNodes + " Nodes in SceneGraph");

        timer.start();
    }

    @Override public void stop() {
        System.exit(0);
    }


    // ******************** Misc **********************************************
    private static void calcNoOfNodes(Node node) {
        if (node instanceof Parent) {
            if (((Parent) node).getChildrenUnmodifiable().size() != 0) {
                ObservableList<Node> tempChildren = ((Parent) node).getChildrenUnmodifiable();
                noOfNodes += tempChildren.size();
                for (Node n : tempChildren) { calcNoOfNodes(n); }
            }
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
