package rocky.teatime.fragments.alerts;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import rocky.teatime.R;
import rocky.teatime.database.teastuff.Tea;
import rocky.teatime.interfaces.AlertListenerInterface;

/**
 * Created by rocky on 17-09-22.
 */

public class TeaDeletionConfirmationAlert extends DialogFragment {

    AlertListenerInterface listener;    // The interface used to deliver action events

    /**
     * Does everything we need inorder to create and display the dialogue
     * @param savedInstanceState
     * @return The alert, ready to show the world.
     */
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Getting the String resources
        Resources resources = getActivity().getResources();

        // Building the dialog and establishing the click handlers
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setMessage(resources.getString(R.string.DeletionAffirmation));
        alertBuilder.setCancelable(false);

        // Constructing the buttons
        alertBuilder.setPositiveButton(resources.getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Calling the method in the activity
                listener.onDialoguePositiveClick(TeaDeletionConfirmationAlert.this);
            }
        });

        // Constructing the buttons
        alertBuilder.setNegativeButton(resources.getString(R.string.No), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                listener.onDialogueNegativeClick(TeaDeletionConfirmationAlert.this);
            }
        });
       return alertBuilder.create();
    }

    /**
     * Simply attaching this dialogue to the main activity so that we can call the onPositive/
     * onNegative methods there. This will ensure a more linear execution
     * @param appContext Present application context
     */
    @Override
    public void onAttach(Context appContext) {
        super.onAttach(appContext);

        // Ensuring that the calling application has the AlertListenerInterface applied
        try {
            // Instantiate the dialogue listener so that we can communicate with it
            listener = (AlertListenerInterface) appContext;
        } catch (ClassCastException e) {
            throw new ClassCastException(appContext.toString() + "does not implement " +
                    "the AlertListenerInterface!");
        }
    }

    public AlertListenerInterface getListener() {
        return listener;
    }
}
