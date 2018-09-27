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
package eu.hansolo.fx.simplesectiongauge;

import eu.hansolo.medusa.Fonts;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.Gauge.ScaleDirection;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.medusa.Section;
import eu.hansolo.medusa.tools.Helper;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * User: hansolo
 * Date: 25.07.16
 * Time: 08:33
 */
@DefaultProperty("children")
public class SimpleSectionGauge extends Region {
    private static final double PREFERRED_WIDTH  = 250;
    private static final double PREFERRED_HEIGHT = 250;
    private static final double MINIMUM_WIDTH    = 50;
    private static final double MINIMUM_HEIGHT   = 50;
    private static final double MAXIMUM_WIDTH    = 1024;
    private static final double MAXIMUM_HEIGHT   = 1024;
    private double          size;
    private Canvas          sectionCanvas;
    private GraphicsContext sectionCtx;
    private Arc             barBackground;
    private Arc             bar;
    private Text            titleText;
    private Text            valueText;
    private Text            unitText;
    private Pane            pane;
    private Paint           backgroundPaint;
    private Paint           borderPaint;
    private double          borderWidth;
    private Gauge           model;
    private List<Section>   sections;
    private boolean         sectionsVisible;
    private String          formatString;


    // ******************** Constructors **************************************
    @SuppressWarnings("ConfusingArgumentToVarargsMethod")
    public SimpleSectionGauge() {
        this("", "",
             0, 100,
             Color.rgb(69, 106, 207), Color.rgb(150, 150, 150, 0.25),
             true, null);
    }
    public SimpleSectionGauge(final String TITLE, final String UNIT,
                              final double MIN_VALUE, final double MAX_VALUE,
                              final Color BAR_COLOR, final Color BAR_BACKGROUND_COLOR,
                              final boolean SECTIONS_VISIBLE, final Section... SECTIONS) {
        backgroundPaint = Color.TRANSPARENT;
        borderPaint     = Color.TRANSPARENT;
        borderWidth     = 0d;
        model           = GaugeBuilder.create()
                                      .minValue(MIN_VALUE)
                                      .maxValue(MAX_VALUE)
                                      .title(TITLE)
                                      .unit(UNIT)
                                      .barBackgroundColor(BAR_BACKGROUND_COLOR)
                                      .barColor(BAR_COLOR)
                                      .animated(true)
                                      .startAngle(150)
                                      .angleRange(300)
                                      .sectionsVisible(SECTIONS_VISIBLE)
                                      .build();
        if (null != SECTIONS) {
            model.setSections(SECTIONS);
            sections = model.getSections();
        } else {
            sections = new ArrayList<>();
        }
        sectionsVisible = model.getSectionsVisible();
        formatString    = new StringBuilder("%.").append(Integer.toString(model.getDecimals())).append("f").toString();
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
        sectionCanvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        sectionCtx    = sectionCanvas.getGraphicsContext2D();

        barBackground = new Arc(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5, PREFERRED_WIDTH * 0.4, PREFERRED_HEIGHT * 0.4, model.getStartAngle() + 150, 300);
        barBackground.setType(ArcType.OPEN);
        barBackground.setStroke(model.getBarBackgroundColor());
        barBackground.setStrokeWidth(PREFERRED_WIDTH * 0.125);
        barBackground.setStrokeLineCap(StrokeLineCap.BUTT);
        barBackground.setFill(null);

        bar = new Arc(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5, PREFERRED_WIDTH * 0.4, PREFERRED_HEIGHT * 0.4, model.getStartAngle() + 90, 0);
        bar.setType(ArcType.OPEN);
        bar.setStroke(model.getBarColor());
        bar.setStrokeWidth(PREFERRED_WIDTH * 0.125);
        bar.setStrokeLineCap(StrokeLineCap.BUTT);
        bar.setFill(null);

        titleText = new Text(model.getTitle());
        titleText.setFill(model.getTitleColor());
        Helper.enableNode(titleText, !model.getTitle().isEmpty());

        valueText = new Text();
        valueText.setStroke(null);
        valueText.setFill(model.getValueColor());
        Helper.enableNode(valueText, model.isValueVisible());

        unitText = new Text();
        unitText.setStroke(null);
        unitText.setFill(model.getUnitColor());
        Helper.enableNode(unitText, model.isValueVisible() && !model.getUnit().isEmpty());

        pane = new Pane(barBackground, sectionCanvas, titleText, valueText, unitText, bar);
        pane.setBackground(new Background(new BackgroundFill(backgroundPaint, new CornerRadii(1024), Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(borderPaint, BorderStrokeStyle.SOLID, new CornerRadii(1024), new BorderWidths(borderWidth))));

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        model.currentValueProperty().addListener(o -> setBar(model.getCurrentValue()));
        model.setOnUpdate(e -> handleControlPropertyChanged(e.eventType.name()));
        model.decimalsProperty().addListener(o -> handleControlPropertyChanged("DECIMALS"));
    }


    // ******************** Methods *******************************************
    private void handleControlPropertyChanged(final String EVENT_TYPE) {
        if ("VISIBILITY".equals(EVENT_TYPE)) {
            Helper.enableNode(valueText, model.isValueVisible());
            Helper.enableNode(unitText, model.isValueVisible() && !model.getUnit().isEmpty());
        } else if ("SECTIONS".equals(EVENT_TYPE)) {
            sections = model.getSections();
        } else if ("RESIZE".equals(EVENT_TYPE)) {
            resize();
            redraw();
        } else if ("REDRAW".equals(EVENT_TYPE)) {
            redraw();
        } else if ("RECALC".equals(EVENT_TYPE)) {
            redraw();
        } else if ("DECIMALS".equals(EVENT_TYPE)) {
            formatString = new StringBuilder("%.").append(Integer.toString(model.getDecimals())).append("f").toString();
        }
    }

    public double getValue() { return model.getCurrentValue(); }
    public void setValue(final double VALUE) { model.setValue(VALUE); }
    public ReadOnlyDoubleProperty valueProperty() { return model.currentValueProperty(); }

    public double getMinValue() { return model.getMinValue(); }
    public void setMinValue(final double VALUE) { model.setMinValue(VALUE); }
    public DoubleProperty minValueProperty() { return model.minValueProperty(); }

    public double getMaxValue() { return model.getMaxValue(); }
    public void setMaxValue(final double VALUE) { model.setMaxValue(VALUE); }
    public DoubleProperty maxValueProperty() { return model.maxValueProperty(); }

    public String getTitle() { return model.getTitle(); }
    public void setTitle(final String TITLE) { model.setTitle(TITLE); }
    public StringProperty titleProperty() { return model.titleProperty(); }

    public String getUnit() { return model.getUnit(); }
    public void setUnit(final String UNIT) { model.setUnit(UNIT); }
    public StringProperty unitProperty() { return model.unitProperty(); }

    public ObservableList<Section> getSections() { return model.getSections(); }
    public void setSections(final List<Section> SECTIONS) { model.setSections(SECTIONS); }
    public void setSections(final Section... SECTIONS) { setSections(Arrays.asList(SECTIONS)); }
    public void addSection(final Section SECTION) { model.addSection(SECTION); }
    public void removeSection(final Section SECTION) { model.removeSection(SECTION); }
    public void clearSections() { model.clearSections(); }

    public boolean getSectionsVisible() { return model.getSectionsVisible(); }
    public void setSectionsVisible(final boolean VISIBLE) { model.setSectionsVisible(VISIBLE); }
    public BooleanProperty sectionsVisibleProperty() { return model.sectionsVisibleProperty(); }

    public Color getBarColor() { return model.getBarColor(); }
    public void setBarColor(final Color COLOR) { model.setBarColor(COLOR); }
    public ObjectProperty<Color> barColorProperty() { return model.barColorProperty(); }

    public Color getBarBackgroundColor() { return model.getBarBackgroundColor(); }
    public void setBarBackgroundColor(final Color COLOR) { model.setBarBackgroundColor(COLOR); }
    public ObjectProperty<Color> barBackgroundColorProperty() { return model.barBackgroundColorProperty(); }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }


