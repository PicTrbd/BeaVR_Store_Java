package store_app_list.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.stage.Window;
import org.apache.http.NameValuePair;
import store_launcher.network.ServiceHandler;
import store_launcher.response.ApplicationsResponse;
import store_launcher.response.CategoriesResponse;
import utils.Config;
import utils.ControllersTools;
import utils.JSONParser;
import utils.StoredValues;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by Nicolas on 2/16/2016.
 */
public class ApplicationsListController implements Initializable {

    @FXML
    private Parent root;
    @FXML
    private ComboBox<String> theme_combobox;
    @FXML
    private ComboBox<String> filter_combobox;
    @FXML
    private VBox category_box_holder;
    @FXML
    private VBox device_box_holder;
    @FXML
    private VBox application_list;
    @FXML
    private VBox filters_menu_holder;


    private ResourceBundle mResources;

    private List<CheckBox> mSelectedCategoriesCheckBoxes;
    private List<CheckBox> mSelectedDevicesCheckBoxes;
    private List<ApplicationsResponse.Applications> mApplications;
    private List<ApplicationsResponse.Applications> mDisplayedApplications;
    private List<String> mCurrentCategories;
    private boolean isAllApplicationsSelected;

    @FXML
    protected void handleFeedbackLink(ActionEvent event) throws InterruptedException, IOException {
        Scene scene = ((Node) event.getTarget()).getScene();
        Window window = ((Node) event.getTarget()).getScene().getWindow();
        ControllersTools.showResizableWindow("BeaVR - " + mResources.getString("feedback_title"), window.getWidth(), window.getHeight(),
                getClass().getResource("/layouts/feedback.fxml"),
                getClass().getResource("/styles/app_list.css"),
                root, ControllersTools.isWindowMaximized(scene), new Locale("FR"));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mSelectedCategoriesCheckBoxes = new ArrayList<>();
        mSelectedDevicesCheckBoxes = new ArrayList<>();
        mCurrentCategories = new ArrayList<>();
        mResources = resources;
        setComboboxesValues();
        setDevices();
        setApplications();
    }

    private void setComboboxesValues() {
        setCategoryTypes();
        setFilters();
    }

