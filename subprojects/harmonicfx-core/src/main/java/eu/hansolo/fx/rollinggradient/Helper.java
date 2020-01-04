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
package eu.hansolo.fx.rollinggradient;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.function.Predicate;


public class Helper {
    private static final double  EPSILON           = 1E-6;

    public static final double   MAP_WIDTH         = 1009.1149817705154 - 1.154000163078308;
    public static final double   MAP_HEIGHT        = 665.2420043945312;
    public static final double   MAP_OFFSET_X      = -MAP_WIDTH * 0.0285;
    public static final double   MAP_OFFSET_Y      = MAP_HEIGHT * 0.195;

    public static final double   MIN_FONT_SIZE     = 5;

    public static final <T extends Number> T clamp(final T MIN, final T MAX, final T VALUE) {
        if (VALUE.doubleValue() < MIN.doubleValue()) return MIN;
        if (VALUE.doubleValue() > MAX.doubleValue()) return MAX;
        return VALUE;
    }

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

    public static final double clampMin(final double MIN, final double VALUE) {
        if (VALUE < MIN) return MIN;
        return VALUE;
    }
    public static final double clampMax(final double MAX, final double VALUE) {
        if (VALUE > MAX) return MAX;
        return VALUE;
    }

    public static final double round(final double VALUE, final int PRECISION) {
        final int SCALE = (int) Math.pow(10, PRECISION);
        return (double) Math.round(VALUE * SCALE) / SCALE;
    }

    public static final double nearest(final double SMALLER, final double VALUE, final double LARGER) {
        return (VALUE - SMALLER) < (LARGER - VALUE) ? SMALLER : LARGER;
    }

    public static int roundDoubleToInt(final double VALUE){
        double dAbs = Math.abs(VALUE);
        int    i      = (int) dAbs;
        double result = dAbs - (double) i;
        if (result < 0.5) {
            return VALUE < 0 ? -i : i;
        } else {
            return VALUE < 0 ? -(i + 1) : i + 1;
        }
    }

    public static final double[] calcAutoScale(final double MIN_VALUE, final double MAX_VALUE) {
        double maxNoOfMajorTicks = 10;
        double maxNoOfMinorTicks = 10;
        double niceMinValue;
        double niceMaxValue;
        double niceRange;
        double majorTickSpace;
        double minorTickSpace;
        niceRange      = (calcNiceNumber((MAX_VALUE - MIN_VALUE), false));
        majorTickSpace = calcNiceNumber(niceRange / (maxNoOfMajorTicks - 1), true);
        niceMinValue   = (Math.floor(MIN_VALUE / majorTickSpace) * majorTickSpace);
        niceMaxValue   = (Math.ceil(MAX_VALUE / majorTickSpace) * majorTickSpace);
        minorTickSpace = calcNiceNumber(majorTickSpace / (maxNoOfMinorTicks - 1), true);
        return new double[]{ niceMinValue, niceMaxValue, majorTickSpace, minorTickSpace };
    }

    /**
     * Can be used to implement discrete steps e.g. on a slider.
     * @param MIN_VALUE          The min value of the range
     * @param MAX_VALUE          The max value of the range
     * @param VALUE              The value to snap
     * @param MINOR_TICK_COUNT   The number of ticks between 2 major tick marks
     * @param MAJOR_TICK_UNIT    The distance between 2 major tick marks
     * @return The value snapped to the next tick mark defined by the given parameters
     */
    public static double snapToTicks(final double MIN_VALUE, final double MAX_VALUE, final double VALUE, final int MINOR_TICK_COUNT, final double MAJOR_TICK_UNIT) {
        double v = VALUE;
        int    minorTickCount = clamp(0, 10, MINOR_TICK_COUNT);
        double majorTickUnit  = Double.compare(MAJOR_TICK_UNIT, 0.0) <= 0 ? 0.25 : MAJOR_TICK_UNIT;
        double tickSpacing;

        if (minorTickCount != 0) {
            tickSpacing = majorTickUnit / (Math.max(minorTickCount, 0) + 1);
        } else {
            tickSpacing = majorTickUnit;
        }

        int    prevTick      = (int) ((v - MIN_VALUE) / tickSpacing);
        double prevTickValue = prevTick * tickSpacing + MIN_VALUE;
        double nextTickValue = (prevTick + 1) * tickSpacing + MIN_VALUE;

        v = nearest(prevTickValue, v, nextTickValue);

        return clamp(MIN_VALUE, MAX_VALUE, v);
    }

