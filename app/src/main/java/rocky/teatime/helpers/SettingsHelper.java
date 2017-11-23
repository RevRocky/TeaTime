package rocky.teatime.helpers;

import android.content.Context;
import android.preference.PreferenceManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import rocky.teatime.TeaTime;
import rocky.teatime.activities.DatabaseActivity;

/**
 * Created by rocky on 17-07-08.
 */

public class SettingsHelper {

    // Settings Keys
    private static final String TEMP_KEY = "tempPreference";
    private static final String STRENGTH_KEY = "strengthPreference";
    private static final String VIBRATE_PREFERENCE_KEY = "vibrateAlarm";
    private static final String SORT_PREFERENCE_FILE = "sort_preference";   // Name of the sort preference file on disk.

    // If user prefers imperial measurements, the result in the array is "American"
    private static final String IMPERIAL_FLAG = "American";

    /**
     * Checks the user's preferences and checks if their preferred temperature mode is for the
     * imperial mode.
     * @return True if the user prefers imperial temperatures, false otherwise
     */
    public static boolean isTemperatureFahrenheit() {
        return PreferenceManager.getDefaultSharedPreferences(TeaTime.getAppContext()).getString(TEMP_KEY,
                new String()).equals(IMPERIAL_FLAG);
    }

    /**
     * Checks the users preferences to check for their preferred measurement of tea strength
     * @return True if the user prefers imperial strength measurements, false otherwise
     */
    public static boolean isStrengthImperial() {
        return PreferenceManager.getDefaultSharedPreferences(TeaTime.getAppContext()).getString(STRENGTH_KEY,
                new String()).equals(IMPERIAL_FLAG);
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

    /**
     * Saves the preferred sort type of the user to a file. I'm choosing to do this instead of the more
     * kosher Preferences.xml as I want this to be something the user can pick outside the preferences
     * screen.
     * @param sortType The user's preferred sort type.
     */
    public static void savePreferredSortType(byte sortType) {
        FileOutputStream output;
        byte[] array = {sortType};

        try {
            output = TeaTime.getAppContext().openFileOutput(SORT_PREFERENCE_FILE, Context.MODE_PRIVATE);
            output.write(array);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads from our preferred sort file to get the user's preferred sort criteria.
     * @return A byte value matching the user's preferred sort criteria. Values match the constants
     * defined in the DatabaseActivity. If an exception occurs it fails silent returning the default sort
     * which is sort no. 4 (Most Recently Added)
     */
    public static byte getPreferredSortType() {
        FileInputStream fileIn;
        byte[] fileBuffer = new byte[1];
        try {
            fileIn = TeaTime.getAppContext().openFileInput(SORT_PREFERENCE_FILE);
            fileIn.read(fileBuffer);    // Reads in the number to our buffer!

        } catch (IOException e) {
            return DatabaseActivity.SORT_BY_OLD;
        }

        return fileBuffer[0];
    }

}
