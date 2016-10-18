package eu.hansolo.fx.yotaclock;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * User: hansolo
 * Date: 05.12.14
 * Time: 16:34
 */
public class Main extends Application {
    private YotaClock clock;

    @Override public void init() {
        clock = YotaClockBuilder.create()
                                .prefSize(176, 176)
                                .running(true)
                                //.inverted(true)
                                .build();
    }

    public void saveAsPng(Node node, String fileName) {
        WritableImage snapshot = node.snapshot(new SnapshotParameters(), null);

        File file = new File(fileName + ".png");

        try {

            BufferedImage img = SwingFXUtils.fromFXImage(snapshot, null);

            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
        } catch (IOException exception) {
            System.out.println(exception);
        }
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane();
        pane.setPadding(new Insets(10, 10, 10, 10));
        pane.getChildren().addAll(clock);

        Scene scene = new Scene(pane);

        stage.setScene(scene);
        stage.show();

        //saveAsPng(clock, "yotaclock");
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
