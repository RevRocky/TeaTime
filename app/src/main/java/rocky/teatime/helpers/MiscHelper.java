package rocky.teatime.helpers;

import android.support.v4.util.Pair;

/**
 * A helper class which handles.... everything which doesn't really have a home
 */
public class MiscHelper {
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
}