    // ******************** Canvas ********************************************
    private void setBar(final double VALUE) {
        if (model.getMinValue() > 0) {
            bar.setLength((model.getMinValue() - VALUE) * model.getAngleStep());
        } else {
            bar.setLength(-VALUE * model.getAngleStep());
        }
        if (model.getSectionsVisible() && !sections.isEmpty()) {
            for (Section section : sections) {
                if (section.contains(VALUE)) {
                    bar.setStroke(section.getColor());
                    break;
                }
            }
        }

        valueText.setText(String.format(model.getLocale(), formatString, VALUE));
        valueText.setLayoutX((size - valueText.getLayoutBounds().getWidth()) * 0.5);
    }

    private void drawBackground() {
        sectionCanvas.setCache(false);
        sectionCtx.setLineCap(StrokeLineCap.BUTT);
        sectionCtx.clearRect(0, 0, size, size);

        if (sectionsVisible && !sections.isEmpty()) {
            double xy        = 0.012 * size;
            double wh        = size * 0.976;
            double minValue  = model.getMinValue();
            double maxValue  = model.getMaxValue();
            double angleStep = model.getAngleStep();

            sectionCtx.setLineWidth(size * 0.025);
            sectionCtx.setLineCap(StrokeLineCap.BUTT);
            for (int i = 0; i < sections.size(); i++) {
                Section section = sections.get(i);
                double  sectionStartAngle;
                if (Double.compare(section.getStart(), maxValue) <= 0 && Double.compare(section.getStop(), minValue) >= 0) {
                    if (Double.compare(section.getStart(), minValue) < 0 && Double.compare(section.getStop(), maxValue) < 0) {
                        sectionStartAngle = 0;
                    } else {
                        sectionStartAngle = ScaleDirection.CLOCKWISE == model.getScaleDirection() ? (section.getStart() - minValue) * angleStep : -(section.getStart() - minValue) * angleStep;
                    }
                    double sectionAngleExtend;
                    if (Double.compare(section.getStop(), maxValue) > 0) {
                        sectionAngleExtend = ScaleDirection.CLOCKWISE == model.getScaleDirection() ? (maxValue - section.getStart()) * angleStep : -(maxValue - section.getStart()) * angleStep;
                    } else if (Double.compare(section.getStart(), minValue) < 0) {
                        sectionAngleExtend = ScaleDirection.CLOCKWISE == model.getScaleDirection() ? (section.getStop() - minValue) * model.getAngleStep() : -(section.getStop() - minValue) * angleStep;
                    } else {
                        sectionAngleExtend = ScaleDirection.CLOCKWISE == model.getScaleDirection() ? (section.getStop() - section.getStart()) * angleStep : -(section.getStop() - section.getStart()) * angleStep;
                    }
                    sectionCtx.save();

                    sectionCtx.setStroke(section.getColor());
                    sectionCtx.strokeArc(xy, xy, wh, wh, -(120 + sectionStartAngle), -sectionAngleExtend, ArcType.OPEN);
                    sectionCtx.restore();
                }
            }
        }

        sectionCanvas.setCache(true);
        sectionCanvas.setCacheHint(CacheHint.QUALITY);
    }

    
    // ******************** Resizing ******************************************
    private void resizeValueText() {
        double maxWidth = size * 0.86466165;
        double fontSize = size * 0.2556391;
        valueText.setFont(Fonts.latoLight(fontSize));
        if (valueText.getLayoutBounds().getWidth() > maxWidth) { Helper.adjustTextSize(valueText, maxWidth, fontSize); }
        valueText.relocate((size - valueText.getLayoutBounds().getWidth()) * 0.5, (size - valueText.getLayoutBounds().getHeight()) * 0.5);
    }
    private void resizeStaticText() {
        double maxWidth = size * 0.35;
        double fontSize = size * 0.08082707;
        titleText.setFont(Fonts.latoBold(fontSize));
        if (titleText.getLayoutBounds().getWidth() > maxWidth) { Helper.adjustTextSize(titleText, maxWidth, fontSize); }
        titleText.relocate((size - titleText.getLayoutBounds().getWidth()) * 0.5, size * 0.22180451);
        titleText.setFill(Color.RED);
        unitText.setFont(Fonts.latoBold(fontSize));
        if (unitText.getLayoutBounds().getWidth() > maxWidth) { Helper.adjustTextSize(unitText, maxWidth, fontSize); }
        unitText.relocate((size - unitText.getLayoutBounds().getWidth()) * 0.5, size * 0.68984962);
    }

