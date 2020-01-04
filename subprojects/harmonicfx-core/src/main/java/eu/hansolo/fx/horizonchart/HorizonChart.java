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

import javafx.beans.DefaultProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.PopupWindow.AnchorLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eu.hansolo.fx.horizonchart.Helper.clamp;


/**
 * User: hansolo
 * Date: 11.11.17
 * Time: 09:02
 */
@DefaultProperty("children")
public class HorizonChart<T> extends Region {
    private static final double                   PREFERRED_WIDTH    = 250;
    private static final double                   PREFERRED_HEIGHT   = 25;
    private static final double                   MINIMUM_WIDTH      = 10;
    private static final double                   MINIMUM_HEIGHT     = 10;
    private static final double                   MAXIMUM_WIDTH      = 4096;
    private static final double                   MAXIMUM_HEIGHT     = 4096;
    private static final int                      MAX_NO_OF_BANDS    = 5;
    private static final int                      SUB_DIVISIONS      = 24;
    private              double                   width;
    private              double                   height;
    private              Canvas                   canvas;
    private              GraphicsContext          ctx;
    private              Series<T>                series;
    private              List<Point>              points;
    private              double                   scaleX;
    private              double                   scaleY;
    private              boolean                  smoothed;
    private              boolean                  referenceZero;
    private              int                      noOfBands;
    private              int                      noOfItems;
    private              double                   minY;
    private              double                   maxY;
    private              double                   bandWidth;
    private              Tooltip                  tooltip;
    private              Color                    aboveColorFrom;
    private              Color                    aboveColorTo;
    private              Color                    belowColorFrom;
    private              Color                    belowColorTo;
    private              List<Color>              belowColors;
    private              List<Color>              aboveColors;
    private              EventHandler<MouseEvent> mouseListener;
    private              SeriesEventListener      seriesListener;


