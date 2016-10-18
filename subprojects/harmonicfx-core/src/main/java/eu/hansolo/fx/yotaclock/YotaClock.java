package eu.hansolo.fx.yotaclock;

import eu.hansolo.fx.fonts.Fonts;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;


/**
 * Created by hansolo on 05.12.14.
 */
public class YotaClock extends Region {
    private static final double               PREFERRED_WIDTH          = 200;
    private static final double               PREFERRED_HEIGHT         = 200;
    private static final double               MINIMUM_WIDTH            = 50;
    private static final double               MINIMUM_HEIGHT           = 50;
    private static final double               MAXIMUM_WIDTH            = 1024;
    private static final double               MAXIMUM_HEIGHT           = 1024;
    private static final Color                DEFAULT_BACKGROUND_COLOR = Color.web("#27282E");
    private static final Color                DEFAULT_FOREGROUND_COLOR = Color.web("#ffffff");
    private static final DateTimeFormatter    DATE_FORMATER            = DateTimeFormatter.ofPattern("EE d");
    private static final DateTimeFormatter    TIME_FORMATTER           = DateTimeFormatter.ofPattern("HH:mm");
    private          ObjectProperty<Color>    backgroundColor;
    private          ObjectProperty<Color>    foregroundColor;
    private          Color                    transparentForegroundColor;
    private          BooleanProperty          inverted;
    private          BooleanProperty          running;
    private          double                   size;
    private          double                   width;
    private          double                   height;
    private          Canvas                   ticks;
    private          GraphicsContext          ctx;
    private          Rectangle                hour;
    private          Rectangle                minute;
    private          Circle                   knob;
    private          Text                     dateText;
    private          Text                     timeText;
    private          Pane                     pane;
    private          Rotate                   hourRotate;
    private          Rotate                   minuteRotate;
    private volatile ScheduledFuture<?>       periodicOneSecondTask;
    private static   ScheduledExecutorService periodicOneSecondExecutorService;


    // ******************** Constructors **************************************
    public YotaClock() {
        getStylesheets().add(YotaClock.class.getResource("yotaclock.css").toExternalForm());
        getStyleClass().add("yota-clock");
        backgroundColor            = new ObjectPropertyBase<Color>(DEFAULT_BACKGROUND_COLOR) {
            @Override public Object getBean() { return YotaClock.this; }
            @Override public String getName() { return "backgroundColor"; }
        };
        foregroundColor            = new ObjectPropertyBase<Color>(DEFAULT_FOREGROUND_COLOR) {
            @Override public void set(final Color COLOR) {
                super.set(COLOR);
                transparentForegroundColor = Color.web(COLOR.toString().replace("0x", "#").substring(0, 7) + "80");
            }
            @Override public Object getBean() { return YotaClock.this; }
            @Override public String getName() { return "foregroundColor"; }
        };
        transparentForegroundColor = Color.web(DEFAULT_FOREGROUND_COLOR.toString().replace("0x", "#").substring(0, 7) + "80");
        inverted                   = new BooleanPropertyBase(false) {
            @Override public Object getBean() { return this; }
            @Override public String getName() { return "inverted"; }
        };
        running                    = new SimpleBooleanProperty(this, "running", false);
        minuteRotate               = new Rotate();
        hourRotate                 = new Rotate();

        init();
        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void init() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 ||
            Double.compare(getWidth(), 0.0) <= 0 || Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        if (Double.compare(getMinWidth(), 0.0) <= 0 || Double.compare(getMinHeight(), 0.0) <= 0) {
            setMinSize(MINIMUM_WIDTH, MINIMUM_HEIGHT);
        }

        if (Double.compare(getMaxWidth(), 0.0) <= 0 || Double.compare(getMaxHeight(), 0.0) <= 0) {
            setMaxSize(MAXIMUM_WIDTH, MAXIMUM_HEIGHT);
        }
    }

