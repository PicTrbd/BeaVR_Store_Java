package store_app_list.controllers;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Window;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.controlsfx.control.Rating;
import store_app_list.response.CommentsListResponse;
import store_launcher.network.ServiceHandler;
import utils.Config;
import utils.ControllersTools;
import utils.ErrorHandler;
import utils.JSONParser;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Thomas on 22/2/2015.
 */
public class CommentsListController implements Initializable {

    public int applicationId;
    public int authorId;

    public static final String idApplicationField = "idApplication";
    public static final String idAuthorField = "idAuthor";


    @FXML
    private Parent root;
    @FXML
    private VBox comments_list;

    private ResourceBundle mResources;
    private Locale mLocale;

    private List<CommentsListResponse.Comment> mComments;

    /*
    *
    * Code handling the list of comments
    *
    */

    // Handle the visual part of the comments list
    private void setCommentsToVBOX(List<CommentsListResponse.Comment> commentsList) {

        comments_list.getChildren().clear();

        for (CommentsListResponse.Comment comment : commentsList) {

            // One comment
            BorderPane commentBorderPane = new BorderPane();

            // Left part of the comment with the email
            VBox commentInformations = new VBox();
            commentInformations.setMinWidth(250);
            commentInformations.setSpacing(5);
            commentInformations.setPadding(new Insets(15, 10, 20, 10));
            commentInformations.getStyleClass().add("comment_informations");

            Text fakeName = new Text("FakePseudo@gmail.com");
            fakeName.setFont(Font.font("Helvetica", FontWeight.BOLD, FontPosture.REGULAR, 16));
            fakeName.setFill(Color.web("#32A9AD"));
            Text commentDate = new Text(new SimpleDateFormat("dd/MM/yyyy").format(comment.date));
            commentDate.setText("Le " + commentDate.getText());
            commentDate.setFont(Font.font("Helvetica", FontPosture.REGULAR, 15));
            Text commentHour = new Text(new SimpleDateFormat("hh:mm").format(comment.date));
            commentHour.setText("À " + commentHour.getText());
            commentHour.setFont(Font.font("Helvetica", FontPosture.REGULAR, 15));

            commentInformations.getChildren().add(fakeName);
            commentInformations.getChildren().add(commentDate);
            commentInformations.getChildren().add(commentHour);

            commentBorderPane.setLeft(commentInformations);

            // Right part of the comment with the rating and the comment
            VBox commentContent = new VBox();
            commentContent.setSpacing(20);
            commentContent.setPadding(new Insets(15, 10, 20, 15));
            commentContent.getStyleClass().add("comment_content");

            Rating rating = new Rating(5, comment.rating);
            rating.setDisable(true);
            Text content = new Text(comment.comment);
            content.setFont(Font.font("Helvetica", FontPosture.REGULAR, 14));
            content.setWrappingWidth(450);

            commentContent.getChildren().add(rating);
            commentContent.getChildren().add(content);

            commentBorderPane.setCenter(commentContent);

            comments_list.getChildren().add(commentBorderPane);
        }

    }

    // Get the list of comments
    protected void getCommentsList() {

        // Declare a new ServiceHandler object in order to create a new Thread.
        // We must pass the ServiceHandler.Request() as a parameter to ServiceHandler Constructor.
        // This way, we can override ServiceHandler.Request's method performRequest with our own code.
        // This code will be executed in our thread.
        final ServiceHandler serviceHandler = new ServiceHandler(new ServiceHandler.Request() {
            @Override
            public Object performRequest() {
                // This method will be called in our thread.
                return retrieveCommentsListValues();
            }
        });

        // The thread has finished retrieving data. The callback below is called.
        serviceHandler.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                CommentsListResponse commentsListResponse = (CommentsListResponse) serviceHandler.getValue();

                if (!commentsListResponse.getError())
                {
                    mComments = commentsListResponse.Comments;
                    setCommentsToVBOX(mComments);
                }
                else {
                    ResourceBundle errorBundle = ResourceBundle.getBundle("bundles.Error", mLocale);
                    System.out.println(ErrorHandler.getDescription(commentsListResponse.getCode(), errorBundle));
                }
            }
        });

        serviceHandler.restart();
    }

    // Execution of the request
    private Object retrieveCommentsListValues() {
        // Creation of the JSonParser
        JSONParser parser = new JSONParser(Config.GET_COMMENTS_URL);
        // Execution of the request
        return parser.getRequestFromID(applicationId, new CommentsListResponse());
    }

    /*
    *
    * Code handling the top of the comments page (form if the user never commented this app, comment otherwise)
    *
    */

    // Check if the user has already commented the application
    protected void checkHasCommentedCurrentApp() {

        // Declare a new ServiceHandler object in order to create a new Thread.
        // We must pass the ServiceHandler.Request() as a parameter to ServiceHandler Constructor.
        // This way, we can override ServiceHandler.Request's method performRequest with our own code.
        // This code will be executed in our thread.
        final ServiceHandler serviceHandler = new ServiceHandler(new ServiceHandler.Request() {
            @Override
            public Object performRequest() {
                // This method will be called in our thread.
                return retrieveHasCommentedValues();
            }
        });

        // The thread has finished retrieving data. The callback below is called.
        serviceHandler.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                CommentsListResponse commentsListResponse = (CommentsListResponse) serviceHandler.getValue();

                if (!commentsListResponse.getError())
                {
                    System.out.println("No Error.");
                }
                else {
                    if (commentsListResponse.getCode() == 103)
                        System.out.println("103");
                    else {
                        ResourceBundle errorBundle = ResourceBundle.getBundle("bundles.Error", mLocale);
                        System.out.println(ErrorHandler.getDescription(commentsListResponse.getCode(), errorBundle));
                    }
                }
            }
        });

        serviceHandler.restart();
    }

    // Execution of the request
    private Object retrieveHasCommentedValues() {
        // Creation of the JSonParser
        JSONParser parser = new JSONParser(Config.HAS_COMMENTED_URL);

        // Parameters of the post request
        List<NameValuePair> urlParameters = new ArrayList<>();

        urlParameters.add(new BasicNameValuePair(idApplicationField, Integer.toString(applicationId)));
        urlParameters.add(new BasicNameValuePair(idAuthorField, Integer.toString(authorId)));

        // Execution of the request
        return parser.postRequest(urlParameters, new CommentsListResponse());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mResources = resources;
        mLocale = mResources.getLocale();

        //TODO: virer ça quand on pourra transmettre l'appID
        applicationId = 1;
        authorId = 3;

        checkHasCommentedCurrentApp();
        getCommentsList();
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
