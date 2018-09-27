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
package eu.hansolo.fx.horizonchart;

import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Helper {

    public static final int clamp(final int MIN, final int MAX, final int VALUE) {
        if (VALUE < MIN) return MIN;
        if (VALUE > MAX) return MAX;
        return VALUE;
    }
    public static final long clamp(final long MIN, final long MAX, final long VALUE) {
        if (VALUE < MIN) return MIN;
        if (VALUE > MAX) return MAX;
        return VALUE;
    }
    public static final double clamp(final double MIN, final double MAX, final double VALUE) {
        if (Double.compare(VALUE, MIN) < 0) return MIN;
        if (Double.compare(VALUE, MAX) > 0) return MAX;
        return VALUE;
    }

    public static final List<Color> createColorPalette(final Color FROM_COLOR, final Color TO_COLOR, final int NO_OF_COLORS) {
        int    steps        = clamp(1, 50, NO_OF_COLORS) - 1;
        double step         = 1.0 / steps;
        double deltaRed     = (TO_COLOR.getRed()     - FROM_COLOR.getRed())     * step;
        double deltaGreen   = (TO_COLOR.getGreen()   - FROM_COLOR.getGreen())   * step;
        double deltaBlue    = (TO_COLOR.getBlue()    - FROM_COLOR.getBlue())    * step;
        double deltaOpacity = (TO_COLOR.getOpacity() - FROM_COLOR.getOpacity()) * step;

        List<Color> palette      = new ArrayList<>(NO_OF_COLORS);
        Color       currentColor = FROM_COLOR;
        palette.add(currentColor);
        for (int i = 0 ; i < steps ; i++) {
            double red     = clamp(0d, 1d, (currentColor.getRed()     + deltaRed));
            double green   = clamp(0d, 1d, (currentColor.getGreen()   + deltaGreen));
            double blue    = clamp(0d, 1d, (currentColor.getBlue()    + deltaBlue));
            double opacity = clamp(0d, 1d, (currentColor.getOpacity() + deltaOpacity));
            currentColor   = Color.color(red, green, blue, opacity);
            palette.add(currentColor);
        }
        return palette;
    }

    public static final Color getComplementaryColor(final Color COLOR) {
        return Color.hsb(COLOR.getHue() + 180, COLOR.getSaturation(), COLOR.getBrightness());
    }

    public static final List<Color> createColorVariationsAsList(final Color COLOR, final int NO_OF_COLORS) {
        return Arrays.asList(createColorVariations(COLOR, NO_OF_COLORS));
    }
    public static final Color[] createColorVariations(final Color COLOR, final int NO_OF_COLORS) {
        int    noOfColors = clamp(1, 5, NO_OF_COLORS);
        double step       = 0.8 / noOfColors;
        double hue        = COLOR.getHue();
        double brg        = COLOR.getBrightness();
        Color[] colors = new Color[noOfColors];
        for (int i = 0 ; i < noOfColors ; i++) { colors[i] = Color.hsb(hue, 0.2 + i * step, brg); }
        return colors;
    }

    public static final List<Color> getColorRangeMinMaxAsList(final Color COLOR, final int STEPS) {
        return Arrays.asList(getColorRangeMinMax(COLOR, STEPS));
    }
    public static final Color[] getColorRangeMinMax(final Color COLOR, final int STEPS) {
        double hue            = COLOR.getHue();
        double saturation     = COLOR.getSaturation();
        double brightness     = COLOR.getBrightness();
        double saturationStep = saturation / STEPS;
        double brightnessStep = brightness / STEPS;
        double halfSteps      = STEPS / 2;
        Color fromColor       = COLOR.hsb(hue, saturation, clamp(0, 1, brightness + brightnessStep * halfSteps));
        Color toColor         = COLOR.hsb(hue, saturation, clamp(0, 1, brightness - brightnessStep * halfSteps));
        return new Color[] { fromColor, toColor };
    }

    public static List<Point> subdividePointsAsList(final List<Point> POINTS, final int SUB_DIVISIONS) {
        Point[] points = POINTS.toArray(new Point[0]);
        return Arrays.asList(subdividePoints(points, SUB_DIVISIONS));
    }
    public static Point[] subdividePoints(final Point[] POINTS, final int SUB_DIVISIONS) {
        assert POINTS != null;
        assert POINTS.length >= 3;
        int    noOfPoints = POINTS.length;

        Point[] subdividedPoints = new Point[((noOfPoints - 1) * SUB_DIVISIONS) + 1];

        double increments = 1.0 / (double) SUB_DIVISIONS;

        for (int i = 0 ; i < noOfPoints - 1 ; i++) {
            Point p0 = i == 0 ? POINTS[i] : POINTS[i - 1];
            Point p1 = POINTS[i];
            Point p2 = POINTS[i + 1];
            Point p3 = (i+2 == noOfPoints) ? POINTS[i + 1] : POINTS[i + 2];

            CatmullRom crs = new CatmullRom(p0, p1, p2, p3);

            for (int j = 0 ; j <= SUB_DIVISIONS ; j++) {
                subdividedPoints[(i * SUB_DIVISIONS) + j] = crs.q(j * increments);
            }
        }

        return subdividedPoints;
    }

    public static List<Point> subdividePointsLinearAsList(final List<Point> POINTS, final int SUB_DIVISIONS) {
        Point[] points = POINTS.toArray(new Point[0]);
        return Arrays.asList(subdividePointsLinear(points, SUB_DIVISIONS));
    }
    public static Point[] subdividePointsLinear(final Point[] POINTS, final int SUB_DIVISIONS) {
        assert  POINTS != null;
        assert  POINTS.length >= 3;

        int     noOfPoints       = POINTS.length;
        Point[] subdividedPoints = new Point[((noOfPoints - 1) * SUB_DIVISIONS) + 1];
        double  stepSize         = (POINTS[1].getX() - POINTS[0].getX()) / SUB_DIVISIONS;
        for (int i = 0 ; i < noOfPoints - 1 ; i++) {
            for (int j = 0 ; j <= SUB_DIVISIONS ; j++) {
                subdividedPoints[(i * SUB_DIVISIONS) + j] = calcIntermediatePoint(POINTS[i], POINTS[i+1], stepSize * j);
            }
        }
        return subdividedPoints;
    }

    public static Point calcIntermediatePoint(final Point LEFT_POINT, final Point RIGHT_POINT, final double INTERVAL_X) {
        double m = (RIGHT_POINT.getY() - LEFT_POINT.getY()) / (RIGHT_POINT.getX() - LEFT_POINT.getX());
        double x = INTERVAL_X;
        double y = m * x;
        return new Point(LEFT_POINT.getX() + x, LEFT_POINT.getY() + y);
    }

    public static Point calcIntersectionPoint(final Point LEFT_POINT, final Point RIGHT_POINT, final double INTERSECTION_Y) {
        double[] xy = calculateInterSectionPoint(LEFT_POINT.getX(), LEFT_POINT.getY(), RIGHT_POINT.getX(), RIGHT_POINT.getY(), INTERSECTION_Y);
        return new Point(xy[0], xy[1]);
    }
    public static double[] calculateInterSectionPoint(final Point LEFT_POINT, final Point RIGHT_POINT, final double INTERSECTION_Y) {
        return calculateInterSectionPoint(LEFT_POINT.getX(), LEFT_POINT.getY(), RIGHT_POINT.getX(), RIGHT_POINT.getY(), INTERSECTION_Y);
    }
    public static double[] calculateInterSectionPoint(final double X1, final double Y1, final double X2, final double Y2, final double INTERSECTION_Y) {
        double m = (Y2 - Y1) / (X2 - X1);
        double interSectionX = (INTERSECTION_Y - Y1) / m;
        return new double[] { X1 + interSectionX, INTERSECTION_Y };
    }

    public static <X, Y extends Number> List<Data> convertFromXYChartData(final List<XYChart.Data<X, Y>> DATA_LIST) {
        List<Data> dataList = new ArrayList<>(DATA_LIST.size());
        DATA_LIST.forEach(xyChartData -> dataList.add(new Data(xyChartData.getXValue(), xyChartData.getYValue().doubleValue())));
        return dataList;
    }
}
