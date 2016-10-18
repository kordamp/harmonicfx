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
package eu.hansolo.fx.weather;

import eu.hansolo.fx.weather.darksky.DarkSky.Language;
import eu.hansolo.fx.weather.darksky.DarkSky.Unit;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.UnsupportedEncodingException;


/**
 * User: hansolo
 * Date: 04.10.16
 * Time: 05:35
 */
public class Main extends Application {
    private static final Point2D HOME = new Point2D(7.632815, 51.911858);
    private WeatherPanel weatherPanel;


    @Override public void init() {
        if (!ApiKeys.DARK_SKY_API_KEY.isPresent() || !ApiKeys.MAPQUEST_API_KEY.isPresent()) throw new IllegalArgumentException("Please provide Dark Sky and Mapquest API Key");
        Point2D home = new Point2D(0, 0);
        try {
            home = GeoCode.geoCode("Westfalenstrasse 93, 48165 Münster, Germany"); // Get latitude and longitude by geo coding
        } catch (UnsupportedEncodingException ex) {
            System.exit(0);
        }

        Location location = new Location(HOME.getY(), HOME.getX(), "HILTRUP");
        // Location location = new Location(HOME.getY(), HOME.getX()); // Will get the city name by reverse geo coding given latitude and longitude
        // Location location = new Location(home.getY(), home.getX()); // Use latitude and longitude from geo coded street address
        location.setUnit(Unit.CA);
        location.setLanguage(Language.GERMAN);

        weatherPanel = new WeatherPanel(location);
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane(weatherPanel);

        Scene scene = new Scene(pane);

        stage.setTitle("WeatherFX");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
