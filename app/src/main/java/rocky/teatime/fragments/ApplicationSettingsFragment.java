package rocky.teatime.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import rocky.teatime.R;

/**
 * Created by Rocky on 31-May-17.
 */

public class ApplicationSettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Loading the preferences from our XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
