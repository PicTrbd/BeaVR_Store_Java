package store_launcher.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Created by Nicolas on 1/26/2016.
 */
public class MinimizeButton extends HBox {

    public MinimizeButton() {
        Button button = new Button("-");
        button.getStyleClass().clear();
        button.getStyleClass().add("actionbutton");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                stage.setIconified(true);
            }
        });
        this.getChildren().add(button);
    }
}
