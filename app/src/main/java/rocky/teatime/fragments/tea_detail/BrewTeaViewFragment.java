package rocky.teatime.fragments.tea_detail;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import rocky.teatime.R;
import rocky.teatime.TeaTime;
import rocky.teatime.helpers.MiscHelper;

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
    private TextView minTempView;
    private TextView maxTempView;

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

        maxTempView = (TextView)theView.findViewById(R.id.max_temp_view);
        brewButtonFirst = (ImageButton)theView.findViewById(R.id.first_brew_button);
        brewButtonSecond = (ImageButton)theView.findViewById(R.id.second_brew_button);
        return theView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Sets the gui components up for a single temperature
     * @param fragView Reference to the fragment's view object
     */
    private void setupSingleTemperature(View fragView) {
        // TODO Implement
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
}