    private void resize() {
        double width = getWidth() - getInsets().getLeft() - getInsets().getRight();
        double height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size = width < height ? width : height;

        if (width > 0 && height > 0) {
            pane.setMaxSize(size, size);
            pane.setPrefSize(size, size);
            pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

            sectionCanvas.setWidth(size);
            sectionCanvas.setHeight(size);

            barBackground.setCenterX(size * 0.5);
            barBackground.setCenterY(size * 0.5);
            barBackground.setRadiusX(size * 0.4);
            barBackground.setRadiusY(size * 0.4);
            barBackground.setStrokeWidth(size * 0.125);

            bar.setCenterX(size * 0.5);
            bar.setCenterY(size * 0.5);
            bar.setRadiusX(size * 0.4);
            bar.setRadiusY(size * 0.4);
            bar.setStrokeWidth(size * 0.125);

            resizeValueText();

            redraw();
        }
    }

    private void redraw() {
        pane.setBackground(new Background(new BackgroundFill(backgroundPaint, new CornerRadii(1024), Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(borderPaint, BorderStrokeStyle.SOLID, new CornerRadii(1024), new BorderWidths(borderWidth / PREFERRED_WIDTH * size))));

        drawBackground();
        setBar(model.getCurrentValue());

        titleText.setText(model.getTitle());
        unitText.setText(model.getUnit());
        resizeStaticText();

        titleText.setFill(model.getTitleColor());
        valueText.setFill(model.getValueColor());
        unitText.setFill(model.getUnitColor());
    }
}
