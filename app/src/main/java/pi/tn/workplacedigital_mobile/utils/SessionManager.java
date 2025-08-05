package pi.tn.workplacedigital_mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences("workplace_session", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveUsername(String username) {
        editor.putString("username", username);
        editor.apply();
    }

    public String getUsername() {
        return prefs.getString("username", null);
    }
}
