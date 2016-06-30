package store_launcher.response;

import java.util.List;

/**
 * Created by Nicolas on 2/18/2016.
 */
public class CategoriesResponse {
    // True if there is an error, False otherwise
    public boolean Error;
    // Error code returned
    public int Code;
    // Data (all the row about the user)
    public List<Categories> Categories;

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

    public static class Categories {

        public int idCategories;
        public String name;
        public String description;
        public int type;
    }
}
