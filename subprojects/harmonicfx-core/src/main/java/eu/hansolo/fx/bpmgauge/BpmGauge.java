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
package eu.hansolo.fx.bpmgauge;

import eu.hansolo.medusa.Fonts;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

import java.util.Locale;


/**
 * User: hansolo
 * Date: 10.05.16
 * Time: 19:55
 */
public class BpmGauge extends Region {
    public enum Zone {
        UNKNOWN("UNKNOWN"),
        VERY_LIGHT("VERY LIGHT"),
        LIGHT("LIGHT"),
        MODERATE("MODERATE"),
        HARD("HARD"),
        MAX("MAX");

        public final String TEXT;

        Zone(final String TEXT) {
            this.TEXT = TEXT;
        }
    }
    private static final PseudoClass   ZONE_1_PSEUDO_CLASS  = PseudoClass.getPseudoClass("zone-1");
    private static final PseudoClass   ZONE_2_PSEUDO_CLASS  = PseudoClass.getPseudoClass("zone-2");
    private static final PseudoClass   ZONE_3_PSEUDO_CLASS  = PseudoClass.getPseudoClass("zone-3");
    private static final PseudoClass   ZONE_4_PSEUDO_CLASS  = PseudoClass.getPseudoClass("zone-4");
    private static final PseudoClass   ZONE_5_PSEUDO_CLASS  = PseudoClass.getPseudoClass("zone-5");
    private static final PseudoClass   WARNING_PSEUDO_CLASS = PseudoClass.getPseudoClass("warning");
    private static final double        PREFERRED_WIDTH      = 184;
    private static final double        PREFERRED_HEIGHT     = 106;
    private static final double        MINIMUM_WIDTH        = 18;
    private static final double        MINIMUM_HEIGHT       = 10;
    private static final double        MAXIMUM_WIDTH        = 1840;
    private static final double        MAXIMUM_HEIGHT       = 1060;
    // View related
    private static double              aspectRatio;
    private        double              width;
    private        double              height;
    private        Region              bpmArea;
    private        Region              avgArea;
    private        Text                percentageText;
    private        Text                percentageTitle;
    private        Text                bpmText;
    private        Text                bpmTitle;
    private        Text                nameText;
    private        Text                zoneText;
    private        Segment             zone1Segment;
    private        Segment             zone2Segment;
    private        Segment             zone3Segment;
    private        Segment             zone4Segment;
    private        Segment             zone5Segment;
    private        HBox                segmentBox;
    private        Pane                pane;
    private        long                lastTimerCall;
    private        AnimationTimer      timer;
    // Model related
    private        double              age;
    private        double              maxBpm;
    private        double              percentage;
    private        Zone                zone;
    private        BooleanProperty     zone1;
    private        BooleanProperty     zone2;
    private        BooleanProperty     zone3;
    private        BooleanProperty     zone4;
    private        BooleanProperty     zone5;
    private        BooleanProperty     warning;
    private        Gauge               gauge;


    // ******************** Constructors **************************************
    public BpmGauge() {
        this(35);
    }
    public BpmGauge(final int AGE) {
        getStylesheets().add(BpmGauge.class.getResource("styles.css").toExternalForm());
        getStyleClass().add("bpm-gauge");
        aspectRatio   = PREFERRED_HEIGHT / PREFERRED_WIDTH;
        age           = clamp(12, 110, AGE);
        maxBpm        = (207 - 0.7 * (age));
        zone          = Zone.VERY_LIGHT;
        zone1         = new BooleanPropertyBase(false) {
            @Override protected void invalidated() { pseudoClassStateChanged(ZONE_1_PSEUDO_CLASS, get()); }
            @Override public Object getBean() { return BpmGauge.this; }
            @Override public String getName() { return "zone1"; }
        };
        zone2         = new BooleanPropertyBase(false) {
            @Override protected void invalidated() { pseudoClassStateChanged(ZONE_2_PSEUDO_CLASS, get()); }
            @Override public Object getBean() { return BpmGauge.this; }
            @Override public String getName() { return "zone2"; }
        };
        zone3         = new BooleanPropertyBase(false) {
            @Override protected void invalidated() { pseudoClassStateChanged(ZONE_3_PSEUDO_CLASS, get()); }
            @Override public Object getBean() { return BpmGauge.this; }
            @Override public String getName() { return "zone3"; }
        };
        zone4         = new BooleanPropertyBase(false) {
            @Override protected void invalidated() { pseudoClassStateChanged(ZONE_4_PSEUDO_CLASS, get()); }
            @Override public Object getBean() { return BpmGauge.this; }
            @Override public String getName() { return "zone4"; }
        };
        zone5         = new BooleanPropertyBase(false) {
            @Override protected void invalidated() { pseudoClassStateChanged(ZONE_5_PSEUDO_CLASS, get()); }
            @Override public Object getBean() { return BpmGauge.this; }
            @Override public String getName() { return "zone5"; }
        };
        warning       = new BooleanPropertyBase(false) {
            @Override protected void invalidated() { pseudoClassStateChanged(WARNING_PSEUDO_CLASS, get()); }
            @Override public Object getBean() { return BpmGauge.this; }
            @Override public String getName() { return "warning"; }
        };
        gauge         = GaugeBuilder.create().animated(false).minValue(40).maxValue(210).build();
        lastTimerCall = System.nanoTime();
        timer         = new AnimationTimer() {
            @Override public void handle(long now) {
                if (now > lastTimerCall + 500_000_000l) {
                    warning.set(!warning.get());
                    lastTimerCall = now;
                }
            }
        };

        init();
        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void init() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 ||
            Double.compare(getWidth(), 0.0) <= 0 || Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        if (Double.compare(getMinWidth(), 0.0) <= 0 || Double.compare(getMinHeight(), 0.0) <= 0) {
            setMinSize(MINIMUM_WIDTH, MINIMUM_HEIGHT);
        }

        if (Double.compare(getMaxWidth(), 0.0) <= 0 || Double.compare(getMaxHeight(), 0.0) <= 0) {
            setMaxSize(MAXIMUM_WIDTH, MAXIMUM_HEIGHT);
        }
    }

