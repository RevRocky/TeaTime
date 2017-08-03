package rocky.teatime.helpers;

import android.app.Activity;
import android.support.v4.util.Pair;
import android.util.DisplayMetrics;

/**
 * A helper class which handles.... everything which doesn't really have a home
 */
public class MiscHelper {

    private static float gramsToOuncesConst = .083454f;  // 1 gram per 100 ml equates to .008 ounces per cup

    /**
     * Takes an integer amount of seconds and returns a pair with an equivalent amount of time in
     * minutes/seconds.
     * @param seconds An integer containing an amount of seconds
     * @return A pair where the first value is an amount of minutes and the second value is the amount
     * of seconds left over
     */
    public static Pair<Integer, Integer> secondsToMinutes(int seconds) {
        int minutes = seconds / 60;     // Sixty seconds in a minute
        seconds %= 60;                  // Remaining seconds is seconds modulo 60
        return new Pair<>(minutes, seconds);
    }

    /**
     * Returns the total screen width of the calling activity
     * @param callingActivity Activity making the request
     * @return Width of the screen within the calling activity
     */
    public static int getScreenWidth(Activity callingActivity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        callingActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * Returns the total screen height of the calling activity
     * @param callingActivity Activity making the request
     * @return Height of the screen within the calling activity
     */
    public static int getScreenHeight(Activity callingActivity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        callingActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    /**
     * Like Ellis Island but for a temperature
     * @param tempInCentigrade Huddled masses yearning to breathe free
     * @return A proud, patriotic American. God Bless. MAGA!!
     */
    public static int centigradeToFahrenheit(int tempInCentigrade) {
        return Math.round(tempInCentigrade * (1.8f) + 32);
    }

    /**
     * Converts imperial temperatures into bloody communist units.
     * @param tempInFahrenheit Temperature in AMERICAN
     * @return The same temperature, yearning to breathe free
     */
    public static int fahrenheitToCentigrade(int tempInFahrenheit) {
        return Math.round((tempInFahrenheit - 32) * (5.0f / 9.0f));
    }

    /**
     * Converts a measurement in ounces per US cup water to grams per 100mL water.
     * @param ouncesPerCup A measurement of ideal brew strength in ounces per US cup of water
     * @return An equivalent measurement in terms of grams of tea per 100mL
     */
    public static float ouncesToGrams(float ouncesPerCup) {
        return ouncesPerCup * gramsToOuncesConst;
    }

    /**
     * Converts a measurement in grams per 100mL water to ounces per cup of water
     * @param gramsPerHundredmL The measurement in grams per 100mL water
     * @return Measurement in ounces per cup water
     */
    public static float gramsToOunces(float gramsPerHundredmL) {
        return gramsPerHundredmL / gramsToOuncesConst;
    }

}
