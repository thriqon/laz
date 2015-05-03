package de.jonasw.laz;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by thriqon on 5/1/15.
 */
class Preferences {
    private static final String TIMER_PREFERENCES_FILE_NAME = "timer";
    private static final int TIMER_PREFERENCES_FLAGS = Context.MODE_PRIVATE;

    private Preferences() {}

    public static SharedPreferences getPreferencesFor(Context context) {
        return context.getSharedPreferences(TIMER_PREFERENCES_FILE_NAME, TIMER_PREFERENCES_FLAGS);
    }
}
