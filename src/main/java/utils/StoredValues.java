package utils;

import java.util.List;

/**
 * Created by Nicolas on 2/21/2016.
 */
public class StoredValues {

    private static List<String> categoryTypes;
    private static List<String> devices;

    public static List<String> getCategoryTypes() {
        return categoryTypes;
    }

    public static void setCategoryTypes(List<String> categoryTypesList) {
        categoryTypes = categoryTypesList;
    }

    public static List<String> getDevices() {
        return devices;
    }

    public static void setDevices(List<String> devicesList) {
        devices = devicesList;
    }
}