    /**
     * Returns a "niceScaling" number approximately equal to the range.
     * Rounds the number if ROUND == true.
     * Takes the ceiling if ROUND = false.
     *
     * @param RANGE the value range (maxValue - minValue)
     * @param ROUND whether to round the result or ceil
     * @return a "niceScaling" number to be used for the value range
     */
    public static final double calcNiceNumber(final double RANGE, final boolean ROUND) {
        double niceFraction;
        double exponent = Math.floor(Math.log10(RANGE));   // exponent of range
        double fraction = RANGE / Math.pow(10, exponent);  // fractional part of range

        if (ROUND) {
            if (Double.compare(fraction, 1.5) < 0) {
                niceFraction = 1;
            } else if (Double.compare(fraction, 3)  < 0) {
                niceFraction = 2;
            } else if (Double.compare(fraction, 7) < 0) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        } else {
            if (Double.compare(fraction, 1) <= 0) {
                niceFraction = 1;
            } else if (Double.compare(fraction, 2) <= 0) {
                niceFraction = 2;
            } else if (Double.compare(fraction, 5) <= 0) {
                niceFraction = 5;
            } else {
                niceFraction = 10;
            }
        }
        return niceFraction * Math.pow(10, exponent);
    }

    public static final void adjustTextSize(final Text TEXT, final double MAX_WIDTH, final double FONT_SIZE) {
        final String FONT_NAME          = TEXT.getFont().getName();
        double       adjustableFontSize = FONT_SIZE;

        while (TEXT.getBoundsInLocal().getWidth() > MAX_WIDTH && adjustableFontSize > MIN_FONT_SIZE) {
            adjustableFontSize -= 0.05;
            TEXT.setFont(new Font(FONT_NAME, adjustableFontSize));
        }
    }
    public static final void adjustTextSize(final Label TEXT, final double MAX_WIDTH, final double FONT_SIZE) {
        final String FONT_NAME          = TEXT.getFont().getName();
        double       adjustableFontSize = FONT_SIZE;

        while (TEXT.getBoundsInLocal().getWidth() > MAX_WIDTH && adjustableFontSize > MIN_FONT_SIZE) {
            adjustableFontSize -= 0.05;
            TEXT.setFont(new Font(FONT_NAME, adjustableFontSize));
        }
    }

    public static final void fitNodeWidth(final Node NODE, final double MAX_WIDTH) {
        NODE.setVisible(NODE.getLayoutBounds().getWidth() < MAX_WIDTH);
        //enableNode(NODE, NODE.getLayoutBounds().getWidth() < MAX_WIDTH);
    }

    public static final DateTimeFormatter getDateFormat(final Locale LOCALE) {
        if (Locale.US == LOCALE) {
            return DateTimeFormatter.ofPattern("MM/dd/YYYY");
        } else if (Locale.CHINA == LOCALE) {
            return DateTimeFormatter.ofPattern("YYYY.MM.dd");
        } else {
            return DateTimeFormatter.ofPattern("dd.MM.YYYY");
        }
    }
    public static final DateTimeFormatter getLocalizedDateFormat(final Locale LOCALE) {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(LOCALE);
    }

    public static final void enableNode(final Node NODE, final boolean ENABLE) {
        NODE.setManaged(ENABLE);
        NODE.setVisible(ENABLE);
    }

    public static final String colorToCss(final Color COLOR) {
        return COLOR.toString().replace("0x", "#");
    }

    public static final ThreadFactory getThreadFactory(final String THREAD_NAME, final boolean IS_DAEMON) {
        return runnable -> {
            Thread thread = new Thread(runnable, THREAD_NAME);
            thread.setDaemon(IS_DAEMON);
            return thread;
        };
    }

    public static final void stopTask(ScheduledFuture<?> task) {
        if (null == task) return;
        task.cancel(true);
        task = null;
    }

    public static final boolean isMonochrome(final Color COLOR) {
        return Double.compare(COLOR.getRed(), COLOR.getGreen()) == 0 && Double.compare(COLOR.getGreen(), COLOR.getBlue()) == 0;
    }

