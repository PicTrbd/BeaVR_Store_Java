package store_app_list.controllers;

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
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Window;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import store_app_list.response.SendFeedbackResponse;
import store_launcher.network.ServiceHandler;
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
 * Created by Thomas on 12/2/2015.
 */
public class FeedbackController implements Initializable {

    public static final int SUBJECT_LIMIT = 80;
    public static final int MESSAGE_LIMIT = 2000;

    public static final String idUserField = "idUser";
    public static final String subjectField = "object";
    public static final String messageField = "description";
    public static final String recontactField = "recontact";

    @FXML
    private Parent root;
    @FXML
    private TextField subject_field;
    @FXML
    private TextArea message_field;
    @FXML
    private Button sendFeedback_button;
    @FXML
    private CheckBox recontact_checkbox;
    @FXML
    private ProgressIndicator login_progressindicator;
    @FXML
    private Text error_text;
    @FXML
    private HBox validate_box;

    private ResourceBundle mResources;
    private Locale mLocale;

    @FXML
    protected void handleSendFeedbackButton(ActionEvent event) throws InterruptedException {

        // Declare a new ServiceHandler object in order to create a new Thread.
        // We must pass the ServiceHandler.Request() as a parameter to ServiceHandler Constructor.
        // This way, we can override ServiceHandler.Request's method performRequest with our own code.
        // This code will be executed in our thread.
        final ServiceHandler serviceHandler = new ServiceHandler(new ServiceHandler.Request() {
            @Override
            public Object performRequest() {
                // This method will be called in our thread.
                return retrieveFeedbackValues();
            }
        });

        // The thread has finished retrieving data. The callback below is called.
        serviceHandler.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                SendFeedbackResponse sendFeedbackResponse = (SendFeedbackResponse) serviceHandler.getValue();

                if (sendFeedbackResponse.getError()) {
                    ResourceBundle errorBundle = ResourceBundle.getBundle("bundles.Error", mLocale);
                    error_text.setStyle("-fx-fill:#FF0000");
                    error_text.setText(ErrorHandler.getDescription(sendFeedbackResponse.getCode(), errorBundle));
                }
                else {
                    error_text.setStyle("-fx-fill:#32A9AD");
                    error_text.setText(mResources.getString("feedback_success"));
                    validate_box.getChildren().remove(sendFeedback_button);
                }
            }
        });

        login_progressindicator.visibleProperty().bind(serviceHandler.runningProperty());
        sendFeedback_button.visibleProperty().bind(login_progressindicator.visibleProperty().not());
        error_text.visibleProperty().bind(error_text.textProperty().isNotEmpty());

        serviceHandler.restart();
    }

    private Object retrieveFeedbackValues() {
        // Creation of the JSonParser
        JSONParser parser = new JSONParser(Config.SEND_FEEDBACK_URL);

        // Parameters of the post request
        List<NameValuePair> urlParameters = new ArrayList<>();
        //TODO Une fois qu'on aura les sessions, faire en sorte que ça soit l'ID de l'utilisateur qui soit envoyé !!!
        urlParameters.add(new BasicNameValuePair(idUserField, Integer.toString(1)));
        urlParameters.add(new BasicNameValuePair(subjectField, subject_field.getText()));
        urlParameters.add(new BasicNameValuePair(messageField, message_field.getText()));
        if (recontact_checkbox.isSelected())
            urlParameters.add(new BasicNameValuePair(recontactField, Integer.toString(1)));
        else
            urlParameters.add(new BasicNameValuePair(recontactField, Integer.toString(0)));

        // Execution of the request
        return parser.postRequest(urlParameters, new SendFeedbackResponse());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mResources = resources;
        mLocale = mResources.getLocale();

        // Limit the sizes of the textfield and textarea
        subject_field.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    // Check if the new character is greater than LIMIT
                    if (subject_field.getText().length() >= SUBJECT_LIMIT)
                        subject_field.setText(subject_field.getText().substring(0, SUBJECT_LIMIT));
                }
            }
        });
        message_field.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    // Check if the new character is greater than LIMIT
                    if (message_field.getText().length() >= MESSAGE_LIMIT)
                        message_field.setText(message_field.getText().substring(0, MESSAGE_LIMIT));
                }
            }
        });
        // Here, we bind the value of the two inputs so that, when one of their value is empty, the button is disabled
        sendFeedback_button.disableProperty().bind(
                Bindings.isEmpty(subject_field.textProperty())
                        .or(Bindings.isEmpty(message_field.textProperty())));
    }

    @FXML
    protected void handleStoreHyperlink(ActionEvent event) throws InterruptedException, IOException {
        Scene scene = ((Node) event.getTarget()).getScene();
        Window window = ((Node) event.getTarget()).getScene().getWindow();
        ControllersTools.showResizableWindow("BeaVR - " + mResources.getString("app_list_store"), window.getWidth(), window.getHeight(),
                getClass().getResource("/layouts/applications_list.fxml"),
                getClass().getResource("/styles/app_list.css"),
                root, ControllersTools.isWindowMaximized(scene), new Locale("FR"));
    }
}
