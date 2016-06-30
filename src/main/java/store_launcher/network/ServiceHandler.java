package store_launcher.network;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
/**
 * Created by Nicolas on 12/18/2015.
 */
public class ServiceHandler extends Service<Object> {

    public interface Request {
        Object performRequest();
    }

    Request mRequest;

    public ServiceHandler(Request request) {
        mRequest = request;
    }

    @Override
    protected Task<Object> createTask() {
        return new Task<Object>() {
            @Override
            protected Object call() throws Exception {
                return mRequest.performRequest();
            }
        };
    }
}
