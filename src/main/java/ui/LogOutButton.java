package ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import utils.ControllersTools;
import utils.SessionHandler;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Nicolas on 1/26/2016.
 */
public class LogOutButton extends HBox {

    public LogOutButton() {
        Button button = new Button("x");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Locale locale = new Locale("fr", "FR");
                ResourceBundle resourceBundle = ResourceBundle.getBundle("bundles.Content", locale);
                SessionHandler.terminateSession();
                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                try {
                    ControllersTools.showResizableWindowWithClosing("BeaVR - " + resourceBundle.getString("login"), 350, 500,
                            getClass().getResource("/layouts/login.fxml"), getClass().getResource("/styles/launcher.css"), stage, locale);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        button.getStyleClass().clear();
        button.getStyleClass().add("logoutbutton");
        this.getChildren().add(button);
    }
}
