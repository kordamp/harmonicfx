package eu.hansolo.fx.yotaclock;

import javafx.beans.property.*;
import javafx.geometry.Dimension2D;
import javafx.scene.paint.Color;

import java.time.LocalDateTime;
import java.util.HashMap;


/**
 * Created by hansolo on 08.12.14.
 */
public class YotaClockBuilder <B extends YotaClockBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected YotaClockBuilder() {}


    // ******************** Methods *******************************************
    public static final YotaClockBuilder create() {
        return new YotaClockBuilder();
    }

    public final B backgroundColor(final Color COLOR) {
        properties.put("backgroundColor", new SimpleObjectProperty<>(COLOR));
        return (B) this;
    }

    public final B foregroundColor(final Color COLOR) {
        properties.put("foregroundColor", new SimpleObjectProperty<>(COLOR));
        return (B) this;
    }

    public final B running(final boolean RUNNING) {
        properties.put("running", new SimpleBooleanProperty(RUNNING));
        return (B) this;
    }

    public final B inverted(final boolean INVERTED) {
        properties.put("inverted", new SimpleBooleanProperty(INVERTED));
        return (B) this;
    }

    public final B dateTime(final LocalDateTime DATE_TIME) {
        properties.put("dateTime", new SimpleObjectProperty<>(DATE_TIME));
        return (B) this;
    }

    public final B prefSize(final double WIDTH, final double HEIGHT) {
        properties.put("prefSize", new SimpleObjectProperty<>(new Dimension2D(WIDTH, HEIGHT)));
        return (B)this;
    }
    public final B minSize(final double WIDTH, final double HEIGHT) {
        properties.put("minSize", new SimpleObjectProperty<>(new Dimension2D(WIDTH, HEIGHT)));
        return (B)this;
    }
    public final B maxSize(final double WIDTH, final double HEIGHT) {
        properties.put("maxSize", new SimpleObjectProperty<>(new Dimension2D(WIDTH, HEIGHT)));
        return (B)this;
    }

    public final B prefWidth(final double PREF_WIDTH) {
        properties.put("prefWidth", new SimpleDoubleProperty(PREF_WIDTH));
        return (B)this;
    }
    public final B prefHeight(final double PREF_HEIGHT) {
        properties.put("prefHeight", new SimpleDoubleProperty(PREF_HEIGHT));
        return (B)this;
    }

    public final B minWidth(final double MIN_WIDTH) {
        properties.put("minWidth", new SimpleDoubleProperty(MIN_WIDTH));
        return (B)this;
    }
    public final B minHeight(final double MIN_HEIGHT) {
        properties.put("minHeight", new SimpleDoubleProperty(MIN_HEIGHT));
        return (B)this;
    }

    public final B maxWidth(final double MAX_WIDTH) {
        properties.put("maxWidth", new SimpleDoubleProperty(MAX_WIDTH));
        return (B)this;
    }
    public final B maxHeight(final double MAX_HEIGHT) {
        properties.put("maxHeight", new SimpleDoubleProperty(MAX_HEIGHT));
        return (B)this;
    }

    public final B scaleX(final double SCALE_X) {
        properties.put("scaleX", new SimpleDoubleProperty(SCALE_X));
        return (B)this;
    }
    public final B scaleY(final double SCALE_Y) {
        properties.put("scaleY", new SimpleDoubleProperty(SCALE_Y));
        return (B)this;
    }

    public final B layoutX(final double LAYOUT_X) {
        properties.put("layoutX", new SimpleDoubleProperty(LAYOUT_X));
        return (B)this;
    }
    public final B layoutY(final double LAYOUT_Y) {
        properties.put("layoutY", new SimpleDoubleProperty(LAYOUT_Y));
        return (B)this;
    }

    public final B translateX(final double TRANSLATE_X) {
        properties.put("translateX", new SimpleDoubleProperty(TRANSLATE_X));
        return (B)this;
    }
    public final B translateY(final double TRANSLATE_Y) {
        properties.put("translateY", new SimpleDoubleProperty(TRANSLATE_Y));
        return (B)this;
    }

    public final B opacity(final double OPACITY) {
        properties.put("opacity", new SimpleDoubleProperty(clamp(0, 1, OPACITY)));
        return (B) this;
    }

    private double clamp(final double MIN, final double MAX, final double VALUE) {
        if (VALUE < MIN) return MIN;
        if (VALUE > MAX) return MAX;
        return VALUE;
    }


    public final YotaClock build() {
        final YotaClock CONTROL = new YotaClock();
        properties.forEach((key, property) -> {
            if ("prefSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                CONTROL.setPrefSize(dim.getWidth(), dim.getHeight());
            } else if("minSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                CONTROL.setPrefSize(dim.getWidth(), dim.getHeight());
            } else if("maxSize".equals(key)) {
                Dimension2D dim = ((ObjectProperty<Dimension2D>) properties.get(key)).get();
                CONTROL.setPrefSize(dim.getWidth(), dim.getHeight());
            } else if("prefWidth".equals(key)) {
                CONTROL.setPrefWidth(((DoubleProperty) properties.get(key)).get());
            } else if("prefHeight".equals(key)) {
                CONTROL.setPrefHeight(((DoubleProperty) properties.get(key)).get());
            } else if("minWidth".equals(key)) {
                CONTROL.setMinWidth(((DoubleProperty) properties.get(key)).get());
            } else if("minHeight".equals(key)) {
                CONTROL.setMinHeight(((DoubleProperty) properties.get(key)).get());
            } else if("maxWidth".equals(key)) {
                CONTROL.setMaxWidth(((DoubleProperty) properties.get(key)).get());
            } else if("maxHeight".equals(key)) {
                CONTROL.setMaxHeight(((DoubleProperty) properties.get(key)).get());
            } else if("scaleX".equals(key)) {
                CONTROL.setScaleX(((DoubleProperty) properties.get(key)).get());
            } else if("scaleY".equals(key)) {
                CONTROL.setScaleY(((DoubleProperty) properties.get(key)).get());
            } else if ("layoutX".equals(key)) {
                CONTROL.setLayoutX(((DoubleProperty) properties.get(key)).get());
            } else if ("layoutY".equals(key)) {
                CONTROL.setLayoutY(((DoubleProperty) properties.get(key)).get());
            } else if ("translateX".equals(key)) {
                CONTROL.setTranslateX(((DoubleProperty) properties.get(key)).get());
            } else if ("translateY".equals(key)) {
                CONTROL.setTranslateY(((DoubleProperty) properties.get(key)).get());
            } else if ("opacity".equals(key)) {
                CONTROL.setOpacity(((DoubleProperty) properties.get(key)).get());
            } else if ("backgroundColor".equals(key)) {
                CONTROL.setBackgroundColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("foregroundColor".equals(key)) {
                CONTROL.setForegroundColor(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("running".equals(key)) {
                CONTROL.setRunning(((BooleanProperty) properties.get(key)).get());
            } else if ("inverted".equals(key)) {
                CONTROL.setInverted(((BooleanProperty) properties.get(key)).get());
            } else if ("dateTime".equals(key)) {
                CONTROL.updateTime(((ObjectProperty<LocalDateTime>) properties.get(key)).get());
            }
        });

        return CONTROL;
    }
}
