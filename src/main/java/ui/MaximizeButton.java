package ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Created by Nicolas on 2/11/2016.
 */
public class MaximizeButton extends HBox {

    private final Button button = new Button("+");
    private boolean isMaximized = false;

    public MaximizeButton() {
        button.getStyleClass().clear();
        button.getStyleClass().add("actionbutton");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
                if (!isMaximized) {
                    stage.resizableProperty().setValue(false);
                    stage.setX(primaryScreenBounds.getMinX());
                    stage.setY(primaryScreenBounds.getMinY());
                    stage.setWidth(primaryScreenBounds.getWidth());
                    stage.setHeight(primaryScreenBounds.getHeight());
                    button.setText("o");
                    isMaximized = true;
                } else {
                    stage.setWidth(1024);
                    stage.setHeight(768);
                    stage.centerOnScreen();
                    button.setText("+");
                    isMaximized = false;
                }
            }
        });
        this.getChildren().add(button);
    }

    public void setManualResized() {
        button.setText("+");
        isMaximized = false;
    }

    public void setMaximizedStatus() {
        button.setText("o");
        isMaximized = true;
    }

    public boolean isMaximized() {
        return isMaximized;
    }
}
