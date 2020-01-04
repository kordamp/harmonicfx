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

import java.util.List;
import java.util.Locale;


/**
 * User: hansolo
 * Date: 17.02.17
 * Time: 15:19
 */
@DefaultProperty("children")
public class DonutChart extends Region {
    private static final double                        PREFERRED_WIDTH  = 150;
    private static final double                        PREFERRED_HEIGHT = 150;
    private static final double                        MINIMUM_WIDTH    = 50;
    private static final double                        MINIMUM_HEIGHT   = 50;
    private static final double                        MAXIMUM_WIDTH    = 1024;
    private static final double                        MAXIMUM_HEIGHT   = 1024;
    private              double                        size;
    private              double                        width;
    private              double                        height;
    private              Canvas                        chartCanvas;
    private              GraphicsContext               chartCtx;
    private              Canvas                        legendCanvas;
    private              GraphicsContext               legendCtx;
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
    public DonutChart() {
        this((ChartData[]) null);
    }
    public DonutChart(final ChartData... DATA) {
        backgroundPaint    = Color.TRANSPARENT;
        borderPaint        = Color.TRANSPARENT;
        borderWidth        = 0d;
        dataList           = null == DATA ? FXCollections.observableArrayList() : FXCollections.observableArrayList(DATA);
        barBorderColor     = new ObjectPropertyBase<Color>(Color.LIGHTGRAY) {
            @Override protected void invalidated() { redraw(); }
            @Override public Object getBean() { return DonutChart.this; }
            @Override public String getName() { return "barBorderColor"; }
        };
        textColor          = new ObjectPropertyBase<Color>(Color.BLACK) {
            @Override protected void invalidated() { redraw(); }
            @Override public Object getBean() { return DonutChart.this; }
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

        chartCanvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        chartCtx = chartCanvas.getGraphicsContext2D();

        legendCanvas = new Canvas(PREFERRED_WIDTH * 0.225, PREFERRED_HEIGHT);
        legendCtx    = legendCanvas.getGraphicsContext2D();

        pane = new Pane(legendCanvas, chartCanvas);
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
        double          canvasSize     = chartCanvas.getWidth();
        int             noOfItems      = dataList.size();
        double          center         = canvasSize * 0.5;
        double          innerRadius    = canvasSize * 0.275;
        double          outerRadius    = canvasSize * 0.4;
        double          barWidth       = canvasSize * 0.1;
        //List<ChartData> sortedDataList = dataList.stream().sorted(Comparator.comparingDouble(ChartData::getValue).reversed()).collect(Collectors.toList());
        double          sum            = dataList.stream().mapToDouble(ChartData::getValue).sum();
        double          stepSize       = 360.0 / sum;
        double          angle          = 0;
        double          startAngle     = 90;
        double          xy             = canvasSize * 0.1;
        double          wh             = canvasSize * 0.8;
        Color           bkgColor       = Color.BLACK;
        Color           textColor      = Color.WHITE;

        chartCtx.clearRect(0, 0, canvasSize, canvasSize);
        chartCtx.setLineCap(StrokeLineCap.BUTT);
        chartCtx.setFill(textColor);
        chartCtx.setTextBaseline(VPos.CENTER);
        chartCtx.setTextAlign(TextAlignment.CENTER);

        // Sum
        chartCtx.setFont(Font.font(canvasSize * 0.15));
        chartCtx.fillText(String.format(Locale.US, "%.0f", sum), center, center, canvasSize * 0.4);

        chartCtx.setFont(Font.font(barWidth * 0.5));
        for (int i = 0 ; i < noOfItems ; i++) {
            ChartData data  = dataList.get(i);
            double    value = data.getValue();
            startAngle -= angle;
            angle = value * stepSize;

            // Segment
            chartCtx.setLineWidth(barWidth);
            chartCtx.setStroke(data.getColor());
            chartCtx.strokeArc(xy, xy, wh, wh, startAngle, -angle, ArcType.OPEN);

            // Percentage
            double x = innerRadius * Math.cos(Math.toRadians(startAngle - (angle * 0.5)));
            double y = -innerRadius * Math.sin(Math.toRadians(startAngle - (angle * 0.5)));
            chartCtx.setFill(textColor);
            chartCtx.fillText(String.format(Locale.US, "%.0f%%", (value / sum * 100.0)), center + x, center + y, barWidth);

            // Value
            x = outerRadius * Math.cos(Math.toRadians(startAngle - (angle * 0.5)));
            y = -outerRadius * Math.sin(Math.toRadians(startAngle - (angle * 0.5)));
            chartCtx.setFill(bkgColor);
            chartCtx.fillText(String.format(Locale.US, "%.0f", value), center + x, center + y, barWidth);
        }
    }

    private void drawLegend() {
        double          canvasWidth  = legendCanvas.getWidth();
        double          canvasHeight = legendCanvas.getHeight();
        int             noOfItems    = dataList.size();
        //List<ChartData> sortedDataList = dataList.stream().sorted(Comparator.comparingDouble(ChartData::getValue).reversed()).collect(Collectors.toList());
        Color           textColor    = Color.WHITE;
        double          stepSize     = canvasHeight * 0.9 / (noOfItems + 1);

        legendCtx.clearRect(0, 0, canvasWidth, canvasHeight);
        legendCtx.setTextAlign(TextAlignment.LEFT);
        legendCtx.setTextBaseline(VPos.CENTER);
        legendCtx.setFont(Font.font(canvasHeight * 0.045));

        for (int i = 0 ; i < noOfItems ; i++) {
            ChartData data = dataList.get(i);

            legendCtx.setFill(data.getColor());
            legendCtx.fillOval(0, (i + 1) * stepSize, size * 0.0375, size * 0.0375);
            legendCtx.setFill(textColor);
            legendCtx.fillText(data.getName(), size * 0.05, (i + 1) * stepSize + canvasHeight * 0.025);
        }
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            pane.setMaxSize(width, height);
            pane.setPrefSize(width, height);

            chartCanvas.setWidth(size);
            chartCanvas.setHeight(size);

            legendCanvas.setWidth(size * 0.25);
            legendCanvas.setHeight(size);
            legendCanvas.relocate(size * 0.05, size * 0.05);
            legendCanvas.setVisible(width > (height * 1.2));

            if (width > (height * 1.5)) {
                chartCanvas.relocate((width - size) * 0.5, 0);
            } else if (width > (height * 1.2)) {
                chartCanvas.relocate(width - size, 0);
            } else {
                chartCanvas.relocate((width - size) * 0.5, 0);
            }

            redraw();
        }
    }

    private void redraw() {
        pane.setBackground(new Background(new BackgroundFill(backgroundPaint, CornerRadii.EMPTY, Insets.EMPTY)));
        pane.setBorder(new Border(new BorderStroke(borderPaint, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(borderWidth / PREFERRED_WIDTH * size))));

        drawLegend();
        drawChart();
    }
}
