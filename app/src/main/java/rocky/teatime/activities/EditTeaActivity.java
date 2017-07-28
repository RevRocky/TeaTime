package rocky.teatime.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.gson.Gson;

import rocky.teatime.R;
import rocky.teatime.database.TeaStuff.JsonTea;
import rocky.teatime.database.TeaStuff.Tea;
import rocky.teatime.exceptions.NotEnoughInfoException;
import rocky.teatime.helpers.AlertHelper;
import rocky.teatime.helpers.MiscHelper;
import rocky.teatime.helpers.SettingsHelper;

/**
 * The EditTeaActivity is a class which... handles the editing of a TeaDB entry. For the most part
 * it borrows heavily from it's parent class but with some alternative methods handling the population
 * of fields as well as the saving of an item to our database.
 * @author Rocky Petkov
 */
public class EditTeaActivity extends AddTeaActivity {

    public static final int EDIT_TEA_REQUEST = 50;


    /**
     * Launches the edit tea activity
     * @param teaToEdit The tea object we wish to edit
     */
    public static void launchEditScreen(Tea teaToEdit, Activity sourceActivity) {
        Intent editIntent = new Intent(sourceActivity, EditTeaActivity.class);
        editIntent.putExtra(Tea.TEA_PAYLOAD_KEY, new Gson().toJson(new JsonTea(teaToEdit), JsonTea.class));
        sourceActivity.startActivityForResult(editIntent, EDIT_TEA_REQUEST);
    }

    /**
     * Handles the basics of creating the activity
     * @param savedInstanceState A bundle with a "Json-ized" tea object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        teaInQuestion = Tea.readFromBundle(getIntent().getExtras());
        setContentView(R.layout.content_new_add_tea);
        changeCaptions();   // Check to see if we ought to change our captions.
        populateEntries();  // Using the tea object to populate the form!
    }

    /**
     * Populates the different GUI Fields based upon fields in the teaBeingEdited object.
     */
    private void populateEntries() {
        populateNameField();            // Populate the name field
        spinTimeSpinners();             // Populate the minute/second spinners
        populateTemperatureFields();    // Populates the fields storing the brewing temperatures
        populateStrengthField();        // Populates the strength field
        displayTeaImage();              // Display Thumbnail should there be one.
        spinTimeSpinners();             // Lastly we handle the type spinners!
    }

    /**
     * Populates the field showing the name of the twa
     */
    private void populateNameField() {
        // Going through and populating all the fields!
        EditText nameField = (EditText) findViewById(R.id.teaName);
        nameField.setText(teaInQuestion.getName());
    }

    /**
     * Sets the time spinners to match the value for the tea stored in the DB.
     */
    private void spinTimeSpinners() {
        // Handling the spinners
        Pair<Spinner, Spinner> spinners = new Pair<>((Spinner) findViewById(R.id.minuteSpinnerOne),
                (Spinner) findViewById(R.id.secondSinnerOne));
        Pair<Integer, Integer> minSec = MiscHelper.secondsToMinutes(teaInQuestion.getBrewTime());
        spinners.first.setSelection(minSec.first);
        spinners.second.setSelection(minSec.second);

        // The second one may well be null so let's see if we even need to do anything
        if (teaInQuestion.getBrewTimeSub() > 0) {
            // NOTE: 0 is default brew time on the spinners so it's all good!
            spinners = new Pair<>((Spinner) findViewById(R.id.minuteSpinner2),
                    (Spinner) findViewById(R.id.secondSpinner2));
            minSec = MiscHelper.secondsToMinutes(teaInQuestion.getBrewTimeSub());
            spinners.first.setSelection(minSec.first);
            spinners.second.setSelection(minSec.second);
        }
    }

    /**
     * Populates the fields to do with temperature. If the user prefers the metric system the
     * internal Imperial measurements are converted to metric.
     */
    private void populateTemperatureFields() {
        // Since the following are non required fields we have to ensure that
        // they are actually populated
        EditText[] tempFields = {(EditText) findViewById(R.id.minTempEntry),
                (EditText) findViewById(R.id.maxTempEntry)};
        int[] temperatures = {teaInQuestion.getBrewMin(), teaInQuestion.getBrewMax()};

        // Just in case I actually forgot how for loops work!
        boolean imperialTemperatures = SettingsHelper.isTemperatureFahrenheit();
        for (int i = 0; i < tempFields.length; i++) {
            if (temperatures[i] > -1 && imperialTemperatures) {
                tempFields[i].setText(Integer.toString(temperatures[i]));
            } else if (temperatures[i] > -1) {
                // We know the user prefers centigrade
                int centigradeTemp = MiscHelper.fahrenheitToCentigrade(temperatures[i]);
                tempFields[i].setText(Integer.toString(centigradeTemp));
            }
        }
    }

    /**
     * Populates the strength field with the users preferred robustness. This will be truncated to
     * two places of significance. Should the user prefer the metric system, the internal Imperial
     * figure will be converted to a metric equivalent.
     */
    private void populateStrengthField() {
        // Checking to see if the user has specified a strength, if so display it.
        boolean imperialStrength = SettingsHelper.isStrengthImperial();
        EditText strengthField = (EditText) findViewById(R.id.brewStrength);
        if (teaInQuestion.getIdealStrength() > -1f && imperialStrength) {
            strengthField.setText(String.format("%f", teaInQuestion.getIdealStrength()));
        }
        else if (teaInQuestion.getIdealStrength() > -1f) {
            // We know the user prefers metric units for strength
            float metricStrength = MiscHelper.ouncesToGrams(teaInQuestion.getIdealStrength());
            strengthField.setText(String.format("%.2f", metricStrength));
        }
    }

    /**
     * Displays the image (if there is one) associated with the teaInQuestion object.
     */
    private void displayTeaImage() {
        // Now we handle the image!
        if (!teaInQuestion.getPicLocation().equals("null")) {
            // If an image exists... open it.
            ImageButton ourButton = (ImageButton)findViewById(R.id.imageIcon);
            ourButton.setImageBitmap(BitmapFactory.decodeFile(teaInQuestion.getPicLocation()));
        }
    }

    /**
     * Spins the type spinner to match that of the stored tea, teaInQuestion.
     */
    private void spinTypeSpinners() {
        Spinner teaTypeSelect = (Spinner) findViewById(R.id.teaTypeSelect);
        teaTypeSelect.setSelection(teaInQuestion.getType().ordinal());
    }


    /**
     * Saves the users input to the database
     * @param thisView The current programme view
     */
    @Override
    public void saveDBInfo (View thisView) {
        try {
            parseName();                 // Read the name of the tea
            readTimeSpinners();          // Read the time spinners
        }
        catch (NotEnoughInfoException e) {
            Log.e("error", e.getMessage());
            return;                     // Return to the main event loop
        }

        readTemperatureEntries();    // Read the temperature entries
        readPictureLocation();       // Read the location of the picture
        readStrengthField();         // Read the user's preferred strength


        //Finally we extract the type of the tea from the spinner
        Spinner teaType = (Spinner) findViewById(R.id.teaTypeSelect);
        int typeNum = (int) teaType.getSelectedItemId();
        teaInQuestion.setType(typeNum);

        teaInQuestion.updateDBEntry(this);  // The reference to this is being passed in to be used as a context

        // Preparing to return to last activity
        // TODO: Would it be worth checking for any changes.
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

}
