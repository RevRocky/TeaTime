package rocky.teatime.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import rocky.teatime.R;
import rocky.teatime.database.DataSource;
import rocky.teatime.database.TeaStuff.Tea;
import rocky.teatime.helpers.AlertHelper;

/**
 * The EditTeaActivity is a class which... handles the editing of a TeaDB entry. For the most part
 * it borrows heavily from it's parent class but with some alternative methods handling the population
 * of fields as well as the saving of an item to our database.
 * @author Rocky Petkov
 */
public class EditTeaActivity extends AddTeaActivity {

    private Tea teaBeingEdited;

    /**
     * Handles the basics of creating the activity
     * @param savedInstanceState A bundle with a "Json-ized" tea object
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        teaBeingEdited = Tea.readFromBundle(getIntent().getExtras());
        setContentView(R.layout.content_new_add_tea);
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
        Pair<Integer, Integer> minSec = secondsToMinutes(teaBeingEdited.getBrewTime());
        spinners.first.setSelection(minSec.first);
        spinners.second.setSelection(minSec.second);

        // The second one may well be null so let's see if we even need to do anything
        if (teaBeingEdited.getBrewTimeSub() > 0) {
            // NOTE: 0 is default brew time on the spinners so it's all good!
            spinners = new Pair<> ((Spinner) findViewById(R.id.minuteSpinner2),
                    (Spinner) findViewById(R.id.secondSpinner2));
            minSec = secondsToMinutes(teaBeingEdited.getBrewTimeSub());
            spinners.first.setSelection(minSec.first);
            spinners.second.setSelection(minSec.second);
        }

        // Since the following are non required fields we have to ensure that
        // they are actually populated
        EditText[] tempFields = {(EditText) findViewById(R.id.minTempEntry),
                (EditText) findViewById(R.id.maxTempEntry)};
        int[] temperatures = {teaBeingEdited.getBrewMin(), teaBeingEdited.getBrewMax()};

        // Just in case I actually forgot how for loops work!
        for (int i = 0; i < tempFields.length; i++) {
            if (temperatures[i] > -1) {
                tempFields[i].setText(Integer.toString(temperatures[i]));
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

    /**
     * Takes an integer amount of seconds and returns a tuple with an equivalent amount of time in
     * minutes/seconds.
     * @param seconds An integer containing an amount of seconds
     * @return A pair where the first value is an amount of minutes and the second value is the amount
     * of seconds left over
     */
    private Pair<Integer, Integer> secondsToMinutes(int seconds) {
        int minutes = seconds / 60;     // Sixty seconds in a minute
        seconds %= 60;                  // Remaining seconds is seconds modulo 60
        return new Pair<>(minutes, seconds);
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
        if (checkField(entry)) {
            teaBeingEdited.setBrewMin(Integer.valueOf(entry));
        }
        else {
            teaBeingEdited.setBrewMin(-1);
        }

        // Not a required field so we can ignore it!
        currentField = (EditText) findViewById(R.id.maxTempEntry);
        entry = currentField.getText().toString().trim();
        if (checkField(entry)) {
            teaBeingEdited.setBrewMax(Integer.valueOf(entry));
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
