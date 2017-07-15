package rocky.teatime.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import rocky.teatime.R;
import rocky.teatime.database.DataSource;
import rocky.teatime.database.TeaStuff.JsonTea;
import rocky.teatime.database.TeaStuff.Tea;
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

    private Tea teaBeingEdited;

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
        teaBeingEdited = Tea.readFromBundle(getIntent().getExtras());
        setContentView(R.layout.content_new_add_tea);
        changeCaptions();   // Check to see if we ought to change our captions.
        populateEntries();  // Using the tea object to populate the form!
    }

    /**
     * Populates the different GUI Fields based upon fields in the teaBeingEdited object.
     */
    private void populateEntries() {
        //TODO Once Photo locations are worked into the programme... add that stuff here
        EditText nameField;
        Spinner teaTypeSelect;

        // Going through and populating all the fields!
        nameField = (EditText) findViewById(R.id.teaName);
        nameField.setText(teaBeingEdited.getName());

        // Handling the spinners
        Pair<Spinner, Spinner> spinners = new Pair<> ((Spinner) findViewById(R.id.minuteSpinnerOne),
                (Spinner) findViewById(R.id.secondSinnerOne));
        Pair<Integer, Integer> minSec = MiscHelper.secondsToMinutes(teaBeingEdited.getBrewTime());
        spinners.first.setSelection(minSec.first);
        spinners.second.setSelection(minSec.second);

        // The second one may well be null so let's see if we even need to do anything
        if (teaBeingEdited.getBrewTimeSub() > 0) {
            // NOTE: 0 is default brew time on the spinners so it's all good!
            spinners = new Pair<> ((Spinner) findViewById(R.id.minuteSpinner2),
                    (Spinner) findViewById(R.id.secondSpinner2));
            minSec = MiscHelper.secondsToMinutes(teaBeingEdited.getBrewTimeSub());
            spinners.first.setSelection(minSec.first);
            spinners.second.setSelection(minSec.second);
        }

        // Since the following are non required fields we have to ensure that
        // they are actually populated
        EditText[] tempFields = {(EditText) findViewById(R.id.minTempEntry),
                (EditText) findViewById(R.id.maxTempEntry)};
        int[] temperatures = {teaBeingEdited.getBrewMin(), teaBeingEdited.getBrewMax()};

        // Just in case I actually forgot how for loops work!
        boolean imperialTemperatures = SettingsHelper.isTemperatureFahrenheit();
        for (int i = 0; i < tempFields.length; i++) {
            if (temperatures[i] > -1 && imperialTemperatures) {
                tempFields[i].setText(Integer.toString(temperatures[i]));
            }
            else if (temperatures[i] > -1) {
                // We know the user prefers centigrade
                int centigradeTemp = MiscHelper.fahrenheitToCentigrade(temperatures[i]);
                tempFields[i].setText(Integer.toString(centigradeTemp));
            }
        }

        // Now we handle the image!
        if (!teaBeingEdited.getPicLocation().equals("null")) {
            // If an image exists... open it.
            ImageButton ourButton = (ImageButton)findViewById(R.id.imageIcon);
            ourButton.setImageBitmap(BitmapFactory.decodeFile(teaBeingEdited.getPicLocation()));
        }

        // Lastly we handle the teaType selection
        teaTypeSelect = (Spinner) findViewById(R.id.teaTypeSelect);
        teaTypeSelect.setSelection(teaBeingEdited.getType().ordinal());
    }

    @Override
    /**
     * Saves the users input to the database
     * @param thisView The current programme view
     */
    public void saveDBInfo (View thisView) {
        EditText currentField;      // Current field we are reading
        Spinner teaType;            // Spinner where user selects their type of tea
        String entry;
        DataSource dbInterface;     // An interface to the SQL Database


        // No real good way to do this in a loop structure... just one at a time, unfortunately
        currentField = (EditText) findViewById(R.id.teaName);
        entry = currentField.getText().toString().trim();
        boolean fieldStatus = checkField(entry);

        if (fieldStatus) {
            teaBeingEdited.setName(entry);
        }
        else {
            AlertHelper.createOKAlert(getResources().getString(R.string.GeneralInfoError),
                    this);   // Displaying the alert.
            return;
        }

        // Reading and validating the brew time based upon the values in the spinners
        Spinner minSpin = (Spinner) findViewById(R.id.minuteSpinnerOne);
        Spinner secSpin = (Spinner) findViewById(R.id.secondSinnerOne);
        teaBeingEdited.setBrewTime(readTimeSpinners(minSpin, secSpin));

        // We only want to ensure the user has entered a proper first brew time
        if (teaBeingEdited.getBrewTime() <= 0) {
            AlertHelper.createOKAlert(getResources().getString(R.string.SteepError),
                    this);
            return;
        }

        // Recycling the minSpin and secSpin objects
        minSpin = (Spinner) findViewById(R.id.minuteSpinner2);
        secSpin = (Spinner) findViewById(R.id.secondSpinner2);
        teaBeingEdited.setBrewTimeSub(readTimeSpinners(minSpin, secSpin));

        // Not a required field so we can ignore it!
        currentField = (EditText) findViewById(R.id.minTempEntry);
        entry = currentField.getText().toString().trim();

        // Getting temperature preference
        boolean imperialTemperature = SettingsHelper.isTemperatureFahrenheit();

        // Checking the field for validity!
        boolean nonEmptyField = checkField(entry);

        if (nonEmptyField && imperialTemperature) {
            teaBeingEdited.setBrewMin(Integer.valueOf(entry));
        }
        else if (nonEmptyField) {   // We know the user prefers centigrade
            int convertedTemp = MiscHelper.centigradeToFahrenheit(Integer.valueOf(entry));
            teaBeingEdited.setBrewMin(convertedTemp);
        }
        else {
            teaBeingEdited.setBrewMin(-1);
        }

        // Not a required field so we can ignore it!
        currentField = (EditText) findViewById(R.id.maxTempEntry);
        entry = currentField.getText().toString().trim();
        nonEmptyField = checkField(entry);
        if (nonEmptyField && imperialTemperature) {
            teaBeingEdited.setBrewMax(Integer.valueOf(entry));
        }
        else if (nonEmptyField) {   // We know the user prefers centigrade
            int convertedTemp = MiscHelper.centigradeToFahrenheit(Integer.valueOf(entry));
            teaBeingEdited.setBrewMax(convertedTemp);
        }
        else {
            teaBeingEdited.setBrewMax(-1);
        }

        // Check to see if there's a picture. If so... save it.
        if (currentPhotoPath == null) {
            teaBeingEdited.setPicLocation(Tea.NO_PICTURE_FLAG);
        }
        else {
            teaBeingEdited.setPicLocation(currentPhotoPath);
        }

        //Finally we extract the type of the tea from the spinner
        teaType = (Spinner) findViewById(R.id.teaTypeSelect);
        int typeNum = (int) teaType.getSelectedItemId();
        teaBeingEdited.setType(typeNum);

        teaBeingEdited.updateDBEntry(this);  // The reference to this is being passed in to be used as a context

        // Preparing to return to last activity
        // TODO: Would it be worth checking for any changes.
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

}
