package store_launcher.ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

/**
 * Created by Nicolas on 1/26/2016.
 */
public class CloseButton extends HBox {

    public CloseButton() {
        Button button = new Button("x");
        button.getStyleClass().clear();
        button.getStyleClass().add("actionbutton");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Platform.exit();
            }
        });
        this.getChildren().add(button);
    }
}
