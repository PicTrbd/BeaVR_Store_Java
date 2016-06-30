package utils;

/**
 * Created by Nicolas on 2/3/2016.
 */
public class Config {

    public static final String BASE_URL = "http://localhost:3000/api";

    public static final String RESET_PASSWORD_URL = BASE_URL + "/reset-password/";
    public static final String LOGIN_URL = BASE_URL + "/connection/";
    public static final String REGISTER_URL = BASE_URL + "/registration/";
    public static final String SEND_FEEDBACK_URL = BASE_URL + "/sendFeedback/";
    public static final String GET_DEVICES_URL = BASE_URL + "/devices/";
    public static final String GET_CATEGORY_TYPES_URL = BASE_URL + "/categoryTypes/";
    public static final String GET_CATEGORIES_URL = BASE_URL + "/categories/";
    public static final String GET_APPLICATIONS_URL = BASE_URL + "/applications/";
    public static final String GET_CATEGORY_TYPES_AND_DEVICES_URL = BASE_URL + "/categorytypesanddevices/";
    public static final String GET_COMMENTS_URL = BASE_URL + "/getComments/";
    public static final String HAS_COMMENTED_URL = BASE_URL + "/hasCommented";
}