    public static final double colorDistance(final Color COLOR_1, final Color COLOR_2) {
        final double DELTA_R = (COLOR_2.getRed()   - COLOR_1.getRed());
        final double DELTA_G = (COLOR_2.getGreen() - COLOR_1.getGreen());
        final double DELTA_B = (COLOR_2.getBlue()  - COLOR_1.getBlue());

        return Math.sqrt(DELTA_R * DELTA_R + DELTA_G * DELTA_G + DELTA_B * DELTA_B);
    }

    public static double[] colorToYUV(final Color COLOR) {
        final double WEIGHT_FACTOR_RED   = 0.299;
        final double WEIGHT_FACTOR_GREEN = 0.587;
        final double WEIGHT_FACTOR_BLUE  = 0.144;
        final double U_MAX               = 0.436;
        final double V_MAX               = 0.615;
        double y = clamp(0, 1, WEIGHT_FACTOR_RED * COLOR.getRed() + WEIGHT_FACTOR_GREEN * COLOR.getGreen() + WEIGHT_FACTOR_BLUE * COLOR.getBlue());
        double u = clamp(-U_MAX, U_MAX, U_MAX * ((COLOR.getBlue() - y) / (1 - WEIGHT_FACTOR_BLUE)));
        double v = clamp(-V_MAX, V_MAX, V_MAX * ((COLOR.getRed() - y) / (1 - WEIGHT_FACTOR_RED)));
        return new double[] { y, u, v };
    }

    public static final boolean isBright(final Color COLOR) { return Double.compare(colorToYUV(COLOR)[0], 0.5) >= 0.0; }
    public static final boolean isDark(final Color COLOR) { return colorToYUV(COLOR)[0] < 0.5; }

    public static final Color getContrastColor(final Color COLOR) {
        return COLOR.getBrightness() > 0.5 ? Color.BLACK : Color.WHITE;
    }

    public static final Color getColorWithOpacity(final Color COLOR, final double OPACITY) {
        return Color.color(COLOR.getRed(), COLOR.getGreen(), COLOR.getBlue(), clamp(0.0, 1.0, OPACITY));
    }