    private void initGraphics() {
        ticks = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ctx   = ticks.getGraphicsContext2D();

        hour  = new Rectangle(3, 60);
        hour.setArcHeight(3);
        hour.setArcWidth(3);
        hour.setStroke(Color.web("#282a3280"));
        hour.getTransforms().setAll(hourRotate);

        minute = new Rectangle(3, 96);
        minute.setArcHeight(3);
        minute.setArcWidth(3);
        minute.setStroke(Color.web("#282a3280"));
        minute.getTransforms().setAll(minuteRotate);

        knob = new Circle(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5, 4.5);
        knob.setStroke(Color.web("#282a3280"));

        dateText = new Text("");

        timeText = new Text("00:00");

        pane = new Pane();
        pane.getChildren().addAll(ticks, hour, minute, knob, dateText, timeText);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        backgroundColor.addListener(o -> handleControlPropertyChanged("REPAINT"));
        foregroundColor.addListener(o -> handleControlPropertyChanged("REPAINT"));
        inverted.addListener(o -> handleControlPropertyChanged("INVERTED"));
        running.addListener(o -> handleControlPropertyChanged("RUNNING"));
    }


    // ******************** Methods *******************************************
    private void handleControlPropertyChanged(final String PROPERTY) {
        if ("INVERTED".equals(PROPERTY)) {
            Color tmpColor = getBackgroundColor();
            backgroundColor.set(getForegroundColor());
            foregroundColor.set(tmpColor);
            transparentForegroundColor = Color.web(getForegroundColor().toString().replace("0x", "#").substring(0, 7) + "80");
            resize();
        } else if ("RUNNING".equals(PROPERTY)) {
            if (isRunning()) {
                scheduleOneSecondTask();
            } else {
                stopTask(periodicOneSecondTask);
            }
        } else if ("REPAINT".equals(PROPERTY)) {
            resize();
        }
    }

    public Color getBackgroundColor() { return backgroundColor.get(); }
    public void setBackgroundColor(final Color BACKGROUND_COLOR) { backgroundColor.set(BACKGROUND_COLOR); }
    public ObjectProperty<Color> backgroundColorProperty() { return backgroundColor; }

    public Color getForegroundColor() { return foregroundColor.get(); }
    public void setForegroundColor(final Color FOREGROUND_COLOR) { foregroundColor.set(FOREGROUND_COLOR); }
    public ObjectProperty<Color> foregroundColorProperty() { return foregroundColor; }

    public boolean isInverted() { return inverted.get(); }
    public void setInverted(final boolean INVERTED) { inverted.set(INVERTED); }
    public BooleanProperty invertedProperty() { return inverted; }

    public boolean isRunning() { return running.get(); }
    public void setRunning(final boolean RUNNING) { running.set(RUNNING); }
    public BooleanProperty runningProperty() { return running; }

    public void updateTime() {
        updateTime(LocalDateTime.now());
    }
    public void updateTime(final LocalDateTime TIME) {
        Platform.runLater(() -> {
            minuteRotate.setAngle(TIME.getMinute() * 6 + TIME.getSecond() * 0.1);
            hourRotate.setAngle(0.5 * (60 * TIME.getHour() + TIME.getMinute()));
            timeText.setText(TIME_FORMATTER.format(TIME));
            timeText.relocate((size - timeText.getLayoutBounds().getWidth()) * 0.5, size * 0.6);
            dateText.setText(DATE_FORMATER.format(TIME).toUpperCase());
            dateText.relocate(((size * 0.5) - dateText.getLayoutBounds().getWidth()) * 0.5 + (size * 0.45), (size - dateText.getLayoutBounds().getHeight()) * 0.5);
        });
    }


    // ******************** Scheduled task related ****************************
    private synchronized static void enableOneSecondExecutorService() {
        if (null == periodicOneSecondExecutorService) {
            periodicOneSecondExecutorService = new ScheduledThreadPoolExecutor(1, getThreadFactory("Second", false));
        }
    }
    private synchronized void scheduleOneSecondTask() {
        enableOneSecondExecutorService();
        stopTask(periodicOneSecondTask);
        periodicOneSecondTask = periodicOneSecondExecutorService.scheduleAtFixedRate(() -> updateTime(), 1, 1, TimeUnit.SECONDS);
    }

    private static ThreadFactory getThreadFactory(final String THREAD_NAME, final boolean IS_DAEMON) {
        return runnable -> {
            Thread thread = new Thread(runnable, THREAD_NAME);
            thread.setDaemon(IS_DAEMON);
            return thread;
        };
    }

    private void stopTask(ScheduledFuture<?> task) {
        if (null == task) return;

        task.cancel(true);
        task = null;
    }


