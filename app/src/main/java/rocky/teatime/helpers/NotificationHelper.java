package rocky.teatime.helpers;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import rocky.teatime.R;
import rocky.teatime.activities.DatabaseActivity;

import static android.support.v4.app.NotificationCompat.DEFAULT_ALL;
import static android.support.v4.app.NotificationCompat.FLAG_AUTO_CANCEL;

/**
 * A mostly static helper class that contains some methods which ease the
 * notification creation process
 * @author Rocky Petkov
 * @version The first
 */
public class NotificationHelper {

    private static int CURRENT_NOTIFICATION_ID = 0; // This can be incremented + decremented for multiple brews at once.

    /**
     * Generates a notification which will return the user to an already running instance of the
     * supplied activity.
     * @param priorActivity The activity we wish to return to.
     * @param message Message to display on the notification
     * @param priority Priority level to assign to the notification.
     */
    public static int generateAppReturnNotification(Activity priorActivity, String message,
                                                     int priority){
        int notificationID = CURRENT_NOTIFICATION_ID;

        /*
         *  TBH I have no idea what changing the class for the second parameter did to resolve the
         *  issue of the notification persisting beyond when the user went back to the Tea Viewing
         *  Screen but it seemed to work!
         */
        Intent notificationIntent = new Intent(priorActivity, DatabaseActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.setFlags(Notification.FLAG_AUTO_CANCEL); // Or maybe it was this!
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent intent = PendingIntent.getActivity(priorActivity, 0, notificationIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(priorActivity)
                .setContentTitle(priorActivity.getString(R.string.app_name))    // TODO: Think about changing this to name of tea
                .setContentIntent(intent)
                .setPriority(priority) //private static final PRIORITY_HIGH = 5;
                .setContentText(message)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.notification_icon)
                .setDefaults(DEFAULT_ALL);
        NotificationManager mNotificationManager =
                (NotificationManager) priorActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(CURRENT_NOTIFICATION_ID, mBuilder.build());
        CURRENT_NOTIFICATION_ID++;  // Increment the current id.
        return notificationID;
    }
}
