package rocky.teatime.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Vibrator;

import rocky.teatime.R;

/**
 * A Woman's (or a particular type of Man's) Best Friend.
 * @author Rocky Petkov
 * @version Final
 */
public class VibratorService extends Service {

    private static int vibrateLength = 500; // He likes it big

    Vibrator discreetAlarm;

    /**
     * This method exists... because it needs to.
     * @param intent The intent given to the service upon it's creation
     * @return A null-pointer of an IBinder type.
     */
    @Override
    public IBinder onBind(Intent intent) {
        // I exist to exist. What is my point.
        // What is any of our points?
        return null;
    }

    /**
     * Initialised the mediaplayer object with the desired wav file
     */
    public void onCreate() {
        discreetAlarm = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * Starts the media player
     * @param intent The impetus for us to start this looping media player
     * @param flags Additional data regarding this instance
     * @param startID An integer given to the method... for some reason
     * @return We return START_STICKY because if it goes away a poor soul might forget... And then
     * would have bitter tea... And that is no good!
     */
    public int onStartCommand(Intent intent, int flags, int startID) {
        discreetAlarm.vibrate(500);
        return START_STICKY;
    }

    /**
     * Stops the sound. This method will be called when the user closes the pop up alert!
     */
    @Override
    public void onDestroy() {
        discreetAlarm.cancel();
    }
}
