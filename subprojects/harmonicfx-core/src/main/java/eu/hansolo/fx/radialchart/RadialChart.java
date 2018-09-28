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
package eu.hansolo.fx.radialchart;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
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
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.Comparator;
import java.util.Locale;


/**
 * User: hansolo
 * Date: 17.02.17
 * Time: 15:19
 */
@DefaultProperty("children")
public class RadialChart extends Region {
    private static final double                        PREFERRED_WIDTH  = 250;
    private static final double                        PREFERRED_HEIGHT = 250;
    private static final double                        MINIMUM_WIDTH    = 50;
    private static final double                        MINIMUM_HEIGHT   = 50;
    private static final double                        MAXIMUM_WIDTH    = 1024;
    private static final double                        MAXIMUM_HEIGHT   = 1024;
    private              double                        size;
    private              double                        width;
    private              double                        height;
    private              Canvas                        canvas;
    private              GraphicsContext               ctx;
    private              Pane                          pane;
    private              Paint                         backgroundPaint;
    private              Paint                         borderPaint;
    private              double                        borderWidth;
    private              ObservableList<ChartData>     dataList;
    private              ListChangeListener<ChartData> chartDataListener;
    private              ChartDataEventListener        chartEventListener;
    private              ObjectProperty<Color>         barBorderColor;
    private              ObjectProperty<Color>         textColor;


    // ******************** Constructors **************************************
    public RadialChart() {
        this(null);
    }
    public RadialChart(final ChartData... DATA) {
        backgroundPaint    = Color.TRANSPARENT;
        borderPaint        = Color.TRANSPARENT;
        borderWidth        = 0d;
        dataList           = null == DATA ? FXCollections.observableArrayList() : FXCollections.observableArrayList(DATA);
        barBorderColor     = new ObjectPropertyBase<Color>(Color.LIGHTGRAY) {
            @Override protected void invalidated() { redraw(); }
            @Override public Object getBean() { return RadialChart.this; }
            @Override public String getName() { return "barBorderColor"; }
        };
        textColor          = new ObjectPropertyBase<Color>(Color.BLACK) {
            @Override protected void invalidated() { redraw(); }
            @Override public Object getBean() { return RadialChart.this; }
            @Override public String getName() { return "textColor"; }
        };
        chartEventListener = e -> drawChart();
        dataList.forEach(chartData -> chartData.addChartDataEventListener(chartEventListener));
        chartDataListener  = c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(addedItem -> addedItem.addChartDataEventListener(chartEventListener));
                } else if (c.wasRemoved()) {
                    c.getRemoved().forEach(removedItem -> removedItem.removeChartDataEventListener(chartEventListener));
                }
            }
            drawChart();
        };

        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void initGraphics() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 ||
            Double.compare(getWidth(), 0.0) <= 0 || Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        canvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ctx    = canvas.getGraphicsContext2D();

        pane = new Pane(canvas);
        pane.setBackground(new Background(new BackgroundFill(backgroundPaint, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(borderPaint, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(borderWidth))));

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        dataList.addListener(chartDataListener);
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double HEIGHT) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public ObservableList<ChartData> getChartData() { return dataList; }
    public void addChartData(final ChartData... DATA) { dataList.addAll(DATA); }
    public void setChartData(final ChartData... DATA) { dataList.setAll(DATA); }
    public void removeChartData(final ChartData DATA) { dataList.remove(DATA); }
    public void clearChartData() { dataList.clear(); }

    public Color getTextColor() { return textColor.get(); }
    public void setTextColor(final Color COLOR) { textColor.set(COLOR); }
    public ObjectProperty<Color> textColorProperty() { return textColor; }

    public Color getBarBorderColor() { return barBorderColor.get(); }
    public void setBarBorderColor(final Color COLOR) { barBorderColor.set(COLOR); }
    public ObjectProperty<Color> barBorderColorProperty() { return barBorderColor; }

    private void drawChart() {
        double          radius         = size * 0.5;
        double          innerSpacer    = radius * 0.18;
        double          barWidth       = (radius - innerSpacer) / dataList.size();
        //List<ChartData> sortedDataList = dataList.stream().sorted(Comparator.comparingDouble(ChartData::getValue)).collect(Collectors.toList());;
        int             noOfItems      = dataList.size();
        double          max            = dataList.stream().max(Comparator.comparingDouble(ChartData::getValue)).get().getValue();

        double          nameX          = radius * 0.975;
        double          nameWidth      = radius * 0.95;
        double          valueY         = radius * 0.94;
        double          valueWidth     = barWidth * 0.9;

        ctx.clearRect(0, 0, size, size);
        ctx.setLineCap(StrokeLineCap.BUTT);
        ctx.setFill(getTextColor());
        ctx.setTextAlign(TextAlignment.RIGHT);
        ctx.setTextBaseline(VPos.CENTER);
        ctx.setFont(Font.font(barWidth * 0.5));

        ctx.setStroke(getBarBorderColor());
        ctx.setLineWidth(1);
        ctx.strokeLine(radius, 0, radius, radius - barWidth * 0.875);
        ctx.strokeLine(0, radius, radius - barWidth * 0.875, radius);
        ctx.strokeArc(noOfItems * barWidth, noOfItems * barWidth, size - (2 * noOfItems * barWidth), size - (2 * noOfItems * barWidth), 90, -270, ArcType.OPEN);

        for (int i = 0 ; i < dataList.size() ; i++) {
            ChartData data  = dataList.get(i);
            double    value = data.getValue();
            double    bkgXY = i * barWidth;
            double    bkgWH = size - (2 * i * barWidth);
            double    barXY = barWidth * 0.5 + i * barWidth;
            double    barWH = size - barWidth - (2 * i * barWidth);
            double    angle = value / max * 270.0;


            // Background
            ctx.setLineWidth(1);
            ctx.setStroke(getBarBorderColor());
            ctx.strokeArc(bkgXY, bkgXY, bkgWH, bkgWH, 90, -270, ArcType.OPEN);

            // DataBar
            ctx.setLineWidth(barWidth);
            ctx.setStroke(data.getColor());
            ctx.strokeArc(barXY, barXY, barWH, barWH, 90, -angle, ArcType.OPEN);

            // Name
            ctx.setTextAlign(TextAlignment.RIGHT);
            ctx.fillText(data.getName(), nameX, barXY, nameWidth);

            // Value
            ctx.setTextAlign(TextAlignment.CENTER);
            ctx.fillText(String.format(Locale.US, "%.0f", value), barXY, valueY, valueWidth);
        }
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            pane.setMaxSize(size, size);
            pane.setPrefSize(size, size);
            pane.relocate((getWidth() - size) * 0.5, (getHeight() - size) * 0.5);

            canvas.setWidth(size);
            canvas.setHeight(size);

            redraw();
        }
    }

    private void redraw() {
        pane.setBackground(new Background(new BackgroundFill(backgroundPaint, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(borderPaint, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(borderWidth / PREFERRED_WIDTH * size))));

        drawChart();
    }
}