    private void initGraphics() {
        bpmArea = new Region();
        bpmArea.getStyleClass().setAll("percentage-area");

        avgArea = new Region();
        avgArea.getStyleClass().setAll("bpm-area");

        percentageText = new Text("");
        percentageText.setTextOrigin(VPos.CENTER);
        percentageText.getStyleClass().setAll("percentage-text");

        percentageTitle = new Text("%");
        percentageTitle.setTextOrigin(VPos.CENTER);
        percentageTitle.getStyleClass().setAll("percentage-title");

        bpmText = new Text("");
        bpmText.setTextOrigin(VPos.CENTER);
        bpmText.getStyleClass().setAll("bpm-text");

        bpmTitle = new Text("BPM");
        bpmTitle.setTextOrigin(VPos.CENTER);
        bpmTitle.getStyleClass().setAll("bpm-title");

        nameText = new Text("YOUR NAME");
        nameText.setTextOrigin(VPos.CENTER);
        nameText.getStyleClass().setAll("name-text");

        zoneText = new Text(getZone().TEXT);
        zoneText.setTextOrigin(VPos.CENTER);
        zoneText.getStyleClass().setAll("zone-text");

        zone1Segment = new Segment(true);
        zone1Segment.getStyleClass().add("zone-1");

        zone2Segment = new Segment();
        zone2Segment.getStyleClass().add("zone-2");

        zone3Segment = new Segment();
        zone3Segment.getStyleClass().add("zone-3");

        zone4Segment = new Segment();
        zone4Segment.getStyleClass().add("zone-4");

        zone5Segment = new Segment();
        zone5Segment.getStyleClass().add("zone-5");

        segmentBox = new HBox(zone1Segment, zone2Segment, zone3Segment, zone4Segment, zone5Segment);
        segmentBox.setAlignment(Pos.BOTTOM_CENTER);
        segmentBox.setSpacing(-8);

        pane = new Pane(bpmArea, avgArea, percentageText, percentageTitle, bpmText, bpmTitle, nameText, zoneText, segmentBox);
        pane.getStyleClass().setAll("background");

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        gauge.valueProperty().addListener(o -> checkZones(gauge.getValue()));

        sceneProperty().addListener(o -> {
            if (null == getScene()) return;
            getScene().windowProperty().addListener(o1 -> {
                if (null == getScene().getWindow()) return;
                getScene().getWindow().setOnShown(e -> resize());
            });
        });
    }


    // ******************** Methods *******************************************
    public int getBpm() { return (int) gauge.getValue(); }
    public void setBpm(final int BPM) { gauge.setValue(BPM); }

    public int getAge() { return (int) age; }
    public void setAge(final int AGE) {
        age    = clamp(12, 110, AGE);
        maxBpm = (int) (207 - 0.7 * (age));
    }

    public String getName() { return nameText.getText(); }
    public void setName(final String name) { nameText.setText(name); }

    public Zone getZone() { return zone; }
    private void setZone(final Zone ZONE) {
        zone = ZONE;
        switch(ZONE) {
            case VERY_LIGHT:
                zone1.set(true);
                zone2.set(false);
                zone3.set(false);
                zone4.set(false);
                zone5.set(false);
                break;
            case LIGHT:
                zone1.set(false);
                zone2.set(true);
                zone3.set(false);
                zone4.set(false);
                zone5.set(false);
                break;
            case MODERATE:
                zone1.set(false);
                zone2.set(false);
                zone3.set(true);
                zone4.set(false);
                zone5.set(false);
                break;
            case HARD:
                zone1.set(false);
                zone2.set(false);
                zone3.set(false);
                zone4.set(true);
                zone5.set(false);
                break;
            case MAX:
                zone1.set(false);
                zone2.set(false);
                zone3.set(false);
                zone4.set(false);
                zone5.set(true);
                break;
        }
    }

