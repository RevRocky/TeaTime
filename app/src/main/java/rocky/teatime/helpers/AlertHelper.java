package rocky.teatime.helpers;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import rocky.teatime.R;
import rocky.teatime.TeaTime;

/**
 * A class containing some useful methods that help with things like creating alerts
 * @author Rocky Petkov
 * @version .1
 */
public class AlertHelper {

    /**
     * Creates a simple alert displaying the message. Will likely be moved to it's own class later on.
     * @param message The message to display with the alert
     */
    public static void createOKAlert(String message, Context applicationContext) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(applicationContext);
        alertBuilder.setMessage(message);
        alertBuilder.setCancelable(false);

        // Constructing the buttons
        alertBuilder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();        // We just want to close our dialogue
            }
        });
        AlertDialog newAlert = alertBuilder.create();
        newAlert.show();
    }

    /**
     * Creates a simply one button alert. When the user acknoledges it, they will close the activity
     * and it will return to the last activity on the stack with the correct return code.
     * @param message Message we wish to display to the user.
     * @param activityToKill Activity we desire to terminate
     * @param exitCode Exit Code communicating the status under which the activity has terminated
     */
    public static void createActivityKillAlert(String message, final Activity activityToKill, final int exitCode) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activityToKill);
        alertBuilder.setMessage(message);
        alertBuilder.setCancelable(false);

        // Constructing the buttons
        alertBuilder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent returnIntent = new Intent();
                activityToKill.setResult(exitCode, returnIntent);
                activityToKill.finish();
                dialog.cancel();

            }
        });
        AlertDialog newAlert = alertBuilder.create();
        newAlert.show();
    }

    /**
     * Creates an alert which can be used to destroy a service corresponding to the supplied intent.
     * @param message The message to display in the alert.
     * @param applicationContext The context of the application when it calls this method!
     * @param serviceIntent An intent corresponding to the service we wish to destroy. It would
     *                      be the same intent supplied when originally starting the service
     * @param killActivity If true it will also kill the parent activity
     */
    public static void createServiceAndNotificationKillAlert(String message, final Activity applicationContext,
                                              final Intent serviceIntent, final boolean killActivity,
                                                             final int notificationID) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(applicationContext);
        alertBuilder.setMessage(message);
        alertBuilder.setCancelable(false);

        // Constructing the buttons
        alertBuilder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                applicationContext.stopService(serviceIntent);
                dialog.cancel();        // We just want to close our dialogue

                // If we are told to... kill the activity
                if (killActivity) {
                    applicationContext.finish();
                }

                //  Kill the notification
                NotificationManager manager =
                        (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);

                manager.cancel(notificationID);
            }
        });
        AlertDialog newAlert = alertBuilder.create();
        newAlert.show();
    }
}
