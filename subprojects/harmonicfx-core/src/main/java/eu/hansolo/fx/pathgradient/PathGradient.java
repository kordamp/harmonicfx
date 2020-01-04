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
package eu.hansolo.fx.pathgradient;


import eu.hansolo.fx.pathgradient.shapes.Path;
import eu.hansolo.fx.pathgradient.tools.GradientLookup;
import eu.hansolo.fx.pathgradient.tools.PathTool;
import eu.hansolo.fx.pathgradient.tools.Point;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class PathGradient {

    public static void strokePathWithGradient(final GraphicsContext CTX, final Path PATH, final GradientLookup GRADIENT_LOOKUP, final double WIDTH, final StrokeLineCap LINE_CAP) {
        Map<Point, Double> samples = samplePath(PATH, 0.1);
        draw(CTX, samples, GRADIENT_LOOKUP, WIDTH, LINE_CAP);
    }

    private static Map<Point, Double> samplePath(final Path PATH, final double PRECISION) {
        PathTool           pathTool         = new PathTool(PATH);
        double             length           = pathTool.getLengthOfPath();
        Map<Point, Double> pointFractionMap = new LinkedHashMap<>();
        for (double i = 0 ; i < length ; i += PRECISION) {
            Point p = pathTool.getPointAtLength(i);
            pointFractionMap.put(p, (i / length));
        }
        pointFractionMap.put(pathTool.getSegmentPointAtLength(length), 1.0);
        return pointFractionMap;
    }

    private static void draw(final GraphicsContext CTX, final Map<Point, Double> SAMPLES, final GradientLookup GRADIENT_LOOKUP, final double WIDTH, final StrokeLineCap LINE_CAP) {
        CTX.save();
        CTX.setLineCap(LINE_CAP);
        CTX.setLineJoin(StrokeLineJoin.ROUND);
        List<Point> points = new ArrayList<>(SAMPLES.keySet());
        Point lastPoint = points.get(0);
        for (int i = 1 ; i < SAMPLES.size() ; i++) {
            Point p0 = points.get(i);

            CTX.setLineWidth(WIDTH);
            CTX.setStroke(GRADIENT_LOOKUP.getColorAt(SAMPLES.get(p0)));
            CTX.strokeLine(lastPoint.getX(), lastPoint.getY(), p0.getX(), p0.getY());


            /*
            double halfLineWidth = WIDTH / 2;
            CTX.setLineWidth(3);
            CTX.setStroke(GRADIENT_LOOKUP.getColorAt(SAMPLES.get(p0)));
            double angle = Math.toDegrees(Math.atan2(p0.getY() - lastPoint.getY(), p0.getX() - lastPoint.getX()));
            double segmentLength = Point.distance(lastPoint, p0);
            double x = lastPoint.getX();
            double y = lastPoint.getY();
            double step = segmentLength / 1;
            for (int l = 0 ; l < segmentLength ; l += step) {
                x += step * Math.cos(Math.toRadians(angle));
                y += step * Math.sin(Math.toRadians(angle));
                double x0 = x + halfLineWidth * Math.cos(Math.toRadians(angle + 90));
                double y0 = y + halfLineWidth * Math.sin(Math.toRadians(angle + 90));
                double x1 = x + halfLineWidth * Math.cos(Math.toRadians(angle - 90));
                double y1 = y + halfLineWidth * Math.sin(Math.toRadians(angle - 90));
                CTX.strokeLine(x0, y0, x1, y1);
            }
            */

            /*
            CTX.setLineWidth(WIDTH);
            CTX.setStroke(GRADIENT_LOOKUP.getColorAt(SAMPLES.get(p0)));
            CTX.beginPath();
            CTX.moveTo(lastPoint.getX(), lastPoint.getY());
            CTX.lineTo(p0.getX(), p0.getY());
            CTX.closePath();
            CTX.stroke();
            */

            lastPoint = p0;
        }
        CTX.restore();
    }
}