    private void setFilters() {
        filter_combobox.getSelectionModel().clearSelection();
        filter_combobox.getItems().clear();
        filter_combobox.setPromptText("Trier par");
        filter_combobox.getItems().addAll("Date", "Prix");
        filter_combobox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    switch (newValue) {
                        case "Prix":
                            List<ApplicationsResponse.Applications> applicationsToSortByPrice = new ArrayList<>(mDisplayedApplications);
                            applicationsToSortByPrice.sort(new Comparator<ApplicationsResponse.Applications>() {
                                @Override
                                public int compare(ApplicationsResponse.Applications o1, ApplicationsResponse.Applications o2) {
                                    if (o1.price > o2.price) {
                                        return 1;
                                    } else if (o1.price < o2.price) {
                                        return -1;
                                    }
                                    return 0;
                                }
                            });
                            setApplicationsToVBOX(applicationsToSortByPrice);
                            break;
                        case "Date":
                            List<ApplicationsResponse.Applications> applicationsToSortByDate = new ArrayList<>(mDisplayedApplications);
                            applicationsToSortByDate.sort(new Comparator<ApplicationsResponse.Applications>() {
                                @Override
                                public int compare(ApplicationsResponse.Applications o1, ApplicationsResponse.Applications o2) {
                                    if (o1.creationDate.after(o2.creationDate)) {
                                        return 1;
                                    } else if (o1.creationDate.before(o2.creationDate)) {
                                        return -1;
                                    }
                                    return 0;
                                }
                            });
                            setApplicationsToVBOX(applicationsToSortByDate);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }

    /*
     * Get the apps according to the checkboxes selected
     */
    private void getAppsFiltered() {
        setFilters();
        if (mSelectedCategoriesCheckBoxes.size() == 0 && mSelectedDevicesCheckBoxes.size() == 0 && !isAllApplicationsSelected) {
            getAppsFromCategoryTypes();
        } else if (mSelectedCategoriesCheckBoxes.size() == 0 && mSelectedDevicesCheckBoxes.size() == 0 && isAllApplicationsSelected) {
            setApplicationsToVBOX(mApplications);
        } else {
            List<ApplicationsResponse.Applications> filteredApps = new ArrayList<>(mApplications);
            List<String> selectedCategoriesOrDevices = new ArrayList<>();
            for (CheckBox selectedCategoryCheckBox : mSelectedCategoriesCheckBoxes) {
                selectedCategoriesOrDevices.add(selectedCategoryCheckBox.getText());
            }
            for (CheckBox selectedDeviceCheckBox : mSelectedDevicesCheckBoxes) {
                selectedCategoriesOrDevices.add(selectedDeviceCheckBox.getText());
            }
            List<ApplicationsResponse.Applications> appsToRemove = new ArrayList<>();
            for (ApplicationsResponse.Applications application : filteredApps) {
                if (!checkAppCategoriesAndDevices(application, selectedCategoriesOrDevices)) {
                    appsToRemove.add(application);
                }
            }
            filteredApps.removeAll(appsToRemove);
            setApplicationsToVBOX(filteredApps);
        }
    }

    private void getAppsFromCategoryTypes() {
        if (mCurrentCategories.size() != 0) {
            List<ApplicationsResponse.Applications> filteredApps = new ArrayList<>(mApplications);
            List<ApplicationsResponse.Applications> appsToRemove = new ArrayList<>();
            for (ApplicationsResponse.Applications application : filteredApps) {
                if (!checkAppCategoriesAndDevices(application, mCurrentCategories)) {
                    appsToRemove.add(application);
                }
            }
            filteredApps.removeAll(appsToRemove);
            setApplicationsToVBOX(filteredApps);
        } else {
            setApplicationsToVBOX(mApplications);
        }
    }

    private boolean checkAppCategoriesAndDevices(ApplicationsResponse.Applications application, List<String> categoriesOrDevices) {
        List<String> appCategories = new ArrayList<>(Arrays.asList(application.categoriesNames.split(", ")));
        List<String> appDevices = new ArrayList<>(Arrays.asList(application.devicesNames.split(", ")));
        appCategories.addAll(appDevices);
        for (String categoryOrDevice : categoriesOrDevices) {
            if (appCategories.contains(categoryOrDevice)) {
                return true;
            }
        }
        return false;
    }

    private void setApplicationsToVBOX(List<ApplicationsResponse.Applications> applicationsList) {
        application_list.getChildren().clear();
        mDisplayedApplications = new ArrayList<>(applicationsList);
        for (ApplicationsResponse.Applications application : applicationsList) {
            /* Holder */
            BorderPane borderPane = new BorderPane();
            borderPane.getStyleClass().add("applications_item");
            borderPane.prefHeight(93);
            borderPane.prefWidth(1000);
            VBox.setMargin(borderPane, new Insets(10, 10, 0, 10));

            /* App header image */
            ImageView imageView = new ImageView();
            imageView.setImage(new Image(application.logo, true));
            imageView.setFitHeight(93);
            imageView.setFitWidth(200);
            imageView.setPickOnBounds(true);
            imageView.setPreserveRatio(false);
            imageView.setCache(true);
            borderPane.setLeft(imageView);

            /* Borderpane containing the app name, the app description and the categoryTypes*/
            BorderPane innerPane = new BorderPane();
            innerPane.setPrefHeight(93);
            innerPane.setPrefWidth(250);

            /* VBox containing the app name and the app description */
            VBox vBox = new VBox();
            Label appTitle = new Label(application.name);
            appTitle.setWrapText(true);
            appTitle.getStyleClass().add("application_item_name");
            VBox.setMargin(appTitle, new Insets(0, 0, 0, 10));
            Label appDescription = new Label(application.description);
            appDescription.setWrapText(true);
            VBox.setMargin(appDescription, new Insets(0, 0, 0, 15));

            /* App categoryTypes */
            Text appCategories = new Text(application.categoriesNames + ", " + application.devicesNames);
            appCategories.setFont(Font.font("Helvetica", FontPosture.ITALIC, 11));
            BorderPane.setMargin(appCategories, new Insets(0, 0, 0, 10));

            /* App price */
            Text price = new Text(String.valueOf(application.price) + mResources.getString("currency"));
            price.getStyleClass().add("application_item_price");
            vBox.getChildren().addAll(appTitle, appDescription);
            innerPane.setTop(vBox);
            innerPane.setBottom(appCategories);
            borderPane.setCenter(innerPane);
            borderPane.setRight(price);
            BorderPane.setAlignment(price, Pos.CENTER);
            BorderPane.setMargin(price, new Insets(0, 10, 0, 5));
            application_list.getChildren().add(borderPane);
        }
    }

    private Object retrieveApplications() {
        // Creation of the JSonParser
        JSONParser parser = new JSONParser(Config.GET_APPLICATIONS_URL);
        // Parameters of the post request
        List<NameValuePair> urlParameters = new ArrayList<>();

        // Execution of the request
        return parser.getRequest(urlParameters, new ApplicationsResponse());
    }

    private void setApplications() {
        // Declare a new ServiceHandler object in order to create a new Thread.
        // We must pass the ServiceHandler.Request() as a parameter to ServiceHandler Constructor.
        // This way, we can override ServiceHandler.Request's method performRequest with our own code.
        // This code will be executed in our thread.
        final ServiceHandler serviceHandler = new ServiceHandler(new ServiceHandler.Request() {
            @Override
            public Object performRequest() {
                // This method will be called in our thread.
                return retrieveApplications();
            }
        });
        // The thread has finished retrieving data. The callback below is called.
        serviceHandler.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                ApplicationsResponse applicationsResponse = (ApplicationsResponse) serviceHandler.getValue();
                if (!applicationsResponse.getError()) {
                    mApplications = applicationsResponse.Applications;
                    setApplicationsToVBOX(mApplications);
                }
            }
        });
        serviceHandler.restart();
    }

    private void setDevices() {
        device_box_holder.getChildren().clear();
        List<String> devices = StoredValues.getDevices();
        for (String device : devices) {
            final CheckBox checkBox = new CheckBox(device);
            checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (newValue) {
                        mSelectedDevicesCheckBoxes.add(checkBox);
                        getAppsFiltered();
                    } else {
                        mSelectedDevicesCheckBoxes.remove(checkBox);
                        getAppsFiltered();
                    }
                }
            });
            device_box_holder.getChildren().add(checkBox);
            VBox.setMargin(checkBox, new Insets(5, 0, 0, 15));
        }
    }

    public void setCategoryTypes() {
        final List<String> categoryTypes = StoredValues.getCategoryTypes();
        theme_combobox.setPromptText(mResources.getString("topic"));
        for (String categoryType : categoryTypes) {
            theme_combobox.getItems().addAll(categoryType);
        }
        theme_combobox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                for (int i = 0; i < categoryTypes.size(); i++) {
                    if (categoryTypes.get(i).equals(newValue)) {
                        setCategories(i + 1);
                        isAllApplicationsSelected = i + 1 == 1;
                        setDevices();
                        setFilters();
                    }
                }
                mSelectedCategoriesCheckBoxes.clear();
            }
        });
    }

    private Object retrieveCategories(int categoryType) {
        // Creation of the JSonParser
        JSONParser parser = new JSONParser(Config.GET_CATEGORIES_URL);
        // Execution of the request
        return parser.getRequestFromID(categoryType, new CategoriesResponse());
    }

    private void setCategories(final int categoryType) {
        // Declare a new ServiceHandler object in order to create a new Thread.
        // We must pass the ServiceHandler.Request() as a parameter to ServiceHandler Constructor.
        // This way, we can override ServiceHandler.Request's method performRequest with our own code.
        // This code will be executed in our thread.
        final ServiceHandler serviceHandler = new ServiceHandler(new ServiceHandler.Request() {
            @Override
            public Object performRequest() {
                // This method will be called in our thread.
                return retrieveCategories(categoryType);
            }
        });
        category_box_holder.getChildren().clear();
        // The thread has finished retrieving data. The callback below is called.
        serviceHandler.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                CategoriesResponse categoriesResponse = (CategoriesResponse) serviceHandler.getValue();
                if (!categoriesResponse.getError()) {
                    setCategoriesToCheckBox(categoriesResponse.Categories);
                } else {
                    setApplicationsToVBOX(mApplications);
                }
            }
        });
        serviceHandler.restart();
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefWidth(25);
        progressIndicator.setPrefHeight(25);
        progressIndicator.setVisible(false);
        filters_menu_holder.getChildren().add(progressIndicator);
        progressIndicator.visibleProperty().bind(serviceHandler.runningProperty());
    }

    private void setCategoriesToCheckBox(List<CategoriesResponse.Categories> categories) {
        mCurrentCategories.clear();
        for (CategoriesResponse.Categories category : categories) {
            mCurrentCategories.add(category.name);
            final CheckBox checkBox = new CheckBox(category.name);
            checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (newValue) {
                        mSelectedCategoriesCheckBoxes.add(checkBox);
                        getAppsFiltered();
                    } else {
                        mSelectedCategoriesCheckBoxes.remove(checkBox);
                        getAppsFiltered();
                    }
                }
            });
            category_box_holder.getChildren().add(checkBox);
            VBox.setMargin(checkBox, new Insets(5, 0, 0, 15));
        }
        getAppsFromCategoryTypes();
    }

    // Temporary access to the list of commments
    @FXML
    protected void temporary_list_comments(ActionEvent event) throws InterruptedException, IOException {
        Scene scene = ((Node) event.getTarget()).getScene();
        Window window = ((Node) event.getTarget()).getScene().getWindow();
        ControllersTools.showResizableWindow("BeaVR - " + mResources.getString("feedback_title"), window.getWidth(), window.getHeight(),
                getClass().getResource("/layouts/comments_list.fxml"),
                getClass().getResource("/styles/app_list.css"),
                root, ControllersTools.isWindowMaximized(scene), new Locale("FR"));

    }
}
