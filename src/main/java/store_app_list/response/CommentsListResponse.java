package store_app_list.response;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Thomas on 22/02/2016.
 */
public class CommentsListResponse {
    // True if there is an error, False otherwise
    public boolean Error;
    // Error code returned
    public int Code;
    // Data (all the row about the user)
    public List<Comment> Comments;

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

    public static class Comment {

        public int idComment;
        public String comment;
        public int rating;
        public int author;
        public int application;
        public Timestamp date;
    }
}
