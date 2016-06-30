package store_launcher.response;

import java.util.Date;
import java.util.List;

/**
 * Created by Nicolas on 2/20/2016.
 */
public class ApplicationsResponse {
    // True if there is an error, False otherwise
    public boolean Error;
    // Error code returned
    public int Code;
    // Data (all the row about the user)
    public List<Applications> Applications;

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

    public static class Applications {

        public String name;
        public String description;
        public Date creationDate;
        public double price;
        public String logo;
        public String url;
        public String categoriesNames;
        public String devicesNames;
        public String authorName;
    }

}