    private void checkZones(final double VALUE) {
        percentage = VALUE / maxBpm;

        if (percentage > 1) {
            timer.start();
        } else {
            timer.stop();
            percentageText.setVisible(true);
        }

        if (percentage > 0.9) {
            setZone(Zone.MAX);
            zone1Segment.setActive(false);
            zone2Segment.setActive(false);
            zone3Segment.setActive(false);
            zone4Segment.setActive(false);
            zone5Segment.setActive(true);
        } else if (percentage > 0.8) {
            setZone(Zone.HARD);
            zone1Segment.setActive(false);
            zone2Segment.setActive(false);
            zone3Segment.setActive(false);
            zone4Segment.setActive(true);
            zone5Segment.setActive(false);
        } else if (percentage > 0.7) {
            setZone(Zone.MODERATE);
            zone1Segment.setActive(false);
            zone2Segment.setActive(false);
            zone3Segment.setActive(true);
            zone4Segment.setActive(false);
            zone5Segment.setActive(false);
        } else if (percentage > 0.6) {
            setZone(Zone.LIGHT);
            zone1Segment.setActive(false);
            zone2Segment.setActive(true);
            zone3Segment.setActive(false);
            zone4Segment.setActive(false);
            zone5Segment.setActive(false);
        } else {
            setZone(Zone.VERY_LIGHT);
            zone1Segment.setActive(true);
            zone2Segment.setActive(false);
            zone3Segment.setActive(false);
            zone4Segment.setActive(false);
            zone5Segment.setActive(false);
        }

        redraw();
    }

    public <T extends Number> T clamp(final T MIN, final T MAX, final T VALUE) {
        if (VALUE.doubleValue() < MIN.doubleValue()) return MIN;
        if (VALUE.doubleValue() > MAX.doubleValue()) return MAX;
        return VALUE;
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        if (aspectRatio * width > height) {
            width = 1 / (aspectRatio / height);
        } else if (1 / (aspectRatio / height) > width) {
            height = aspectRatio * width;
        }

        if (width > 0 && height > 0) {
            pane.setMaxSize(width, height);
            pane.setPrefSize(width, height);
            pane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            bpmArea.setPrefSize(width * 0.79347826, height * 0.50943396);
            bpmArea.relocate(0, height * 0.25471698);

            avgArea.setPrefSize(width * 0.47826087, height * 0.50943396);
            avgArea.relocate(width * 0.52173913, height * 0.25471698);

            percentageText.setFont(Fonts.robotoBold(height * 0.43396226));
            percentageText.setY(height * 0.5);

            percentageTitle.setFont(Fonts.robotoMedium(height * 0.095));
            percentageTitle.relocate(width * 0.54, height * 0.34);

            bpmText.setFont(Fonts.robotoMedium(height * 0.22641509));
            bpmText.setY(height * 0.58);

            bpmTitle.setFont(Fonts.robotoMedium(height * 0.095));
            bpmTitle.relocate(width * 0.84, height * 0.34);

            nameText.setFont(Fonts.robotoMedium(height * 0.16981132));
            nameText.relocate((width - nameText.getLayoutBounds().getWidth()) * 0.5, height * 0.025);

            zoneText.setFont(Fonts.robotoMedium(height * 0.09433962));
            zoneText.setY(height * 0.85);

            double segmentWidth  = width * 0.125;
            double segmentHeight = height * 0.12264151;
            zone1Segment.setPrefSize(segmentWidth, segmentHeight);
            zone2Segment.setPrefSize(segmentWidth, segmentHeight);
            zone3Segment.setPrefSize(segmentWidth, segmentHeight);
            zone4Segment.setPrefSize(segmentWidth, segmentHeight);
            zone5Segment.setPrefSize(segmentWidth, segmentHeight);

            segmentBox.relocate(width * 0.0075, height * 0.76);
            segmentBox.setSpacing(-width * 0.04347826);

            redraw();
        }
    }

    private void redraw() {
        percentageText.setText(String.format(Locale.US, "%.0f", clamp(0d, 100d, (percentage * 100))));
        percentageText.setX(width * 0.5 - percentageText.getLayoutBounds().getWidth());

        bpmText.setText(String.format(Locale.US, "%.0f", gauge.getCurrentValue()));
        bpmText.setX(width * 0.96 - bpmText.getLayoutBounds().getWidth());

        zoneText.setText(getZone().TEXT);
        zoneText.setX(width * 0.95 - zoneText.getLayoutBounds().getWidth());
    }
}
