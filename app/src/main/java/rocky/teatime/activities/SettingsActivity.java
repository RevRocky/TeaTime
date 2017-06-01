package rocky.teatime.activities;

import android.app.Activity;
import android.os.Bundle;

import rocky.teatime.fragments.ApplicationSettingsFragment;

/**
 * Created by Rocky on 31-May-17.
 */
public class SettingsActivity extends Activity {

    /**
     * Simply displays the settings screen. The preferences fragment should
     * take care of the rest.
     * @param savedInstanceState Programme state on activity creation
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Displaying the settings fragment as the main content
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new ApplicationSettingsFragment()).commit();
    }
}
