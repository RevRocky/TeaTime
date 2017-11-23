package rocky.teatime.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import rocky.teatime.R;
import rocky.teatime.helpers.AlertHelper;
import rocky.teatime.helpers.NotificationHelper;
import rocky.teatime.helpers.SettingsHelper;
import rocky.teatime.services.AlarmService;
import rocky.teatime.services.VibratorService;

import static android.support.v4.app.NotificationCompat.PRIORITY_DEFAULT;
import static android.support.v4.app.NotificationCompat.PRIORITY_HIGH;


/**
 * An actiity class wich runs the timer and the alarm for when the user's tea is ready.
 * @Author Rocky Petkov
 * @version Semi-Final
 */
public class TimerActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private int startTime;      // The amount of seconds to begin with
    private float timeElapsed;
    private float progress;       // Progress of brew as a percentage of time remaining
    private float step;

    private Handler handler;
    TextView textProgress;

    public static String START_KEY = "START_TIME";
    private final String TIME_GONE = "TIME_GONE";
    private final String PROGRESS_KEY = "PROGRESS";
    private final String STEP_KEY = "STEP";

    private final String WAKE_LOCK_KEY = "TEA_TIMER_WAKE_LOCK";

    private boolean prematureHalt;   // Lets timer know if it is okay to keep the clock ticking.

    /**
     * Handles the creation of the activity and establishing necessary brewTime
     * @param savedInstanceState A standard activity creation bundle... with a steep time
     *                           attached
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        setContentView(R.layout.timer_layout);
        textProgress = (TextView) findViewById(R.id.txtProgress);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Initialise prematureHalt to false.
        prematureHalt = false;

        // Reading the amount of time remaining from the bundle
        Bundle extras = getIntent().getExtras();

        // Checking to see if this is a fresh activity or  we are resuming from an earlier one
        if (savedInstanceState != null) {
            startTime = savedInstanceState.getInt(START_KEY);
            timeElapsed = (float) savedInstanceState.getDouble(TIME_GONE);
            progress = (float) savedInstanceState.getDouble(PROGRESS_KEY);
            step = (float) savedInstanceState.getDouble(STEP_KEY);
        }
        else if (extras != null) {    // If it is not empty read the brew time, otherwise do nothing.
            startTime = extras.getInt(START_KEY);
            timeElapsed = 0.0f;
            progress = 0.0f;   // We start at 0 percent.
            step = (float) startTime * .01f;  // The amount of seconds in one percent!
        }
        else {
            System.err.println("No time passed in to the timer!");
            finish();   // We're done. Nothing to time
        }
        startTimer();   // Putting the timer code in it's own method for cleanliness
    }

    /**
     * A little method that handles the timer and it's associated thread!
     */
    private void startTimer() {

        // Forgoing the anonymous class so I can stop the timer when I desire to.
        new Thread (new Runnable() {
            @Override
            public void run() {

                // Get the Wake Lock
                PowerManager powerManager = (PowerManager)getSystemService(POWER_SERVICE);
                PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        WAKE_LOCK_KEY);
                wakeLock.acquire();

                // Begin the main loop
                while (progress <= 100f) {

                    // Check to see if there is a premature halt.
                    if (!prematureHalt) {
                        handler.post(new Runnable() {
                            // Seems to be a pattern
                            @Override
                            public void run() {
                                progressBar.setProgress(Math.round(progress));
                                textProgress.setText(displayTimeLeft());   // Set the text with time left
                            }
                        });
                        // Sleep for a bit
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();    // Print the stack trace
                        }
                        // Lots of updatin]g now. Also... Dave Dove would kill me for this...
                        // or he'd dispatch Galal to kill me off.
                        timeElapsed += .1f; // 100 milliseconds is 1-tenth of a second
                        progress = timeElapsed / step;
                    }
                    else {
                        // Ensuring we have the wakelock

                        if (wakeLock.isHeld()) {
                            return;     // The timer has been ordered to stop prematurely. We must return.
                        }
                    }
                }

                // Check if the wake lock is held
                final Intent alarmIntent = playAlarm();    // I really don't like to have length of inner classes be long....

                // What if we run just the alert helper on the ui thread?
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final int notificationID = NotificationHelper.generateAppReturnNotification(TimerActivity.this,
                                getResources().getString(R.string.TeaReady), PRIORITY_DEFAULT);
                        AlertHelper.createServiceAndNotificationKillAlert(getResources().getString(R.string.TeaReady),
                                TimerActivity.this, alarmIntent, true, notificationID);
                    }
                });

                // Ensuring we hold the wakelock before releasing it
                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }
            }
        }).start();
    }

    /**
     * Handles the construction of the alarm service
     * @return Returns the intent associated with the alarm service created!
     */
    private Intent playAlarm() {
        Intent alarmIntent;
        boolean vibratePreference = SettingsHelper.isVibrateMode();

        // Does the user wish for their device to vibrate or not?
        if (vibratePreference){
            alarmIntent = new Intent(this, VibratorService.class);
        }
        else {
            // User does not a vibrate based alarm
            alarmIntent = new Intent(this, AlarmService.class);
        }
        startService(alarmIntent);
        return alarmIntent;
    }

    /**
     * If the user hits the cancel button this should bring them back to the main database screen.
     * @param view View object encapsulating the button hit. We disregard this.
     */
    public void cancelBrew(View view) {
        prematureHalt = true;   // We are prematurely halting the timer.
        finish();   // Finish the activity
    }

    /**
     * Parses the amount of time remaining in a nicely formatted string
     * @return Returns the time left in a nicely formatted XX:XX string that is
     * oh so user friendly!
     */
    private String displayTimeLeft() {
        int timeRemaining = Math.round(startTime - timeElapsed);  // Get the amonunt of time remaining
        int minutes = timeRemaining / 60;   // Will truncate. Woot integer division
        int seconds = timeRemaining % 60;

        // Handling the nasty "0X thing"
        if (seconds >= 10) {
            return String.format("%d:%d", minutes, seconds);
        }
        else {
            return String.format("%d:0%d", minutes, seconds);   // Inserting a zero!
        }
    }

    /**
     * Saves the current state of the timer so it can be recreated once we've rotated the screen
     * @param newProgrammeState the outgoing programme state where we will save all our variables
     */
    @Override
    public void onRestoreInstanceState(Bundle newProgrammeState) {
        newProgrammeState.putInt(START_KEY, startTime);
        newProgrammeState.putDouble(TIME_GONE, timeElapsed);
        newProgrammeState.putDouble(PROGRESS_KEY, progress);
        newProgrammeState.putDouble(STEP_KEY, step);
        super.onRestoreInstanceState(newProgrammeState);
    }
}
