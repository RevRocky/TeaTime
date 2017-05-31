package rocky.teatime.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import rocky.teatime.R;
import rocky.teatime.helpers.AlertHelper;
import rocky.teatime.helpers.NotificationHelper;
import rocky.teatime.services.AlarmService;

import static android.support.v4.app.NotificationCompat.PRIORITY_HIGH;


/**
 * An actiity class wich runs the timer and the alarm for when the user's tea is ready
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

        // Reading the amount of time remaining from the bundle
        Bundle extras = getIntent().getExtras();

        // If it is not empty read the brew time, otherwise do nothing.
        if (extras != null) {
            startTime = extras.getInt("BrewTime");
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

        // Initialise the thread and give it a method!
        new Thread (new Runnable() {
            @Override
            public void run() {
                while (progress <= 100f) {
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
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();    // Print the stack trace
                    }
                    // Lots of updating now. Also... Dave Dove would kill me for this...
                    // or he'd dispatch Galal to kill me off.
                    timeElapsed += .1f; // 100 milliseconds is 1-tenth of a second
                    progress = timeElapsed / step;
                }

                final Intent alarmIntent = playAlarm();    // I really don't like to have length of inner classes be long....
                NotificationHelper.generateAppReturnNotification(TimerActivity.this, getResources().getString(R.string.TeaReady),
                        PRIORITY_HIGH);
                // What if we run just the alert helper on the ui thread?
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertHelper.createServiceKillAlert(getResources().getString(R.string.TeaReady),
                                TimerActivity.this, alarmIntent, true);
                    }
                });
            }
        }).start();
    }

    /**
     * Handles the construction of the alarm service
     * @return Returns the intent associated with the alarm service created!
     */
    private Intent playAlarm() {
        Intent alarmIntent = new Intent(this, AlarmService.class);
        startService(alarmIntent);  // Should handle the construction and destruction of the service
        return alarmIntent;
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
}
