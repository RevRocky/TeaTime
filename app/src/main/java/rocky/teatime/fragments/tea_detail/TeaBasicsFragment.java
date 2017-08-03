package rocky.teatime.fragments.tea_detail;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import rocky.teatime.R;
import rocky.teatime.TeaTime;
import rocky.teatime.activities.TimerActivity;
import rocky.teatime.database.TeaStuff.JsonTea;
import rocky.teatime.database.TeaStuff.Tea;
import rocky.teatime.helpers.MiscHelper;
import rocky.teatime.helpers.SettingsHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link TeaBasicsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeaBasicsFragment extends Fragment {

    private Tea teaBeingViewed;
    private BrewTeaViewFragment brewingFragment;
    private View selfReferenceView;

    // A Set of integers that communicates the size of the image object for each screen size
    private static int XL_SCREEN_IMAGE_HEIGHT = 480;

    /**
     * Returns the image height used for the given screen size. This is so we know how big to make
     * the image. For now, this simply returns the size used on a 5.5 inch screen.
     * @return Image height used on the user's choice of phone screen
     */
    public static int getImageHeight() {
        // TODO: Upon testing on other screen sizes, build into a case statement
        return XL_SCREEN_IMAGE_HEIGHT;
    }

    /**
     * This is a factory method meant to quickly establish the tea_basics fragment
     * @return
     */
    public static TeaBasicsFragment newInstance(String jsonTea) {
        TeaBasicsFragment fragment = new TeaBasicsFragment();
        Bundle args = new Bundle();
        args.putString(Tea.TEA_PAYLOAD_KEY, jsonTea);   // Using the standard tea payload key
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Reads the jsonised tea from the arguments supplied on creation and stores a reference to the
     * object in this here object.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String jsonisedTea = getArguments().getString(Tea.TEA_PAYLOAD_KEY);
            // Making tea from our Json object!
            teaBeingViewed = new Gson().fromJson(jsonisedTea, JsonTea.class).makeTea();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        selfReferenceView = inflater.inflate(R.layout.fragment_tea_basics, container, false);
        brewingFragment = (BrewTeaViewFragment) getChildFragmentManager().findFragmentById(R.id.tea_brew_fragment);
        setPicture();
        buildBrewFragment();
        return selfReferenceView;
    }

    /**
     * Sets the picture to match that which is associated with the tea we are examining
     */
    public void setPicture() {
        String imageLocation = teaBeingViewed.getPicLocation();
        if (!imageLocation.equals("NULL")) {
            ImageView imageView = (ImageView) selfReferenceView.findViewById(R.id.collapsing_tea_image);
            imageView.setImageBitmap(BitmapFactory.decodeFile(imageLocation));
        }
    }

    /**
     * Builds all of the information necessary for the brew fragment to display correct information
     * to the user
     */
    public void buildBrewFragment() {
        // Setting up the fields to do with the timer
        establishTimerFields();

        // Determining if the user has entered one or two temperatures
        if (teaBeingViewed.getBrewMin() == Tea.EMPTY_ENTRY_FLAG) {
            establishSingleTempField(teaBeingViewed.getBrewMax());  // Single temp view with the max temperature
        }
        else {
            establishMultiTempFields();
        }

        // Setting up the strength view to the user
        establishStrengthFields();
    }

    /**
     * A helper method handling the establishment of fields to do with brew timnes
     */
    private void establishTimerFields() {
        TextView[] timeViews = {brewingFragment.getMinTimeView(), brewingFragment.getMaxTimeView()};
        ImageButton[] brewButtons = {brewingFragment.getBrewButtonFirst(),
                brewingFragment.getBrewButtonSecond()};
        final int[] rawTimes = {teaBeingViewed.getBrewTime(), teaBeingViewed.getBrewTimeSub()};
        Pair<Integer, Integer> minSecs;
        int i;

        // Loop through, displaying the time the tea will take to brew and, if a time has been
        // entered, establish an on click listener
        for (i = 0; i < rawTimes.length; i ++) {
            if (rawTimes[i] > 0){

                // Not worth doing a boolean check, we'll just purposefully set everything to be
                // visible in this case
                brewButtons[i].setVisibility(View.VISIBLE);
                timeViews[i].setVisibility(View.VISIBLE);

                minSecs = MiscHelper.secondsToMinutes(rawTimes[i]);
                // Handling that pesky little thing where you have to have "0X" if the number of
                // seconds is less than 10.
                if (minSecs.second < 10) {
                    timeViews[i].setText(String.format("%d:0%d", minSecs.first, minSecs.second));
                }
                else {
                    timeViews[i].setText(String.format("%d:%d", minSecs.first, minSecs.second));
                }

                // Attaching an On Click Listener. Unfortunately, this extra assignment is
                // as I see it necessary, despite it being just the worst style. I don't reckon that
                // this would be the case if lambdas were in this version of jacvs
                final int rawTime = rawTimes[i];
                brewButtons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch a timer activity with the specified time
                        Intent timerIntent = new Intent(TeaTime.getAppContext(), TimerActivity.class);
                        timerIntent.putExtra(TimerActivity.START_KEY, rawTime);
                        startActivity(timerIntent);
                    }
                });
            }
            else {
                // A time has not been entered, we should make the things disappear
                brewButtons[i].setVisibility(View.INVISIBLE);
                timeViews[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Establishing the view if the user has entered one or fewer temperatures into the record for the tea
     * @param singleTempToDisplay The singular temperature which the person wishes to display.
     */
    private void establishSingleTempField(int singleTempToDisplay) {
        TextView singletempView = brewingFragment.getSingleTempView();    // Getting the single temp view
        TextView[] tempViewsToDisable = {brewingFragment.getMinTempView(), brewingFragment.getMaxTempView()};
        boolean imperialTemperatures = SettingsHelper.isTemperatureFahrenheit();

        // Ensuring that the middle temperature view is set properly
        if (imperialTemperatures && singleTempToDisplay > Tea.EMPTY_ENTRY_FLAG) {
            // If imperial temperatures and the entry is not negative one
            singletempView.setText(String.format("%d F", singleTempToDisplay));
        }
        else if (singleTempToDisplay > -1) {
            // We can deduce that the user is an international scum with poor taste in measurements
            singletempView.setText(String.format("%d C", MiscHelper.fahrenheitToCentigrade(singleTempToDisplay)));
        }
        else {
            // The user has entered no real measurement... Display XXX
            singletempView.setText("XXX");  // TODO: Think of a better way to go about this....
        }

        // Make the other two views disappear
        tempViewsToDisable[0].setVisibility(View.INVISIBLE);
        tempViewsToDisable[1].setVisibility(View.INVISIBLE);
    }

    /**
     * A helper method taking the load of establishing the temperature fields if the user has entered
     * multiple temperatures
     */
    private void establishMultiTempFields() {
        //TODO: Implement this with default temperatures based on tea type if times are not specified by
        // the user
        TextView[] tempViews = {brewingFragment.getMinTempView(), brewingFragment.getMaxTempView()};
        int[] brewTemps = {teaBeingViewed.getBrewMin(), teaBeingViewed.getBrewMax()};
        boolean imperialTemperatures = SettingsHelper.isTemperatureFahrenheit();

        // Setting the single temp view so that it is just a dash
        brewingFragment.getSingleTempView().setText("-");   // No need to worry bout internationalisation here

        // Making visible and popilating the min and max temp entries
        for (int i = 0; i < tempViews.length; i++) {
            tempViews[i].setVisibility(View.VISIBLE);   // Setting to visible just to ensure they are seen
            if (imperialTemperatures) {
                tempViews[i].setText(String.format("%d F", brewTemps[i]));
            }
            else {
                tempViews[i].setText(String.format("%d C", MiscHelper.fahrenheitToCentigrade(brewTemps[i])));
            }
        }
    }
    /**
     * A helper method meant purely to increase coherency. Handles establishment of strength fields.
     */
    private void establishStrengthFields() {
        TextView strengthView = brewingFragment.getStrengthView();
        TextView strengthUnitView = brewingFragment.getStrengthUnitView();
        float idealStrength = teaBeingViewed.getIdealStrength();
        if (idealStrength > -1f && SettingsHelper.isStrengthImperial()) {
            // They prefer Imperial measurements
            strengthView.setText(String.format("%.2f", idealStrength));
        }
        else if (idealStrength > -1f) {
            // If they have supplied a strength but not a preference for imperial units
            float metricStrength = MiscHelper.ouncesToGrams(idealStrength);
            strengthView.setText(String.format("%.1f", metricStrength));
            strengthUnitView.setText(R.string.MetricStength);
        }
        else {
            // They haven't given a preference as to strength... so don't display nothing
            strengthView.setVisibility(View.INVISIBLE);
            strengthUnitView.setVisibility(View.INVISIBLE);
        }
    }

    public Tea getTeaBeingViewed() {
        return teaBeingViewed;
    }

    /**
     * Updates the teaBeingViewed field, if the two objects are not equivalent it will also ensure
     * that each of the UI fields gets updated.
     * @param newTeaBeingViewed New tea object which we are displaying.
     */
    public void setTeaBeingViewed(Tea newTeaBeingViewed) {

        if (newTeaBeingViewed.equals(teaBeingViewed)) {
            // If they are equivalent we need not update any UI elements, just a new reference to the
            // tea in question
            teaBeingViewed = newTeaBeingViewed;
        }
        else {
            // If any changes have been made then we must update the UI
            teaBeingViewed = newTeaBeingViewed;
            setPicture();
            buildBrewFragment();
        }
    }
}
