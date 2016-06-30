package utils;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Nicolas on 12/17/2015.
 */
public class ControllersTools {

    public static void showWindow(String title, URL fxmlPath, URL cssPath, Parent root, Locale locale) throws IOException {
        BorderPane parent = FXMLLoader.load(fxmlPath, ResourceBundle.getBundle("bundles.Content", locale));
        Scene scene = new Scene(parent, 350, 500);
        Stage primaryStage = (Stage) root.getScene().getWindow();
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(cssPath.toExternalForm());
        addDraggableNode(parent.getTop());
        primaryStage.show();
    }

    public static void showWindow(String title, double width, double height, URL fxmlPath, URL cssPath, Parent root, Locale locale) throws IOException {
        BorderPane parent = FXMLLoader.load(fxmlPath, ResourceBundle.getBundle("bundles.Content", locale));
        Scene scene = new Scene(parent, width, height);
        Stage primaryStage = (Stage) root.getScene().getWindow();
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(cssPath.toExternalForm());
        addDraggableNode(parent.getTop());
        primaryStage.show();
    }


    public static void addDraggableNode(final Node node) {
        final double[] initialX = new double[1];
        final double[] initialY = new double[1];
        node.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                if (me.getButton() != MouseButton.MIDDLE) {
                    initialX[0] = me.getSceneX();
                    initialY[0] = me.getSceneY();
                }
            }
        });

        node.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                if (me.getButton() != MouseButton.MIDDLE) {
                    node.getScene().getWindow().setX(me.getScreenX() - initialX[0]);
                    node.getScene().getWindow().setY(me.getScreenY() - initialY[0]);
                }
            }
        });
    }
}
