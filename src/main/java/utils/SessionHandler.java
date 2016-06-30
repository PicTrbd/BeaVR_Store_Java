package utils;

import java.util.prefs.Preferences;

/**
 * Created by Nicolas on 3/1/2016.
 */
public class SessionHandler {

    public static final String PREFERENCE_USERNAME_ATTRIBUTE = "preference_username";
    public static final String PREFERENCE_PASSWORD_ATTRIBUTE = "preference_password";
    private static final Preferences mPreferences = Preferences.userNodeForPackage(SessionHandler.class);

    public static void setCredentials(String username, String password) {
        mPreferences.put(PREFERENCE_USERNAME_ATTRIBUTE, username);
        mPreferences.put(PREFERENCE_PASSWORD_ATTRIBUTE, password);
    }

    public static boolean isAlreadyConnected() {
        return (mPreferences.get(PREFERENCE_USERNAME_ATTRIBUTE, null) != null && mPreferences.get(PREFERENCE_PASSWORD_ATTRIBUTE, null) != null);
    }

    public static String getUsername() {
        return mPreferences.get(PREFERENCE_USERNAME_ATTRIBUTE, null);
    }

    public static String getPassword() {
        return mPreferences.get(PREFERENCE_PASSWORD_ATTRIBUTE, null);
    }

    public static void terminateSession() {
        mPreferences.remove(PREFERENCE_USERNAME_ATTRIBUTE);
        mPreferences.remove(PREFERENCE_PASSWORD_ATTRIBUTE);
    }
}
