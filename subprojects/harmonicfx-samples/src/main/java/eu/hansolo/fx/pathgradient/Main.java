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
package eu.hansolo.fx.pathgradient;

import eu.hansolo.fx.pathgradient.shapes.Circle;
import eu.hansolo.fx.pathgradient.shapes.Path;
import eu.hansolo.fx.pathgradient.tools.GradientLookup;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;


/**
 * User: hansolo
 * Date: 24.11.17
 * Time: 17:27
 */
public class Main extends Application {
    private GradientLookup  gradientLookup;
    private Path            path;
    private Canvas          canvas;
    private GraphicsContext ctx;
    private double          radius;

    @Override public void init() {
        radius = 100;

        gradientLookup = new GradientLookup();
        gradientLookup.setStops(new Stop(0.0, Color.BLUE),
                                new Stop(0.25, Color.LIME),
                                new Stop(0.5, Color.YELLOW),
                                new Stop(0.75, Color.ORANGE),
                                new Stop(1.0, Color.RED));

        path = new Path();
        /*
        path.moveTo(91, 36);
        path.lineTo(182, 124);
        path.bezierCurveTo(248, 191, 92, 214, 92, 214);
        path.bezierCurveTo(-26, 248, 200, 323, 200, 323);
        path.bezierCurveTo(303, 355, 383, 141, 383, 141);
        */
        path.appendSVGPath("M 91 36 L 182 124 C 248 191 92 214 92 214 " +
                                     "C -26 248 200 323 200 323 C 303 355 383 141 383 141");

        canvas = new Canvas(400, 400);
        ctx    = canvas.getGraphicsContext2D();

        PathGradient.strokePathWithGradient(ctx, path, gradientLookup, 20, StrokeLineCap.ROUND);
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(canvas);

        Scene scene = new Scene(pane);

        stage.setTitle("Title");
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
