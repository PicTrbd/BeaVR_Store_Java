package store_launcher.controllers;

import javafx.beans.binding.Bindings;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import store_launcher.network.ServiceHandler;
import store_launcher.response.ForgottenPasswordResponse;
import utils.Config;
import utils.ControllersTools;
import utils.ErrorHandler;
import utils.JSONParser;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Nicolas on 12/17/2015.
 */
public class ForgottenPasswordController implements Initializable {

    public static final String emailField = "email";

    @FXML private Parent root;
    @FXML private TextField email_field;
    @FXML private Button reset_button;
    @FXML
    private Button back_button;
    @FXML
    private ProgressIndicator reset_progressindicator;
    @FXML
    private Text error_text;

    private ResourceBundle mResources;
    private Locale mLocale;

    @FXML
    protected void handleResetButton(ActionEvent event) throws InterruptedException {
        // Declare a new ServiceHandler object in order to create a new Thread.
        // We must pass the ServiceHandler.Request() as a parameter to ServiceHandler Constructor.
        // This way, we can override ServiceHandler.Request's method performRequest with our own code.
        // This code will be executed in our thread.
        final ServiceHandler serviceHandler = new ServiceHandler(new ServiceHandler.Request() {
            @Override
            public Object performRequest() {
                // This method will be called in our thread.
                return retrieveValues();
            }
        });

        // The thread has finished retrieving data. The callback below is called.
        serviceHandler.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                ForgottenPasswordResponse forgottenPasswordResponse = (ForgottenPasswordResponse) serviceHandler.getValue();

                if (!forgottenPasswordResponse.getError()) {
                    error_text.setText(mResources.getString("password_sent"));
                } else {
                    ResourceBundle errorBundle = ResourceBundle.getBundle("bundles.Error", mLocale);
                    error_text.setText(ErrorHandler.getDescription(forgottenPasswordResponse.getCode(), errorBundle));
                }
            }
        });

        // We bind :
        // The ProgressIndicator's visibility to the thread's lifecycle
        // The Login button's visibility to the ProgressIndicator's visibility
        // The Error text's visibility to its non-emptiness
        reset_progressindicator.visibleProperty().bind(serviceHandler.runningProperty());
        reset_button.visibleProperty().bind(reset_progressindicator.visibleProperty().not());
        back_button.disableProperty().bind(reset_button.visibleProperty().not());
        error_text.visibleProperty().bind(error_text.textProperty().isNotEmpty());
        serviceHandler.restart();
    }

    private Object retrieveValues() {
        JSONParser parser = new JSONParser(Config.RESET_PASSWORD_URL);

        // Parameters of the post request
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair(emailField, email_field.getText()));

        // Execution of the request
        return parser.postRequest(urlParameters, new ForgottenPasswordResponse());
    }


    @FXML
    protected void handleBackButton(ActionEvent event) throws InterruptedException, IOException {
        ControllersTools.showWindow("BeaVR - " + mResources.getString("login"),
                getClass().getResource("/layouts/login.fxml"),
                getClass().getResource("/styles/launcher.css"),
                root, new Locale("FR"));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mResources = resources;
        mLocale = mResources.getLocale();
        reset_button.disableProperty().bind(
                Bindings.isEmpty(email_field.textProperty()));
    }
}