    public static final List<Color> createColorPalette(final Color FROM_COLOR, final Color TO_COLOR, final int NO_OF_COLORS) {
        int    steps        = clamp(1, 12, NO_OF_COLORS) - 1;
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

    public static final Color[] createColorVariations(final Color COLOR, final int NO_OF_COLORS) {
        int    noOfColors = clamp(1, 12, NO_OF_COLORS);
        double step       = 0.8 / noOfColors;
        double hue        = COLOR.getHue();
        double brg        = COLOR.getBrightness();
        Color[] colors = new Color[noOfColors];
        for (int i = 0 ; i < noOfColors ; i++) { colors[i] = Color.hsb(hue, 0.2 + i * step, brg); }
        return colors;
    }

    public static final Color getColorAt(final List<Stop> STOP_LIST, final double POSITION_OF_COLOR) {
        Map<Double, Stop> STOPS = new TreeMap<>();
        for (Stop stop : STOP_LIST) { STOPS.put(stop.getOffset(), stop); }

        if (STOPS.isEmpty()) return Color.BLACK;

        double minFraction = Collections.min(STOPS.keySet());
        double maxFraction = Collections.max(STOPS.keySet());

        if (Double.compare(minFraction, 0d) > 0) { STOPS.put(0.0, new Stop(0.0, STOPS.get(minFraction).getColor())); }
        if (Double.compare(maxFraction, 1d) < 0) { STOPS.put(1.0, new Stop(1.0, STOPS.get(maxFraction).getColor())); }

        final double POSITION = clamp(0d, 1d, POSITION_OF_COLOR);
        final Color COLOR;
        if (STOPS.size() == 1) {
            final Map<Double, Color> ONE_ENTRY = (Map<Double, Color>) STOPS.entrySet().iterator().next();
            COLOR = STOPS.get(ONE_ENTRY.keySet().iterator().next()).getColor();
        } else {
            Stop lowerBound = STOPS.get(0.0);
            Stop upperBound = STOPS.get(1.0);
            for (Double fraction : STOPS.keySet()) {
                if (Double.compare(fraction,POSITION) < 0) {
                    lowerBound = STOPS.get(fraction);
                }
                if (Double.compare(fraction, POSITION) > 0) {
                    upperBound = STOPS.get(fraction);
                    break;
                }
            }
            COLOR = interpolateColor(lowerBound, upperBound, POSITION);
        }
        return COLOR;
    }
    public static final Color interpolateColor(final Stop LOWER_BOUND, final Stop UPPER_BOUND, final double POSITION) {
        final double POS  = (POSITION - LOWER_BOUND.getOffset()) / (UPPER_BOUND.getOffset() - LOWER_BOUND.getOffset());

        final double DELTA_RED     = (UPPER_BOUND.getColor().getRed()     - LOWER_BOUND.getColor().getRed())     * POS;
        final double DELTA_GREEN   = (UPPER_BOUND.getColor().getGreen()   - LOWER_BOUND.getColor().getGreen())   * POS;
        final double DELTA_BLUE    = (UPPER_BOUND.getColor().getBlue()    - LOWER_BOUND.getColor().getBlue())    * POS;
        final double DELTA_OPACITY = (UPPER_BOUND.getColor().getOpacity() - LOWER_BOUND.getColor().getOpacity()) * POS;

        double red     = clamp(0, 1, (LOWER_BOUND.getColor().getRed()     + DELTA_RED));
        double green   = clamp(0, 1, (LOWER_BOUND.getColor().getGreen()   + DELTA_GREEN));
        double blue    = clamp(0, 1, (LOWER_BOUND.getColor().getBlue()    + DELTA_BLUE));
        double opacity = clamp(0, 1, (LOWER_BOUND.getColor().getOpacity() + DELTA_OPACITY));

        return Color.color(red, green, blue, opacity);
    }

    public static final void scaleNodeTo(final Node NODE, final double TARGET_WIDTH, final double TARGET_HEIGHT) {
        NODE.setScaleX(TARGET_WIDTH / NODE.getLayoutBounds().getWidth());
        NODE.setScaleY(TARGET_HEIGHT / NODE.getLayoutBounds().getHeight());
    }

    public static final String normalize(final String TEXT) {
        String normalized = TEXT.replaceAll("\u00fc", "ue")
                                .replaceAll("\u00f6", "oe")
                                .replaceAll("\u00e4", "ae")
                                .replaceAll("\u00df", "ss");

        normalized = normalized.replaceAll("\u00dc(?=[a-z\u00fc\u00f6\u00e4\u00df ])", "Ue")
                               .replaceAll("\u00d6(?=[a-z\u00fc\u00f6\u00e4\u00df ])", "Oe")
                               .replaceAll("\u00c4(?=[a-z\u00fc\u00f6\u00e4\u00df ])", "Ae");

        normalized = normalized.replaceAll("\u00dc", "UE")
                               .replaceAll("\u00d6", "OE")
                               .replaceAll("\u00c4", "AE");
        return normalized;
    }

    public static final boolean equals(final double A, final double B) { return A == B || Math.abs(A - B) < EPSILON; }
    public static final boolean biggerThan(final double A, final double B) { return (A - B) > EPSILON; }
    public static final boolean lessThan(final double A, final double B) { return (B - A) > EPSILON; }

    public static final boolean isInRectangle(final double X, final double Y,
                                              final double MIN_X, final double MIN_Y,
                                              final double MAX_X, final double MAX_Y) {
        return (Double.compare(X, MIN_X) >= 0 &&
                Double.compare(X, MAX_X) <= 0 &&
                Double.compare(Y, MIN_Y) >= 0 &&
                Double.compare(Y, MAX_Y) <= 0);
    }

    public static final boolean isInEllipse(final double X, final double Y,
                                            final double ELLIPSE_CENTER_X, final double ELLIPSE_CENTER_Y,
                                            final double ELLIPSE_RADIUS_X, final double ELLIPSE_RADIUS_Y) {
        return Double.compare(((((X - ELLIPSE_CENTER_X) * (X - ELLIPSE_CENTER_X)) / (ELLIPSE_RADIUS_X * ELLIPSE_RADIUS_X)) +
                               (((Y - ELLIPSE_CENTER_Y) * (Y - ELLIPSE_CENTER_Y)) / (ELLIPSE_RADIUS_Y * ELLIPSE_RADIUS_Y))), 1) <= 0.0;
    }

    public static final boolean isInPolygon(final double X, final double Y, final Polygon POLYGON) {
        List<Double> points              = POLYGON.getPoints();
        int          noOfPointsInPolygon = POLYGON.getPoints().size() / 2;
        double[]     pointsX             = new double[noOfPointsInPolygon];
        double[]     pointsY             = new double[noOfPointsInPolygon];
        int          pointCounter        = 0;
        for (int i = 0 ; i < points.size() ; i++) {
            if (i % 2 == 0) {
                pointsX[i] = points.get(pointCounter);
            } else {
                pointsY[i] = points.get(pointCounter);
                pointCounter++;
            }
        }
        return isInPolygon(X, Y, noOfPointsInPolygon, pointsX, pointsY);
    }
    public static final boolean isInPolygon(final double X, final double Y, final int NO_OF_POINTS_IN_POLYGON, final double[] POINTS_X, final double[] POINTS_Y) {
        if (NO_OF_POINTS_IN_POLYGON != POINTS_X.length || NO_OF_POINTS_IN_POLYGON != POINTS_Y.length) { return false; }
        boolean inside = false;
        for (int i = 0, j = NO_OF_POINTS_IN_POLYGON - 1; i < NO_OF_POINTS_IN_POLYGON ; j = i++) {
            if (((POINTS_Y[i] > Y) != (POINTS_Y[j] > Y)) && (X < (POINTS_X[j] - POINTS_X[i]) * (Y - POINTS_Y[i]) / (POINTS_Y[j] - POINTS_Y[i]) + POINTS_X[i])) {
                inside = !inside;
            }
        }
        return inside;
    }

    public static final boolean isInRingSegment(final double X, final double Y,
                                                final double CENTER_X, final double CENTER_Y,
                                                final double OUTER_RADIUS, final double INNER_RADIUS,
                                                final double START_ANGLE, final double SEGMENT_ANGLE) {
        double angleOffset = 90.0;
        double pointRadius = Math.sqrt((X - CENTER_X) * (X - CENTER_X) + (Y - CENTER_Y) * (Y - CENTER_Y));
        double pointAngle  = getAngleFromXY(X, Y, CENTER_X, CENTER_Y, angleOffset);
        double startAngle  = angleOffset - START_ANGLE;
        double endAngle    = startAngle + SEGMENT_ANGLE;

        return (Double.compare(pointRadius, INNER_RADIUS) >= 0 &&
                Double.compare(pointRadius, OUTER_RADIUS) <= 0 &&
                Double.compare(pointAngle, startAngle) >= 0 &&
                Double.compare(pointAngle, endAngle) <= 0);
    }

    public static final double getAngleFromXY(final double X, final double Y, final double CENTER_X, final double CENTER_Y) {
        return getAngleFromXY(X, Y, CENTER_X, CENTER_Y, 90.0);
    }
    public static final double getAngleFromXY(final double X, final double Y, final double CENTER_X, final double CENTER_Y, final double ANGLE_OFFSET) {
        // For ANGLE_OFFSET =  0 -> Angle of 0 is at 3 o'clock
        // For ANGLE_OFFSET = 90  ->Angle of 0 is at 12 o'clock
        double deltaX      = X - CENTER_X;
        double deltaY      = Y - CENTER_Y;
        double radius      = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
        double nx          = deltaX / radius;
        double ny          = deltaY / radius;
        double theta       = Math.atan2(ny, nx);
        theta              = Double.compare(theta, 0.0) >= 0 ? Math.toDegrees(theta) : Math.toDegrees((theta)) + 360.0;
        double angle       = (theta + ANGLE_OFFSET) % 360;
        return angle;
    }

    public static final double[] latLonToXY(final double LATITUDE, final double LONGITUDE) {
        return latLonToXY(LATITUDE, LONGITUDE, MAP_OFFSET_X, MAP_OFFSET_Y);
    }
    public static final double[] latLonToXY(final double LATITUDE, final double LONGITUDE, final double MAP_OFFSET_X, final double MAP_OFFSET_Y) {
        double x = (LONGITUDE + 180) * (MAP_WIDTH / 360) + MAP_OFFSET_X;
        double y = (MAP_HEIGHT / 2) - (MAP_WIDTH * (Math.log(Math.tan((Math.PI / 4) + (Math.toRadians(LATITUDE) / 2)))) / (2 * Math.PI)) + MAP_OFFSET_Y;
        return new double[]{ x, y };
    }

    public static final <T> Predicate<T> not(Predicate<T> predicate) { return predicate.negate(); }
}
