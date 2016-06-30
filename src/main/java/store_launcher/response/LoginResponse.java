package store_launcher.response;

/**
 * Created by Thomas on 05/12/2015.
 *
 * This class is the basic response for the login request on the API.
 */
public class LoginResponse {

    // True if there is an error, False otherwise
    public boolean Error;
    // Error code returned
    public int Code;
    // Data (all the row about the user)
    public Data Data;

    public boolean getError() {
        return Error;
    }

    public void setError(boolean error) {
        Error = error;
    }

    public int getCode() {
        return Code;
    }

    public void setMessage(int code) {
        Code = code;
    }

    public class Data {

        public int idUsers;
        public String email;
        public String password;
        public String lastname;
        public String firstname;
        public int role;
        public String registration;

    }

}
