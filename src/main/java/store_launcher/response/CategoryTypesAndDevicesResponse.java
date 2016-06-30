package store_launcher.response;

/**
 * Created by Nicolas on 2/20/2016.
 */
public class CategoryTypesAndDevicesResponse {
    // True if there is an error, False otherwise
    public boolean Error;
    // Error code returned
    public int Code;
    // Data (all the row about the user)
    public CategoryTypesAndDevices CategoryTypesAndDevices;

    public boolean getError() {
        return Error;
    }

    public void setError(boolean error) {
        Error = error;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public static class CategoryTypesAndDevices {

        public String CategoryTypes;
        public String Devices;
    }

}