    // ******************** Constructors **************************************
    public HorizonChart() {
        this(1, new Series<T>(), false);
    }
    public HorizonChart(final Series<T> SERIES) {
        this(1,  SERIES, false);
    }
    public HorizonChart(final int BANDS, final Series<T> SERIES) {
        this(BANDS, SERIES, false);
    }
    public HorizonChart(final int BANDS, final Series<T> SERIES, final boolean SMOOTHED) {
        series        = SERIES;
        scaleX        = 1;
        scaleY        = 1;
        smoothed      = SMOOTHED;
        referenceZero = true;
        noOfBands     = clamp(1, MAX_NO_OF_BANDS, BANDS);
        noOfItems     = SERIES.getNoOfItems();
        minY          = SERIES.getItems().stream().mapToDouble(Data::getY).min().getAsDouble();
        maxY          = SERIES.getItems().stream().mapToDouble(Data::getY).max().getAsDouble();
        bandWidth     = (maxY - minY) / noOfBands;
        tooltip       = new Tooltip();
        tooltip.setAnchorLocation(AnchorLocation.CONTENT_BOTTOM_LEFT);

        adjustColors();

        // Create list of points
        points = new ArrayList<>(noOfItems);
        prepareData();

        mouseListener           = mouseEvent -> {
            final EventType<? extends MouseEvent> TYPE = mouseEvent.getEventType();
            if (MouseEvent.MOUSE_CLICKED == TYPE) {
                Data<T> data = selectDataAt(mouseEvent.getX());
                tooltip.setText(createTooltipText(data));
                tooltip.setX(mouseEvent.getScreenX());
                tooltip.setY(mouseEvent.getScreenY());
                tooltip.show(getScene().getWindow());
                getSeries().fireSeriesEvent(new SeriesEvent(getSeries(), data, SeriesEventType.SELECT_DATA));
            } else if (MouseEvent.MOUSE_MOVED == TYPE) {
                tooltip.hide();
            } else if (MouseEvent.MOUSE_EXITED == TYPE) {
                tooltip.hide();
            }
        };
        seriesListener          = seriesEvent -> redraw();

        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void initGraphics() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 || Double.compare(getWidth(), 0.0) <= 0 ||
            Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        canvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ctx    = canvas.getGraphicsContext2D();

        getChildren().setAll(canvas);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseListener);
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, mouseListener);
        canvas.addEventHandler(MouseEvent.MOUSE_EXITED, mouseListener);
        series.setOnSeriesEvent(seriesListener);
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double HEIGHT) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double WIDTH) { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double HEIGHT) { return super.computePrefWidth(HEIGHT); }
    @Override protected double computePrefHeight(final double WIDTH) { return super.computePrefHeight(WIDTH); }
    @Override protected double computeMaxWidth(final double HEIGHT) { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double WIDTH) { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    /**
     * Returns the currently used series
     * @return the currently used series
     */
    public Series<T> getSeries() { return series; }

    /**
     * Returns true if the items of the series will be smoothed when drawing
     * @return true if the items of the series will be smoothed when drawing
     */
    public boolean isSmoothed() { return smoothed; }
    /**
     * Defines if the items of the series will be smoothed when drawing
     * @param SMOOTHED
     */
    public void setSmoothed(final boolean SMOOTHED) {
        smoothed = SMOOTHED;
        drawChart();
    }

    /**
     * Returns true if the reference for the chart is 0.
     * Otherwise the first y-value of the data series will be
     * taken as reference value and all other values will be
     * normalized to it.
     * @return true if the reference for the chart is 0
     */
    public boolean isReferenceZero() { return referenceZero; }
    /**
     * Defines if the reference for the chart will be 0 or
     * the first y-value of the data series. If false all
     * values will be normalized to the first y-value of the
     * data series (e.g. for stock data).
     * @param IS_ZERO
     */
    public void setReferenceZero(final boolean IS_ZERO) {
        referenceZero = IS_ZERO;
        drawChart();
    }

    /**
     * Returns the number of bands used for visualization
     * @return the number of bands used for visualization
     */
    public int getNoOfBands() { return noOfBands; }
    /**
     * Defines the number of bands used for visualization
     * One can define between 1 and 5 bands
     * @param BANDS
     */
    public void setNoOfBands(final int BANDS) {
        noOfBands = clamp(1, 5, BANDS);
        bandWidth = getRangeY() / noOfBands;

        if (null == aboveColorFrom || null == aboveColorTo) {
            Color   aboveColor      = series.getColor();
            Color[] aboveColorRange = Helper.getColorRangeMinMax(aboveColor, noOfBands);
            aboveColors = Helper.createColorPalette(aboveColorRange[0], aboveColorRange[1], noOfBands);
        } else {
            aboveColors = Helper.createColorPalette(aboveColorFrom, aboveColorTo, BANDS);
        }

        if (null == belowColorFrom || null == belowColorTo) {
            Color   aboveColor      = series.getColor();
            Color   belowColor      = Helper.getComplementaryColor(aboveColor);
            Color[] belowColorRange = Helper.getColorRangeMinMax(belowColor, noOfBands);
            belowColors = Helper.createColorPalette(belowColorRange[0], belowColorRange[1], noOfBands);
        } else {
            belowColors = Helper.createColorPalette(belowColorFrom, belowColorTo, BANDS);
        }

        resize();
    }

    /**
     * Returns the number of items in the current series
     * @return the number of items in the current series
     */
    public int getNoOfItems() { return noOfItems; }

    /**
     * Returns the min value of the items in the current series
     * @return the min value of the items in the current series
     */
    public double getMinY() { return minY; }

    /**
     * Returns the max value of the items in the current series
     * @return the max value of the items in the current series
     */
    public double getMaxY() { return maxY; }

    /**
     * Returns the range of y-values of the items in the current series
     * @return the range of y-values of the items in the current series
     */
    public double getRangeY() { return getMaxY() - getMinY(); }

    /**
     * Returns a copy of the currently used colors for negative values
     * @return a copy of the currently used colors for negative values
     */
    public List<Color> getNegativeColors() { return new ArrayList<>(belowColors); }
    /**
     * Creates a color palette for the negative values
     * based on the number of bands starting with the FROM_COLOR
     * and ending with the TO_COLOR
     * @param FROM_COLOR
     * @param TO_COLOR
     */
    public void setNegativeColors(final Color FROM_COLOR, final Color TO_COLOR) {
        belowColorFrom = FROM_COLOR;
        belowColorTo   = TO_COLOR;
        belowColors    = Helper.createColorPalette(belowColorFrom, belowColorTo, getNoOfBands());
        drawChart();
    }
    public void setNegativeColors(final List<Color> COLORS) {
        if (COLORS.size() < getNoOfBands()) {
            Color negativeBaseColor = COLORS.get(0);
            if (null == negativeBaseColor) { negativeBaseColor = Color.RED; }
            belowColors = Helper.createColorVariationsAsList(negativeBaseColor, getNoOfBands());
        } else {
            belowColors = COLORS;
        }
        drawChart();
    }

    /**
     * Returns a copy of the currently used colors for positive values
     * @return a copy of the currently used colors for positive values
     */
    public List<Color> getPositiveColors() { return new ArrayList<>(aboveColors); }
    /**
     * Creates a color palette for the positive values
     * based on the number of bands starting with the FROM_COLOR
     * and ending with the TO_COLOR
     * @param FROM_COLOR
     * @param TO_COLOR
     */
    public void setPositiveColors(final Color FROM_COLOR, final Color TO_COLOR) {
        aboveColorFrom = FROM_COLOR;
        aboveColorTo   = TO_COLOR;
        aboveColors    = Helper.createColorPalette(aboveColorFrom, aboveColorTo, getNoOfBands());
        drawChart();
    }
    public void setPositiveColors(final List<Color> COLORS) {
        if (COLORS.size() < getNoOfBands()) {
            Color positiveBaseColor = COLORS.get(0);
            if (null == positiveBaseColor) { positiveBaseColor = Color.RED; }
            aboveColors = Helper.createColorVariationsAsList(positiveBaseColor, getNoOfBands());
        } else {
            aboveColors = COLORS;
        }
        drawChart();
    }

    /**
     * Removes the series event listener
     */
    public void dispose() {
        series.removeSeriesEventListener(seriesListener);
        canvas.removeEventHandler(MouseEvent.MOUSE_EXITED, mouseListener);
        canvas.removeEventHandler(MouseEvent.MOUSE_MOVED, mouseListener);
        canvas.removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseListener);
    }

    /**
     * Fill the list of points with the current data from the series
     */
    private void prepareData() {
        // Create list of points
        points.clear();
        for (int i = 0; i < getNoOfItems(); i++) { points.add(new Point(i, series.getItems().get(i).getY())); }

        // Normalize y values to 0
        double refValue = isReferenceZero() ? 0 : (points.isEmpty() ? 0 : points.get(0).getY());
        points.forEach(point -> point.setY(point.getY() - refValue));
    }

    private void drawChart() {
        if (series.getItems().isEmpty()) { return; }

        ctx.clearRect(0, 0, width, height);

        // Subdivide points
        Point[] subdividedPoints;
        if (smoothed) {
            subdividedPoints = Helper.subdividePoints(points.toArray(new Point[0]), SUB_DIVISIONS);
        } else {
            subdividedPoints = Helper.subdividePointsLinear(points.toArray(new Point[0]), SUB_DIVISIONS);
        }

        // Split in points above and below 0
        List<Point>[] splittedPoints = splitIntoAboveAndBelow(Arrays.asList(subdividedPoints));
        List<Point>   aboveRefPoints = splittedPoints[0];
        List<Point>   belowRefPoints = splittedPoints[1];

        // Split points above and below 0 into bands
        Map<Integer, List<Point>> aboveRefPointsSplitToBands = splitIntoBands(aboveRefPoints, bandWidth);
        Map<Integer, List<Point>> belowRefPointsSplitToBands = splitIntoBands(belowRefPoints, bandWidth);

        // Draw values above 0
        if (!aboveRefPoints.isEmpty()) { drawPath(aboveRefPointsSplitToBands, aboveColors); }

        // Draw values below 0
        if (!belowRefPoints.isEmpty()) { drawPath(belowRefPointsSplitToBands, belowColors); }
    }

    private void drawPath(final Map<Integer, List<Point>> MAP_OF_BANDS, final List<Color> COLORS) {
        double oldX = 0;
        for (int band = 0 ; band < getNoOfBands() ; band++) {
            ctx.beginPath();
            for (Point p : MAP_OF_BANDS.get(band)) {
                double x = p.getX() * scaleX;
                double y = height - (p.getY() * scaleY);
                ctx.lineTo(x, y + (band * bandWidth) * scaleY);
                oldX = x;
            }
            ctx.lineTo(oldX, height);
            ctx.lineTo(0, height);
            ctx.closePath();
            ctx.setFill(COLORS.get(band));
            ctx.fill();
        }
    }

    private List<Point>[] splitIntoAboveAndBelow(final List<Point> POINTS) {
        ArrayList<Point> aboveReferencePoints = new ArrayList<>();
        ArrayList<Point> belowReferencePoints = new ArrayList<>();
        Point   last       = POINTS.get(0);
        boolean isAbove    = Double.compare(last.getY(), 0.0) >= 0;
        int     noOfPoints = POINTS.size();
        for (int i = 0 ; i < noOfPoints ; i++) {
            Point current = POINTS.get(i);
            Point next    = i < noOfPoints - 1 ? POINTS.get(i + 1) : POINTS.get(noOfPoints - 1);

            if (Double.compare(current.getY(), 0.0) >= 0) {
                if (!isAbove) {
                    Point p = Helper.calcIntersectionPoint(last, current, 0.0);
                    aboveReferencePoints.add(p);
                    belowReferencePoints.add(p);
                }
                aboveReferencePoints.add(current);
                isAbove = true;
            } else {
                if (isAbove) {
                    Point p = Helper.calcIntersectionPoint(current, next, 0.0);
                    aboveReferencePoints.add(p);
                    belowReferencePoints.add(p);
                }
                // Invert y values that are below the reference point
                belowReferencePoints.add(new Point(current.getX(), -current.getY()));
                isAbove = false;
            }
            last = current;
        }
        return new ArrayList[] { aboveReferencePoints, belowReferencePoints };
    }

    private Map<Integer, List<Point>> splitIntoBands(final List<Point> POINTS, final double BAND_WIDTH) {
        Map<Integer, List<Point>> mapOfBands = new HashMap<>(getNoOfBands());
        if (POINTS.isEmpty()) { return mapOfBands; }

        int    noOfPoints = POINTS.size();
        double currentBandMinY;
        double currentBandMaxY;
        double currentBandMinYScaled;
        double currentBandMaxYScaled;

        // Add first point to all bands
        Point firstPoint = new Point(POINTS.get(0).getX(), POINTS.get(0).getY());
        for (int band = 0 ; band < getNoOfBands() ; band++) {
            List<Point> listOfPointsInBand = new ArrayList<>(noOfPoints);
            listOfPointsInBand.add(firstPoint);
            mapOfBands.put(band, listOfPointsInBand);
        }

        // Iterate over all points and check for each band
        for (int i = 1 ; i < noOfPoints - 1 ; i++) {
            Point  last     = POINTS.get(i - 1);
            double lastY    = height - (last.getY() * scaleY);
            Point  current  = POINTS.get(i);
            double currentY = height - (current.getY() * scaleY);
            Point  next     = POINTS.get(i + 1);
            double nextY    = height - (next.getY() * scaleY);

            for (int band = 0 ; band < getNoOfBands() ; band++) {
                currentBandMinY       = band * BAND_WIDTH;
                currentBandMaxY       = currentBandMinY + BAND_WIDTH;
                currentBandMinYScaled = height - currentBandMinY * scaleY;
                currentBandMaxYScaled = height - currentBandMaxY * scaleY;

                if (Double.compare(lastY, currentBandMinYScaled) >= 0) {             // last <= currentBandMinY
                    // Calculate intersection with currentBandMinY
                    mapOfBands.get(band).add(Helper.calcIntersectionPoint(last, current, currentBandMinY));
                } else if (Double.compare(currentY, currentBandMinYScaled) <= 0 &&
                           Double.compare(currentY, currentBandMaxYScaled) >= 0) {   // currentBandMinY < current < currentBandMaxY
                    mapOfBands.get(band).add(new Point(current.getX(), current.getY()));
                } else if (Double.compare(nextY, currentBandMaxYScaled) <= 0) {      // next >= currentBandMaxY
                    // Calculate intersection with currentBandMaxY
                    mapOfBands.get(band).add(Helper.calcIntersectionPoint(current, next, currentBandMaxY));
                }
            }
        }

        // Add last point to all bands
        Point lastPoint = new Point(POINTS.get(noOfPoints - 1).getX(), clamp(0, BAND_WIDTH, POINTS.get(noOfPoints - 1).getY()));
        mapOfBands.forEach((band, pointsInBand) -> {
            Point lastPointInBand = pointsInBand.get(pointsInBand.size() - 1);
            if(noOfPoints - lastPointInBand.getX() > 2) { pointsInBand.add(new Point(noOfPoints - 1, lastPointInBand.getY())); }
            pointsInBand.add(lastPoint);
        });

        return mapOfBands;
    }

    private Data<T> selectDataAt(final double MOUSE_X) {
        int index = clamp(0, getNoOfItems(), (int) (MOUSE_X / scaleX));
        return getSeries().getItems().get(index);
    }

    private String createTooltipText(final Data<T> DATA) {
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("Name: ");
        if (null == DATA.getName() || DATA.getName().isEmpty()) {
            textBuilder.append("-");
        } else {
            textBuilder.append(DATA.getName());
        }
        textBuilder.append("\nX   : ");
        if (null == DATA.getX()) {
            textBuilder.append("-");
        } else {
            textBuilder.append(DATA.getX().toString());
        }
        textBuilder.append("\nY   : ").append(Double.toString(DATA.getY()));
        return textBuilder.toString();
    }

    private void adjustColors() {
        Color aboveColor = series.getColor();
        Color belowColor;
        if (null == aboveColor ||
            Color.WHITE.equals(aboveColor) ||
            Color.BLACK.equals(aboveColor) ||
            Color.TRANSPARENT.equals(aboveColor)) {
            aboveColor = Color.BLUE;
            belowColor = Color.RED;
        } else {
            belowColor = Helper.getComplementaryColor(aboveColor);
        }
        aboveColors = Helper.createColorVariationsAsList(aboveColor, noOfBands);
        belowColors = Helper.createColorVariationsAsList(belowColor, noOfBands);
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        if (width > 0 && height > 0) {
            canvas.setWidth(width);
            canvas.setHeight(height);
            canvas.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);

            // Scale chart in x- and y-direction to visible pane
            scaleX = width / (getNoOfItems() - 1);
            scaleY = height / (getRangeY() / (getNoOfBands()));

            drawChart();
        }
    }

    public void redraw() {
        noOfItems = getSeries().getNoOfItems();
        minY      = getSeries().getItems().stream().mapToDouble(Data::getY).min().getAsDouble();
        maxY      = getSeries().getItems().stream().mapToDouble(Data::getY).max().getAsDouble();
        bandWidth = getRangeY() / getNoOfBands();
        scaleX    = width / (getNoOfItems() - 1);
        scaleY    = height / (getRangeY() / (getNoOfBands()));
        prepareData();
        drawChart();
    }
}
