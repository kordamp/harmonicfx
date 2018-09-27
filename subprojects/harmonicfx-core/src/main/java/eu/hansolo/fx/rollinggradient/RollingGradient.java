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
package eu.hansolo.fx.rollinggradient;

import eu.hansolo.fx.rollinggradient.event.UpdateEvent;
import eu.hansolo.fx.rollinggradient.event.UpdateEventListener;
import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Shape;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class RollingGradient {
    public enum Speed {
        SLOW(60_000_000),
        NORMAL(40_000_000),
        FAST(10_000_000);

        private final long interval;

        Speed(final long INTERVAL) {
            interval = INTERVAL;
        }

        public long getInterval() { return interval; }
    }
    public enum Direction { LEFT, RIGHT }

    private LinearGradient            gradient;
    private List<UpdateEventListener> listeners;
    private Color                     firstColor;
    private Color                     secondColor;
    private int                       x;
    private double                    limit;
    private int                       period;
    private boolean                   smoothGradient;
    private Direction                 direction;
    private Shape                     shape;
    private Stop[]                    stops;
    private long                      lastTimerCall;
    private long                      interval;
    private AnimationTimer            timer;


    // ******************** Constructors **************************************
    public RollingGradient() {
        this(Color.rgb(88, 154, 227), Color.rgb(50, 130, 222), true, Direction.LEFT, Speed.NORMAL.getInterval());
    }
    public RollingGradient(final Color FIRST_COLOR, final Color SECOND_COLOR) {
        this(FIRST_COLOR, SECOND_COLOR, false, Direction.LEFT, Speed.NORMAL.getInterval());
    }
    public RollingGradient(final Color FIRST_COLOR, final Color SECOND_COLOR, final boolean SMOOTH_GRADIENT, final Direction DIRECTION, final long INTERVAL) {
        listeners      = new CopyOnWriteArrayList<>();
        firstColor     = FIRST_COLOR;
        secondColor    = SECOND_COLOR;
        x              = 10;
        period         = 10;
        limit          = period * 2;
        smoothGradient = SMOOTH_GRADIENT;
        direction      = DIRECTION;
        lastTimerCall  = System.nanoTime();
        interval       = Helper.clamp(10_000_000, 60_000_000, INTERVAL);
        timer          = new AnimationTimer() {
            @Override public void handle(final long now) {
                if (now > lastTimerCall + interval) {
                    gradient = new LinearGradient(period - x, 0, limit - x, 0, false, CycleMethod.REFLECT, stops);

                    if (!listeners.isEmpty()) { fireUpdateEvent(new UpdateEvent(gradient)); }
                    if (null != shape) { shape.setFill(gradient); }

                    if (Direction.RIGHT == direction) {
                        x--;
                        if (x < 0) { x = (int) limit; }
                    } else {
                        x++;
                        if (x >= limit) { x = 0; }
                    }
                    lastTimerCall = now;
                }
            }
        };
        setupStops();
    }


    // ******************** Methods *******************************************
    public void start() { timer.start(); }
    public void stop() { timer.stop(); }

    public Color getFirstColor() { return firstColor; }
    public void setFirstColor(final Color COLOR) {
        firstColor = COLOR;
        setupStops();
    }

    public Color getSecondColor() { return secondColor; }
    public void setSecondColor(final Color COLOR) {
        secondColor = COLOR;
        setupStops();
    }

    public boolean isSmoothGradient() { return smoothGradient; }
    public void setSmoothGradient(final boolean SMOOTH) {
        smoothGradient = SMOOTH;
        setupStops();
    }

    public Direction getDirection() { return direction; }
    public void setDirection(final Direction DIRECTION) { direction = DIRECTION; }

    public long getInterval() { return interval; }
    public void setInterval(final long INTERVAL) { interval = Helper.clamp(10_000_000, 60_000_000, INTERVAL); }
    public void setInterval(final Speed SPEED) { setInterval(SPEED.getInterval()); }

    public int getPeriod() { return period; }
    public void setPeriod(final int PERIOD) {
        period = Helper.clamp(2, 1000, PERIOD);
        limit  = period * 2;
    }

    public Shape getShape() { return shape; }
    public void setShape(final Shape SHAPE) {
        if (null == SHAPE) { throw new IllegalArgumentException("Shape cannot be null"); }
        shape    = SHAPE;
        gradient = new LinearGradient(period, 0,
                                      2 * period, 0,
                                      false, CycleMethod.REFLECT,
                                      new Stop(0.0, firstColor),
                                      new Stop(0.01, firstColor),
                                      new Stop(0.99, secondColor),
                                      new Stop(1.0, secondColor));
        shape.setFill(gradient);
    }

    public void dispose() {
        timer.stop();
        listeners.clear();
        stops       = null;
        firstColor  = null;
        secondColor = null;
        gradient    = null;
    }

    private void setupStops() {
        if (isSmoothGradient()) {
            stops = new Stop[] { new Stop(0.0, firstColor), new Stop(1.0, secondColor) };
        } else {
            stops = new Stop[] { new Stop(0.0, firstColor),
                                 new Stop(0.5, firstColor),
                                 new Stop(0.5, secondColor),
                                 new Stop(1.0, secondColor) };
        }
    }


    // ******************** Event Handling ************************************
    public void setOnUpdateEvent(final UpdateEventListener LISTENER) { addUpdateEventListener(LISTENER); }
    public void addUpdateEventListener(final UpdateEventListener LISTENER) { if (!listeners.contains(LISTENER)) { listeners.add(LISTENER); }}
    public void removeUpdateEventListener(final UpdateEventListener LISTENER) { if (listeners.contains(LISTENER)) { listeners.remove(LISTENER); } }

    private void fireUpdateEvent(final UpdateEvent EVENT) {
        for(UpdateEventListener listener : listeners) { listener.onUpdateEvent(EVENT); }
    }
}
