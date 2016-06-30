package store_launcher.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import store_launcher.network.ServiceHandler;
import store_launcher.response.RegistrationResponse;
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
 * Created by Thomas on 03/12/2015.
 */
public class RegistrationController implements Initializable {

    public static final String emailField = "email";
    public static final String passwordField = "password";

    public static final int EMAIL_LIMIT = 64;

    // Value set according to the fx:id in the registration_old.fxml
    @FXML
    private Parent root;
    @FXML
    private TextField email_field;
    @FXML
    private PasswordField password_field;
    @FXML
    private Button register_button;
    @FXML
    private Hyperlink login_hyperlink;
    @FXML
    private ProgressIndicator register_progressindicator;
    @FXML
    private Text error_text;

    private ResourceBundle mResources;
    private Locale mLocale;

    @FXML
    protected void handleRegisterButton(ActionEvent event) throws InterruptedException {
        // Declare a new ServiceHandler object in order to create a new Thread.
        // We must pass the ServiceHandler.Request() as a parameter to ServiceHandler Constructor.
        // This way, we can override ServiceHandler.Request's method performRequest with our own code.
        // This code will be executed in our thread.
        final ServiceHandler serviceHandler = new ServiceHandler(new ServiceHandler.Request() {
            @Override
            public Object performRequest() {
                // This method will be called in our thread.
                return retrieveRegistrationValues();
            }
        });

        // The thread has finished retrieving data. The callback below is called.
        serviceHandler.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                RegistrationResponse registrationResponse = (RegistrationResponse) serviceHandler.getValue();

                if (!registrationResponse.getError()) {
                    error_text.setText(mResources.getString("registered"));
                } else {
                    ResourceBundle errorBundle = ResourceBundle.getBundle("bundles.Error", mLocale);
                    error_text.setText(ErrorHandler.getDescription(registrationResponse.getCode(), errorBundle));
                }
            }
        });

        // We bind :
        // The ProgressIndicator's visibility to the thread's lifecycle
        // The Login button's visibility to the ProgressIndicator's visibility
        // The Error text's visibility to its non-emptiness
        register_progressindicator.visibleProperty().bind(serviceHandler.runningProperty());
        register_button.visibleProperty().bind(register_progressindicator.visibleProperty().not());
        login_hyperlink.disableProperty().bind(register_button.visibleProperty().not());
        error_text.visibleProperty().bind(error_text.textProperty().isNotEmpty());
        serviceHandler.restart();
    }

    private Object retrieveRegistrationValues() {
        // Creation of the JSONParser
        JSONParser parser = new JSONParser(Config.REGISTER_URL);

        // Parameters of the post request
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair(emailField, email_field.getText()));
        urlParameters.add(new BasicNameValuePair(passwordField, password_field.getText()));

        // Execution of the request
        return parser.postRequest(urlParameters, new RegistrationResponse());
    }

    @FXML
    protected void handleLoginHyperlink(ActionEvent event) throws InterruptedException, IOException {
        ControllersTools.showWindow("BeaVR - " + mResources.getString("login"), 350, 500,
                getClass().getResource("/layouts/login.fxml"),
                getClass().getResource("/styles/launcher.css"),
                root, new Locale("FR"));
    }

    public void initialize(URL location, ResourceBundle resources) {
        mResources = resources;
        mLocale = mResources.getLocale();
        // Limit the sizes of the textfield and textarea
        email_field.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    // Check if the new character is greater than LIMIT
                    if (email_field.getText().length() >= EMAIL_LIMIT)
                        email_field.setText(email_field.getText().substring(0, EMAIL_LIMIT));
                }
            }
        });
        register_button.disableProperty().bind(
                Bindings.isEmpty(email_field.textProperty())
                        .or(Bindings.isEmpty(password_field.textProperty())));
    }
}
