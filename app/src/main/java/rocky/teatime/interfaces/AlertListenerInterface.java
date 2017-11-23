package rocky.teatime.interfaces;

import android.app.DialogFragment;

/**
 * Small class meant to serve as a common interface so an activity can interact with
 * an alert which it has created
 */
public interface AlertListenerInterface {

    /**
     * What is to be done on clicking the positive button
     * @param dialogFragment Dialogue on which the postitive button was hit
     */
    public void onDialoguePositiveClick(DialogFragment dialogFragment);

    /**
     * What is to be done on clicking the negative button
     * @param dialogFragment Dialogue on which the negative choice was selected
     */
    public void onDialogueNegativeClick(DialogFragment dialogFragment);

}
