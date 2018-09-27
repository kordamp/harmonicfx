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
package eu.hansolo.fx.segment;

import eu.hansolo.fx.segment.Segment.Character;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Random;


/**
 * User: hansolo
 * Date: 19.05.16
 * Time: 19:08
 */
public class Main extends Application {
    private static final Random RND = new Random();
    private int            counter;
    private boolean        toggle;
    private HBox           row0;
    private Segment        r00;
    private Segment        r01;
    private Segment        r02;
    private HBox           row1;
    private Segment        r10;
    private Segment        r11;
    private Segment        r12;
    private HBox           row2;
    private Segment        r20;
    private Segment        r21;
    private Segment        r22;
    private HBox           row3;
    private Segment        r30;
    private Segment        r31;
    private Segment        r32;
    private HBox           text;
    private Segment        t0;
    private Segment        t1;
    private Segment        t2;
    private Segment        t3;
    private Segment        t4;
    private Segment        segment;
    private long           lastTimerCall;
    private AnimationTimer timer;


    @Override public void init() {
        r00     = new Segment(Character.C4);
        r01     = new Segment(Character.C7);
        r02     = new Segment(Character.C1);
        row0     = new HBox(r00, r01, r02);
        row0.setSpacing(5);

        r10     = new Segment(Character.C1);
        r11     = new Segment(Character.C3);
        r12     = new Segment(Character.C1);
        row1    = new HBox(r10, r11, r12);
        row1.setSpacing(5);

        r20     = new Segment(Character.C1);
        r21     = new Segment(Character.C5);
        r22     = new Segment(Character.C8);
        row2    = new HBox(r20, r21, r22);
        row2.setSpacing(5);

        r30     = new Segment(Character.C2);
        r31     = new Segment(Character.C1);
        r32     = new Segment(Character.C0);
        row3    = new HBox(r30, r31, r32);
        row3.setSpacing(5);

        t0 = new Segment(Character.CJ);
        t1 = new Segment(Character.CA);
        t2 = new Segment(Character.CV);
        t3 = new Segment(Character.CA);
        t4 = new Segment(Character.EMPTY);
        text = new HBox(t0, t1, t2, t3, t4);
        text.setSpacing(5);

        segment = new Segment(Character.EMPTY);

        counter       = 0;
        lastTimerCall = System.nanoTime();
        timer         = new AnimationTimer() {
            @Override public void handle(long now) {
                if (now > lastTimerCall + 1_000_000_000l) {
                    toggle ^= true;
                    r00.setCharacter(Segment.Character.values()[RND.nextInt(11)]);
                    r01.setCharacter(Segment.Character.values()[RND.nextInt(11)]);
                    r02.setCharacter(Segment.Character.values()[RND.nextInt(11)]);
                    r10.setCharacter(Segment.Character.values()[RND.nextInt(11)]);
                    r11.setCharacter(Segment.Character.values()[RND.nextInt(11)]);
                    r12.setCharacter(Segment.Character.values()[RND.nextInt(11)]);
                    r20.setCharacter(Segment.Character.values()[RND.nextInt(11)]);
                    r21.setCharacter(Segment.Character.values()[RND.nextInt(11)]);
                    r22.setCharacter(Segment.Character.values()[RND.nextInt(11)]);
                    r30.setCharacter(Segment.Character.values()[RND.nextInt(11)]);
                    r31.setCharacter(Segment.Character.values()[RND.nextInt(11)]);
                    r32.setCharacter(Segment.Character.values()[RND.nextInt(11)]);

                    t0.setCharacter(toggle ? Character.CJ    : Character.CR);
                    t1.setCharacter(toggle ? Character.CA    : Character.CO);
                    t2.setCharacter(toggle ? Character.CV    : Character.CC);
                    t3.setCharacter(toggle ? Character.CA    : Character.CK);
                    t4.setCharacter(toggle ? Character.EMPTY : Character.CS);

                    counter++;
                    if (counter > Character.values().length - 1) counter = 0;
                    segment.setCharacter(Segment.Character.values()[counter]);

                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        VBox vbox = new VBox(row0, row1, row2, row3, text, segment);
        vbox.setSpacing(20);

        StackPane pane = new StackPane(vbox);
        pane.setPadding(new Insets(10));
        pane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane);

        stage.setTitle("Friday Fun XXX");
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
