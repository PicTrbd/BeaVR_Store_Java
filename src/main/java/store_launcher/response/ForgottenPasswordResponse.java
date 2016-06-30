package store_launcher.response;

/**
 * Created by Thomas on 22/01/2016.
 *
 * This class is the basic response for the forgotten password request on the API.
 */
public class ForgottenPasswordResponse {

    // True if there is an error, False otherwise
    public boolean Error;
    // Code returned
    public int Code;

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

}
