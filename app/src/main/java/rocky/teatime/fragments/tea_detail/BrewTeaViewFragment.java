package rocky.teatime.fragments.tea_detail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import rocky.teatime.R;

/**
 * This fragment is responsible for displaying the times it takes to brew the tea in question in a
 * way that is pleasing to the user.
 */
public class BrewTeaViewFragment extends Fragment {


    // Just copies of the related amounts of the tea being displayed
    private ImageButton brewButtonFirst;
    private ImageButton brewButtonSecond;
    private TextView minTimeView;
    private TextView maxTimeView;
    private TextView minTempView;       // Invisible if only one temperature is entered
    private TextView singleTempView;    // Holds the dash unless only one temperature is entered
    private TextView maxTempView;       // Invisible if only one temperature is entered
    private TextView strengthView;
    private TextView strengthUnitView;

    public BrewTeaViewFragment() {
        // Required empty public constructor
    }

    /**
     * In addition to the basic requirements, this will acquire the references to all the necessary
     * text views.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View theView = inflater.inflate(R.layout.fragment_brew_tea_view, container, false);
        minTimeView = (TextView)theView.findViewById(R.id.first_brew_time);
        maxTimeView = (TextView)theView.findViewById(R.id.second_brew_time);
        minTempView = (TextView)theView.findViewById(R.id.min_temp_view);
        singleTempView = (TextView)theView.findViewById(R.id.single_temp_view);
        maxTempView = (TextView)theView.findViewById(R.id.max_temp_view);
        brewButtonFirst = (ImageButton)theView.findViewById(R.id.first_brew_button);
        brewButtonSecond = (ImageButton)theView.findViewById(R.id.second_brew_button);
        strengthView = (TextView)theView.findViewById(R.id.strengthView);
        strengthUnitView = (TextView)theView.findViewById(R.id.strengthUnitView);
        return theView;
    }

    public TextView getMinTimeView() {
        return minTimeView;
    }

    public TextView getMaxTimeView() {
        return maxTimeView;
    }

    public TextView getMinTempView() {
        return minTempView;
    }

    public TextView getMaxTempView() {
        return maxTempView;
    }

    public ImageButton getBrewButtonFirst() {
        return brewButtonFirst;
    }

    public ImageButton getBrewButtonSecond() {
        return brewButtonSecond;
    }

    public TextView getStrengthView() {
        return strengthView;
    }

    public TextView getStrengthUnitView() {
        return strengthUnitView;
    }

    public TextView getSingleTempView() {
        return singleTempView;
    }
}

