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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.scene.paint.Color;


public class Data<T> {
    private String                _name;
    private StringProperty        name;
    private Color                 _color;
    private ObjectProperty<Color> color;
    private T                     _x;
    private ObjectProperty<T>     x;
    private double                _y;
    private DoubleProperty        y;


    // ******************** Constructors **************************************
    public Data() {
        this("", null, 0, Color.CYAN);
    }
    public Data(final double Y) {
        this("", null, Y, Color.CYAN);
    }
    public Data(final double Y, final Color COLOR) {
        this("", null, Y, COLOR);
    }
    public Data(final T X, final double Y) {
        this("", X, Y, Color.CYAN);
    }
    public Data(final T X, final double Y, final Color COLOR) {
        this("", X, Y, COLOR);
    }
    public Data(final String NAME, final double Y) {
        this(NAME, null, Y, Color.CYAN);
    }
    public Data(final String NAME, final double Y, final Color COLOR) {
        this(NAME, null, Y, COLOR);
    }
    public Data(final String NAME, final T X, final double Y) {
        this(NAME, X, Y, Color.CYAN);
    }
    public Data(final String NAME, final T X, final double Y, final Color COLOR) {
        _name  = NAME;
        _color = COLOR;
        _x     = X;
        _y     = Y;
    }


    // ******************** Methods *******************************************
    public String getName() { return null == name ? _name : name.get(); }
    public void setName(final String NAME) {
        if (null == name) {
            _name = NAME;
        } else {
            name.set(NAME);
        }
    }
    public StringProperty nameProperty() {
        if (null == name) {
            name = new StringPropertyBase(_name) {
                @Override public Object getBean() { return Data.this; }
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
        } else {
            color.set(COLOR);
        }
    }
    public ObjectProperty<Color> colorProperty() {
        if (null == color) {
            color = new ObjectPropertyBase<Color>(_color) {
                @Override public Object getBean() { return Data.this; }
                @Override public String getName() { return "color"; }
            };
            _color = null;
        }
        return color;
    }

    public T getX() { return null == x ? _x : x.get(); }
    public void setX(final T X) {
        if (null == x) {
            _x = X;
        } else {
            x.set(X);
        }
    }
    public ObjectProperty<T> xProperty() {
        if (null == x) {
            x = new ObjectPropertyBase<T>(_x) {
                @Override public Object getBean() { return Data.this; }
                @Override public String getName() { return "x"; }
            };
        }
        return x;
    }

    public double getY() { return null == y ? _y : y.get(); }
    public void setY(final double Y) {
        if (null == y) {
            _y = Y;
        } else {
            y.set(Y);
        }
    }
    public DoubleProperty yProperty() {
        if (null == y) {
            y = new DoublePropertyBase(_y) {
                @Override public Object getBean() { return Data.this; }
                @Override public String getName() { return "y"; }
            };
        }
        return y;
    }

    /**
     * Returns all data in json format
     * @return all data in json format
     */
    @Override public String toString() {
        return new StringBuilder().append("{ \"name\":\"").append(getName()).append("\"")
                                  .append(", \"color\":\"").append(getColor().toString().replace("0x", "#")).append("\"")
                                  .append(", \"x\":").append(getX())
                                  .append(", \"y\":").append(getY())
                                  .append(" }")
                                  .toString();
    }
}