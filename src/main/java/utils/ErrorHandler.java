package utils;

import java.util.ResourceBundle;

/**
 * Created by Nicolas on 2/3/2016.
 */
public class ErrorHandler {

    public static String getDescription(Integer errorCode, ResourceBundle resourceBundle) {
        return resourceBundle.getString(errorCode.toString());
    }
}
