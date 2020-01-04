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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class Series<T> {
    public  final SeriesEvent REFRESH = new SeriesEvent(Series.this, SeriesEventType.REDRAW);
    private String                                    _name;
    private StringProperty                            name;
    private Color                                     _color;
    private ObjectProperty<Color>                     color;
    private ObservableList<Data<T>>                   items;
    private CopyOnWriteArrayList<SeriesEventListener> listeners;


    // ******************** Constructors **************************************
    public Series() {
        this(null, "", Color.CYAN);
    }
    public Series(final List<Data<T>> ITEMS) {
        this(ITEMS, "", Color.CYAN);
    }
    public Series(final List<Data<T>> ITEMS, final Color COLOR) {
        this(ITEMS, "", COLOR);
    }
    public Series(final List<Data<T>> ITEMS, final Color COLOR, final Paint FILL) {
        this(ITEMS, "", COLOR);
    }
    public Series(final List<Data<T>> ITEMS, final String NAME) {
        this(ITEMS, NAME, Color.CYAN);
    }
    public Series(final List<Data<T>> ITEMS, final String NAME, final Color COLOR) {
        _name     = NAME;
        _color    = COLOR;
        items     = FXCollections.observableArrayList();
        listeners = new CopyOnWriteArrayList<>();

        if (null != ITEMS) { items.setAll(ITEMS); }
    }


    // ******************** Methods *******************************************
    public ObservableList<Data<T>> getItems() { return items; }
    public void setItems(final List<Data<T>> ITEMS) { items.setAll(ITEMS); }

    public String getName() { return null == name ? _name : name.get(); }
    public void setName(final String NAME) {
        if (null == name) {
            _name = NAME;
            fireSeriesEvent(REFRESH);
        } else {
            name.set(NAME);
        }
    }
    public StringProperty nameProperty() {
        if (null == name) {
            name = new StringPropertyBase(_name) {
                @Override protected void invalidated() { fireSeriesEvent(REFRESH); }
                @Override public Object getBean() { return Series.this; }
                @Override public String getName() { return "name"; }
            };
            _name = null;
        }
        return name;
    }

    public Color getColor() { return null == color ? _color : color.get(); }
    public void setColor(final Color COLOR) {
        if (null == color) {
            _color = COLOR;
            refresh();
        } else {
            color.set(COLOR);
        }
    }
    public ObjectProperty<Color> colorProperty() {
        if (null == color) {
            color = new ObjectPropertyBase<Color>(_color) {
                @Override protected void invalidated() { refresh(); }
                @Override public Object getBean() {  return Series.this; }
                @Override public String getName() { return "color"; }
            };
            _color = null;
        }
        return color;
    }

    public int getNoOfItems() { return items.size(); }

    public double getMinY() { return items.stream().min(Comparator.comparingDouble(Data::getY)).get().getY(); }
    public double getMaxY() { return items.stream().max(Comparator.comparingDouble(Data::getY)).get().getY(); }
    public double getRangeY() { return getMaxY() - getMinY(); }

    public void refresh() { fireSeriesEvent(REFRESH); }

    /**
     * Returns all items in json format
     * @return all items in json format
     */
    @Override public String toString() {
        StringBuilder json = new StringBuilder();
        json.append("{\n").append("  \"data\":[\n");
        items.forEach(item -> json.append("    ").append(item.toString()).append(",\n"));
        json.setLength(json.length() - 2);
        json.append("\n");
        json.append("  ]\n}");
        return json.toString();
    }


    // ******************** Event handling ************************************
    public void setOnSeriesEvent(final SeriesEventListener LISTENER) { addSeriesEventListener(LISTENER); }
    public void addSeriesEventListener(final SeriesEventListener LISTENER) { if (!listeners.contains(LISTENER)) listeners.add(LISTENER); }
    public void removeSeriesEventListener(final SeriesEventListener LISTENER) { if (listeners.contains(LISTENER)) listeners.remove(LISTENER); }

    public void fireSeriesEvent(final SeriesEvent EVENT) {
        for (SeriesEventListener listener : listeners) { listener.onSeriesEvent(EVENT); }
    }
}
