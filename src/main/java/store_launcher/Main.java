package store_launcher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.ControllersTools;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {

        // Login
        ResourceBundle resourceBundle = ResourceBundle.getBundle("bundles.Content", new Locale("fr", "FR"));
        BorderPane root = FXMLLoader.load(getClass().getResource("/layouts/login.fxml"), resourceBundle);
        Scene scene = new Scene(root, 350, 500);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("BeaVR - " + resourceBundle.getString("login"));
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/beavr.png")));
        scene.getStylesheets().add(getClass().getResource("/styles/launcher.css").toExternalForm());
        ControllersTools.addDraggableNode(root.getTop());
        primaryStage.show();
    }
}
