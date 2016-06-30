package store_launcher.controllers;

import javafx.beans.binding.Bindings;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import store_launcher.network.ServiceHandler;
import store_launcher.response.LoginResponse;
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
 * Created by Nicolas on 12/2/2015.
 */
public class LoginController implements Initializable {

    public static final String emailField = "email";
    public static final String passwordField = "password";

    // Value set according to the fx:id in the login.fxml
    @FXML private Parent root;
    @FXML private TextField email_field;
    @FXML private PasswordField password_field;
    @FXML private Button login_button;
    @FXML private Hyperlink register_hyperlink;
    @FXML private ProgressIndicator login_progressindicator;
    @FXML private Text error_text;
    @FXML private Text password_forgotten;

    private ResourceBundle mResources;
    private Locale mLocale;

    @FXML
    protected void handleLogInButton(ActionEvent event) throws InterruptedException {
        // Declare a new ServiceHandler object in order to create a new Thread.
        // We must pass the ServiceHandler.Request() as a parameter to ServiceHandler Constructor.
        // This way, we can override ServiceHandler.Request's method performRequest with our own code.
        // This code will be executed in our thread.
        final ServiceHandler serviceHandler = new ServiceHandler(new ServiceHandler.Request() {
            @Override
            public Object performRequest() {
                // This method will be called in our thread.
                return retrieveLoginValues();
            }
        });

        // The thread has finished retrieving data. The callback below is called.
        serviceHandler.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                LoginResponse loginResponse = (LoginResponse) serviceHandler.getValue();

                if (loginResponse.getError()) {
                    ResourceBundle errorBundle = ResourceBundle.getBundle("bundles.Error", mLocale);
                    error_text.setText(ErrorHandler.getDescription(loginResponse.getCode(), errorBundle));
                }
            }
        });

        // We bind :
        // The ProgressIndicator's visibility to the thread's lifecycle
        // The Login button's visibility to the ProgressIndicator's visibility
        // The Error text's visibility to its non-emptiness
        login_progressindicator.visibleProperty().bind(serviceHandler.runningProperty());
        login_button.visibleProperty().bind(login_progressindicator.visibleProperty().not());
        register_hyperlink.disableProperty().bind(login_button.visibleProperty().not());
        error_text.visibleProperty().bind(error_text.textProperty().isNotEmpty());
        serviceHandler.restart();
    }

    private Object retrieveLoginValues() {
        // Creation of the JSonParser
        JSONParser parser = new JSONParser(Config.LOGIN_URL);

        // Parameters of the post request
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair(emailField, email_field.getText()));
        urlParameters.add(new BasicNameValuePair(passwordField, password_field.getText()));
        // Execution of the request
        return parser.postRequest(urlParameters, new LoginResponse());
    }

    @FXML
    protected void handleForgotPasswordHyperlink(ActionEvent event) throws InterruptedException, IOException {
        ControllersTools.showWindow("BeaVR - " + mResources.getString("reset_password"), 350, 250,
                getClass().getResource("/layouts/forgotten_password.fxml"),
                getClass().getResource("/styles/launcher.css"),
                root, new Locale("FR"));
    }

    @FXML
    protected void handleRegisterHyperlink(ActionEvent event) throws InterruptedException, IOException {
        ControllersTools.showWindow("BeaVR - " + mResources.getString("registration"),
                getClass().getResource("/layouts/registration.fxml"),
                getClass().getResource("/styles/launcher.css"),
                root, new Locale("FR"));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mResources = resources;
        mLocale = mResources.getLocale();
        // Here, we bind the value of the two inputs so that, when one of their value is empty, the button is disabled
        login_button.disableProperty().bind(
                Bindings.isEmpty(email_field.textProperty())
                        .or(Bindings.isEmpty(password_field.textProperty())));
    }
}
