package rocky.teatime.helpers;

import android.preference.PreferenceManager;

import rocky.teatime.R;
import rocky.teatime.TeaTime;

/**
 * Created by rocky on 17-07-08.
 */

public class SettingsHelper {

    // Settings Keys
    private static final String TEMP_KEY = "tempPreference";
    private static final String VIBRATE_PREFERENCE_KEY = "vibrateAlarm";

    // If user prefers an imperial key, the result in the array is "American"
    private static final String FAHRENHEIT_FLAG = "American";

    /**
     * Checks the user's preferences and checks if their preferred temperature mode is for the
     * imperial mode.
     * @return True if the user prefers imperial temperatures, false otherwise
     */
    public static boolean isTemperatureFahrenheit() {
        return PreferenceManager.getDefaultSharedPreferences(TeaTime.getAppContext()).getString(TEMP_KEY,
                new String()).equals(FAHRENHEIT_FLAG);
    }

    /**
     * Checks the users preferences to check if they prefer a vibrate mode or not.
     * @return True if they just wish for their device to vibrate when the tea is done brewing, false
     * otherwise.
     */
    public static boolean isVibrateMode() {
        return PreferenceManager.getDefaultSharedPreferences(TeaTime.getAppContext()).getBoolean(VIBRATE_PREFERENCE_KEY,
                false);
    }
}
