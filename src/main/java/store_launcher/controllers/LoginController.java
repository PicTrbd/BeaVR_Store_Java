package store_launcher.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Window;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import store_launcher.network.ServiceHandler;
import store_launcher.response.CategoryTypesAndDevicesResponse;
import store_launcher.response.LoginResponse;
import utils.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by Nicolas on 12/2/2015.
 */
public class LoginController implements Initializable {

    public static final String emailField = "email";
    public static final String passwordField = "password";

    public static final int EMAIL_LIMIT = 64;

    // Value set according to the fx:id in the login.fxml
    @FXML
    private Parent root;
    @FXML
    private TextField email_field;
    @FXML
    private PasswordField password_field;
    @FXML
    private Button login_button;
    @FXML
    private Hyperlink register_hyperlink;
    @FXML
    private ProgressIndicator login_progressindicator;
    @FXML
    private Text error_text;
    @FXML
    private Text password_forgotten;
    @FXML
    private CheckBox remember_me_checkbox;

    private ResourceBundle mResources;
    private Locale mLocale;
    private boolean isRememberMeCheckboxChecked = false;

    @FXML
    protected void handleLogInButton(final ActionEvent event) throws InterruptedException {
        fetchLoginInformations(((Node) event.getTarget()).getScene());
    }

    private void fetchLoginInformations(Scene scene) {
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
            public void handle(WorkerStateEvent workerStateEvent) {
                LoginResponse loginResponse = (LoginResponse) serviceHandler.getValue();

                if (loginResponse != null) {
                    if (loginResponse.getError()) {
                        ResourceBundle errorBundle = ResourceBundle.getBundle("bundles.Error", mLocale);
                        error_text.setText(ErrorHandler.getDescription(loginResponse.getCode(), errorBundle));
                    } else {
                        if (isRememberMeCheckboxChecked && !SessionHandler.isAlreadyConnected()) {
                            SessionHandler.setCredentials(email_field.getText(), password_field.getText());
                        }
                        getCategoryTypesAndDevices(scene);
                    }
                } else {
                    ResourceBundle errorBundle = ResourceBundle.getBundle("bundles.Error", mLocale);
                    error_text.setText(ErrorHandler.getDescription(102, errorBundle));
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

    private Object retrieveCategoryTypesAndDevices() {
        // Creation of the JSonParser
        JSONParser parser = new JSONParser(Config.GET_CATEGORY_TYPES_AND_DEVICES_URL);
        // Parameters of the post request
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        // Execution of the request
        return parser.getRequest(urlParameters, new CategoryTypesAndDevicesResponse());
    }

    private void getCategoryTypesAndDevices(final Scene scene) {
        // Declare a new ServiceHandler object in order to create a new Thread.
        // We must pass the ServiceHandler.Request() as a parameter to ServiceHandler Constructor.
        // This way, we can override ServiceHandler.Request's method performRequest with our own code.
        // This code will be executed in our thread.
        final ServiceHandler serviceHandler = new ServiceHandler(new ServiceHandler.Request() {
            @Override
            public Object performRequest() {
                // This method will be called in our thread.
                return retrieveCategoryTypesAndDevices();
            }
        });
        // The thread has finished retrieving data. The callback below is called.
        serviceHandler.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                CategoryTypesAndDevicesResponse categoryTypesAndDevicesResponse = (CategoryTypesAndDevicesResponse) serviceHandler.getValue();
                if (!categoryTypesAndDevicesResponse.getError()) {
                    try {
                        List<String> categoryTypes = Arrays.asList(categoryTypesAndDevicesResponse.CategoryTypesAndDevices.CategoryTypes.split(", "));
                        List<String> devices = Arrays.asList(categoryTypesAndDevicesResponse.CategoryTypesAndDevices.Devices.split(", "));
                        StoredValues.setCategoryTypes(categoryTypes);
                        StoredValues.setDevices(devices);
                        ControllersTools.showResizableWindowWithClosing("BeaVR - " + mResources.getString("app_list_store"), 1024, 768,
                                getClass().getResource("/layouts/applications_list.fxml"),
                                getClass().getResource("/styles/app_list.css"),
                                root, ControllersTools.isWindowMaximized(scene), new Locale("FR"));
                    } catch (IOException e) {
                        ResourceBundle errorBundle = ResourceBundle.getBundle("bundles.Error", mLocale);
                        error_text.setText(ErrorHandler.getDescription(10, errorBundle));
                    }
                } else {
                    ResourceBundle errorBundle = ResourceBundle.getBundle("bundles.Error", mLocale);
                    error_text.setText(ErrorHandler.getDescription(categoryTypesAndDevicesResponse.getCode(), errorBundle));
                }
            }
        });
        serviceHandler.restart();
        login_progressindicator.visibleProperty().bind(serviceHandler.runningProperty());
        login_button.visibleProperty().bind(login_progressindicator.visibleProperty().not());
        register_hyperlink.disableProperty().bind(login_button.visibleProperty().not());
    }

    @FXML
    protected void handleForgotPasswordHyperlink(ActionEvent event) throws InterruptedException, IOException {
        ControllersTools.showWindow("BeaVR - " + mResources.getString("reset_password"), 350, 500,
                getClass().getResource("/layouts/forgotten_password.fxml"),
                getClass().getResource("/styles/launcher.css"),
                root, new Locale("FR"));
    }

    @FXML
    protected void handleRegisterHyperlink(ActionEvent event) throws InterruptedException, IOException {
        ControllersTools.showWindow("BeaVR - " + mResources.getString("registration"), 350, 500,
                getClass().getResource("/layouts/registration.fxml"),
                getClass().getResource("/styles/launcher.css"),
                root, new Locale("FR"));
    }

    @Override
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
        // Here, we bind the value of the two inputs so that, when one of their value is empty, the button is disabled
        login_button.disableProperty().bind(
                Bindings.isEmpty(email_field.textProperty())
                        .or(Bindings.isEmpty(password_field.textProperty())));

        // Called when the scene is set and available
        root.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldScene, Scene newScene) {
                if (oldScene == null && newScene != null) {
                    newScene.windowProperty().addListener(new ChangeListener<Window>() {
                        @Override
                        public void changed(ObservableValue<? extends Window> observable, Window oldWindow, Window newWindow) {
                            if (oldWindow == null && newWindow != null) {
                                if (SessionHandler.isAlreadyConnected()) {
                                    email_field.setText(SessionHandler.getUsername());
                                    password_field.setText(SessionHandler.getPassword());
                                    remember_me_checkbox.setSelected(true);
                                    fetchLoginInformations(newScene);
                                }
                            }
                        }
                    });
                }
            }
        });
        remember_me_checkbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                isRememberMeCheckboxChecked = newValue;
            }
        });
    }
}
