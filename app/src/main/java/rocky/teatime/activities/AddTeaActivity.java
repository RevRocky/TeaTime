package rocky.teatime.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import rocky.teatime.R;
import rocky.teatime.database.TeaStuff.Tea;
import rocky.teatime.exceptions.NotEnoughInfoException;
import rocky.teatime.fragments.tea_detail.TeaBasicsFragment;
import rocky.teatime.helpers.AlertHelper;
import rocky.teatime.helpers.ImageHelper;
import rocky.teatime.helpers.MiscHelper;
import rocky.teatime.helpers.SettingsHelper;

public class AddTeaActivity extends AppCompatActivity {

    private final int PHOTO_REQUEST = 1;     // A request code for a photo
    protected String currentPhotoPath;       // Where the image will be stored
    protected Tea teaInQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_new_add_tea);
        changeCaptions();
        teaInQuestion = new Tea();
    }

    /**
     * Checks if the user prefers metric temperatures. If they do, change the captions
     */
    protected void changeCaptions() {
        boolean imperialTemperature = SettingsHelper.isTemperatureFahrenheit();
        boolean imperialStrength = SettingsHelper.isStrengthImperial();

        // Changing the captions if the user does not prefer imperial temperatures
        if (!imperialTemperature) {
            ((TextView) findViewById(R.id.minTempCaption)).setText(R.string.min_Temp_Cent);
            ((TextView) findViewById(R.id.maxTempCaption)).setText(R.string.max_Temp_Cent);
        }
        if (!imperialStrength) {
            ((TextView) findViewById(R.id.strengthUnits)).setText(R.string.MetricIdeal);
        }
    }

    /**
     * This method is what happens when the user clicks the camera button
     * it allows them to take a picture of the tea steeped in the cup
     * @param someView ..
     */
    public void takeTeaPic(View someView) {

        // Deleting the old photo in order to save space.
        if (currentPhotoPath != null) {
            File imageFile = new File(currentPhotoPath);
            imageFile.delete();
            currentPhotoPath = null;
        }

        // Verifying that there is an app to receive this intent
        PackageManager packageManager = getPackageManager();

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Creating the intent to take a picture'

            List activities = packageManager.queryIntentActivities(picIntent,
                    PackageManager.MATCH_DEFAULT_ONLY);

            boolean isIntentSafe = activities.size() > 0;

            // Starting the activity if it is safe
            if (isIntentSafe) {
                File photoFile;

                // Ensuring the path was created successfully
                try {
                    photoFile = createImageFile();
                }
                catch (IOException e) {
                    return; // Return silently
                }

                // The file has been successfully created
                if (photoFile != null) {
                    Uri photoUri = FileProvider.getUriForFile(this, "rocky.teatime.fileprovider",
                            photoFile); // Getting a URI for the file
                    picIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);  // Adding an extra intent
                    startActivityForResult(picIntent, PHOTO_REQUEST);
                }
            }
        }
    }

    /**
     * A central clearing house for all activity results
     * @param requestCode A unique code correesponding to the request made
     * @param resultCode Code corresponding to whether or not the result was successful
     * @param data The actual payload from the activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Checking what we are responding to
        if (requestCode == PHOTO_REQUEST) {
            // Ensuring the operation went off smoothly
            if (resultCode == RESULT_OK) {
                ImageButton imgButton = (ImageButton) findViewById(R.id.imageIcon);


                // Getting the width and height of the largest place we'd place this image
                // NOTE: This is the screen in the viewTeaActivity
                Pair<Integer, Integer> biggestImgSize = new Pair<>(TeaBasicsFragment.getImageHeight(),
                        MiscHelper.getScreenWidth(this));

                Bitmap thumbnail = ImageHelper.saveImageAsSize(currentPhotoPath, biggestImgSize.first,
                        biggestImgSize.second);
                imgButton.setImageBitmap(thumbnail);
            }
        }
    }

    /**
     * Creates a file for an image of the tea.
     * @return Returns a file object in the applications local directory
     * @throws IOException IOException will be thrown if there is an issue in creating the file.
     */
    private File createImageFile() throws IOException {
        // Creating an unique file name.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = String.format("JPG_%s_", timeStamp);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Saving the file on the hard disk
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // TODO: update for any changes in GUI layout that make a substantial difference upon
    // the kind of user input there is.

    /**
     * Saves the users input to the database
     * @param thisView The current programme view
     */
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

        // We can just do everything in this method from here on out.
        teaInQuestion.setInStock(true);    // Chances are, if they're adding it, they have it right there.

        //Finally we extract the type of the tea from the spinner
        Spinner teaType = (Spinner) findViewById(R.id.teaTypeSelect);
        int typeNum = (int) teaType.getSelectedItemId();
        teaInQuestion.setType(typeNum);

        teaInQuestion.saveToDB(this);      // The reference to this is being passed in to be used as a context

        // Preparing to return to last activity
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    /**
     * Reads the entry for the name and sets the name of new tea to match that value. If no name is
     * entered then an alert will be flagged
     * @throws NotEnoughInfoException if the user does not supply a name for the tea.
     */
    protected void parseName() throws NotEnoughInfoException {
        EditText currentField = (EditText) findViewById(R.id.teaName);
        String entry = currentField.getText().toString().trim();
        boolean fieldStatus = checkField(entry);

        if (fieldStatus) {
            teaInQuestion.setName(entry);
        }
        else {
            AlertHelper.createOKAlert(getResources().getString(R.string.GeneralInfoError),
                    this);   // Displaying the alert.
            throw new NotEnoughInfoException("Please Supply a name for the tea"); // Throw an error.
        }
    }

    /**
     * Reads the time spinners. If a first steep time is not input into the programme an error will
     * pop up and an accompanying exception will be raised
     * @throws NotEnoughInfoException If the first time spinner is at a time equivalent equal to zero.
     */
    protected void readTimeSpinners() throws NotEnoughInfoException {
        // Reading and validating the brew time based upon the values in the spinners
        Spinner minSpin = (Spinner) findViewById(R.id.minuteSpinnerOne);
        Spinner secSpin = (Spinner) findViewById(R.id.secondSpnnerOne);
        teaInQuestion.setBrewTime(readTimeSpinners(minSpin, secSpin));

        // We only want to ensure the user has entered a proper first brew time
        if (teaInQuestion.getBrewTime() <= 0) {
            AlertHelper.createOKAlert(getResources().getString(R.string.SteepError),
                    this);
            throw new NotEnoughInfoException("Please add a first steep time");  // Throw that error
        }

        // Recycling the minSpin and secSpin objects
        minSpin = (Spinner) findViewById(R.id.minuteSpinner2);
        secSpin = (Spinner) findViewById(R.id.secondSpinner2);
        teaInQuestion.setBrewTimeSub(readTimeSpinners(minSpin, secSpin));
    }

    /**
     * Reads the temperatures supplied by the user and stores them as part of the tea.
     * If the user is entering temperatures in metric then it is converted into ferinheit for
     * consistent storage.
     */
    protected void readTemperatureEntries() {
        String entry;

        EditText[] tempFields = {(EditText) findViewById(R.id.minTempEntry),
                (EditText) findViewById(R.id.maxTempEntry)};
        boolean imperialTemperature = SettingsHelper.isTemperatureFahrenheit();
        int[] intermediateTempArray = new int[tempFields.length];   // Storing the temperatures in a temporary array.

        for (int i = 0; i < tempFields.length; i++) {
            entry = tempFields[i].getText().toString();
            // Checking the field for validity!
            boolean nonEmptyField = checkField(entry);

            if (nonEmptyField && imperialTemperature) {
                intermediateTempArray[i] = Integer.valueOf(entry);
            }
            else if (nonEmptyField) {   // We know the user prefers centigrade
                int convertedTemp = MiscHelper.centigradeToFahrenheit(Integer.valueOf(entry));
                intermediateTempArray[i] = convertedTemp;
            }
            else {
                intermediateTempArray[i] = Tea.EMPTY_ENTRY_FLAG;
            }
        }

        // This way we'll ensure the min and max are always adhered to.
        teaInQuestion.setBrewMin(Math.min(intermediateTempArray[0], intermediateTempArray[1])); // Setting the minimum temperature
        teaInQuestion.setBrewMax(Math.max(intermediateTempArray[0], intermediateTempArray[1])); // Setting the maximum temperature
    }

    /**
     * Gets the path of the current photo taken and sets the relevant parameter in the 'teaInQuestion'
     * object.
     */
    protected void readPictureLocation() {
        // Ensuring the location of the picture is saved
        if (currentPhotoPath == null ) {
            teaInQuestion.setPicLocation(Tea.NO_PICTURE_FLAG);
        }
        else {
            // We have a picture
            teaInQuestion.setPicLocation(currentPhotoPath);
        }
    }

    /**
     * Reads the user's preference for strength. If they prefer metric measurments itr is converted
     * into imperial units for storage.
     */
    protected void readStrengthField() {
        EditText currentField = (EditText) findViewById(R.id.brewStrength);
        String entry = currentField.getText().toString().trim();
        boolean nonEmptyField = checkField(entry);
        if (nonEmptyField && SettingsHelper.isStrengthImperial()) {
            teaInQuestion.setIdealStrength(Float.valueOf(entry));
        }
        else if (nonEmptyField) {
            float imperialStrength = MiscHelper.gramsToOunces(Float.valueOf(entry));
            teaInQuestion.setIdealStrength(imperialStrength);
        }
        else {
            teaInQuestion.setIdealStrength(-1f);   // If not set why save it?
        }
    }

    /**
     * Checks the field to make sure that it is not empty
     * @param fieldValue String value of something entered into a field
     * @return True if the field is valid (ie not empty), False otherwise
     */
    public boolean checkField(String fieldValue) {
        return !(fieldValue.isEmpty());
    }

    /**
     * Reads the two supplied spinners to parse out a time given in minutes and seconds.
     * @param minuteSpinner A spinner where the user entered the number of minutes.
     * @param secondSpinner A spinner where the user entered the number of seconds.
     * @return Returns the length of time on the spinners in seconds.
     */
    public int readTimeSpinners(Spinner minuteSpinner, Spinner secondSpinner) {
        int minutes = Integer.parseInt(minuteSpinner.getSelectedItem().toString());  // Read minutes
        int seconds = Integer.parseInt(secondSpinner.getSelectedItem().toString());
        seconds += (minutes * 60);      // Each minute is actually 60 seconds... right?
        return seconds;
    }
}