    // ******************** Canvas ********************************************
    private void drawTicks() {
        ctx.clearRect(0, 0, size, size);
        double  sinValue;
        double  cosValue;
        double  startAngle = 180;
        double  angleStep  = 360 / 60;
        Point2D center     = new Point2D(size * 0.5, size * 0.5);
        ctx.setLineCap(StrokeLineCap.ROUND);
        for (double angle = 0, counter = 0 ; Double.compare(counter, 59) <= 0 ; angle -= angleStep, counter++) {
            sinValue = Math.sin(Math.toRadians(angle + startAngle));
            cosValue = Math.cos(Math.toRadians(angle + startAngle));

            Point2D innerMainPoint   = new Point2D(center.getX() + size * 0.405 * sinValue, center.getY() + size * 0.405 * cosValue);
            Point2D innerMinorPoint  = new Point2D(center.getX() + size * 0.435 * sinValue, center.getY() + size * 0.435 * cosValue);
            Point2D outerPoint       = new Point2D(center.getX() + size * 0.465 * sinValue, center.getY() + size * 0.465 * cosValue);

            ctx.setStroke(getForegroundColor());
            if (counter % 5 == 0) {
                // Draw major tickmark
                ctx.setLineWidth(size * 0.02);
                ctx.setStroke(Color.web("#282a32"));
                ctx.strokeLine(innerMainPoint.getX(), innerMainPoint.getY(), outerPoint.getX(), outerPoint.getY());
                ctx.setLineWidth(size * 0.01);
                ctx.setStroke(getForegroundColor());
                ctx.strokeLine(innerMainPoint.getX(), innerMainPoint.getY(), outerPoint.getX(), outerPoint.getY());
            } else if (counter % 1 == 0) {
                ctx.setLineWidth(size * 0.0065);
                ctx.setStroke(Color.web("#282a3280"));
                ctx.strokeLine(innerMinorPoint.getX(), innerMinorPoint.getY(), outerPoint.getX(), outerPoint.getY());
                ctx.setLineWidth(size * 0.005);
                ctx.setStroke(transparentForegroundColor);
                ctx.strokeLine(innerMinorPoint.getX(), innerMinorPoint.getY(), outerPoint.getX(), outerPoint.getY());
            }
        }
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width  = getWidth();
        height = getHeight();
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            pane.setBackground(new Background(new BackgroundFill(getBackgroundColor(), new CornerRadii(1024), Insets.EMPTY)));
            pane.setMaxSize(size, size);
            pane.setPrefSize(size, size);
            pane.relocate((width - size) * 0.5, (height - size) * 0.5);

            ticks.setWidth(size);
            ticks.setHeight(size);
            drawTicks();

            hour.setFill(getForegroundColor());
            hour.setWidth(size * 0.015);
            hour.setHeight(size * 0.29);
            hour.setArcWidth(size * 0.015);
            hour.setArcHeight(size * 0.015);
            hour.relocate((size - hour.getWidth()) * 0.5, size * 0.21);

            minute.setFill(getForegroundColor());
            minute.setWidth(size * 0.015);
            minute.setHeight(size * 0.47);
            minute.setArcWidth(size * 0.015);
            minute.setArcHeight(size * 0.015);
            minute.relocate((size - minute.getWidth()) * 0.5, size * 0.03);

            knob.setFill(getForegroundColor());
            knob.setRadius(size * 0.0225);
            knob.setCenterX(size * 0.5);
            knob.setCenterY(size * 0.5);

            dateText.setFill(getForegroundColor());
            dateText.setFont(Fonts.latoLight(size * 0.05));
            dateText.relocate(((size * 0.5) - dateText.getLayoutBounds().getWidth()) * 0.5 + (size * 0.45), (size - dateText.getLayoutBounds().getHeight()) * 0.5);

            timeText.setFill(transparentForegroundColor);
            timeText.setFont(Fonts.latoLight(size * 0.12));
            timeText.relocate((size - timeText.getLayoutBounds().getWidth()) * 0.5, size * 0.6);

            minuteRotate.setPivotX(minute.getWidth() * 0.5);
            minuteRotate.setPivotY(minute.getHeight());
            hourRotate.setPivotX(hour.getWidth() * 0.5);
            hourRotate.setPivotY(hour.getHeight());
        }
    }
}
