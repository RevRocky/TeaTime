package rocky.teatime.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import rocky.teatime.R;
import rocky.teatime.database.DataSource;
import rocky.teatime.database.TeaStuff.Tea;
import rocky.teatime.helpers.AlertHelper;

import static android.graphics.BitmapFactory.decodeFile;

public class AddTeaActivity extends AppCompatActivity {

    private final int PHOTO_REQUEST = 1;     // A request code for a photo
    private String currentPhotoPath;         // Where the image will be stored

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_new_add_tea);
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
            boolean deleted = imageFile.delete();    // Delete the old file
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
                File photoFile = null;

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
                Bitmap thumbnail = prepareThumbnailFromFile(imgButton.getWidth(),
                        imgButton.getHeight());
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

    /**
     * Prepares an appropriately sized bitmap thumbnail of the image which has just been
     * taken
     * @param width Width of the image button
     * @param height Height of the image button
     * @return The image resized to fit within the button
     */
    private Bitmap prepareThumbnailFromFile(int width, int height) {
        // Getting the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        decodeFile(currentPhotoPath, bmOptions);
        int imgW = bmOptions.outWidth;
        int imgH = bmOptions.outHeight;

        // Determine how much to scale the image by
        int scaleFactor = Math.min(imgW/width, imgH/height);

        // Decoding the image file to a bitmap sized to fill our view
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap smallImg =  BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        saveImage(smallImg);        // Save the thumbnail version over the original
        return smallImg;
    }

    /**
     * Saves a bitmap image to disk at currentPhotoPath as a JPEG image!
     * @param image The image to be saved to disk.
     */
    private void saveImage(Bitmap image) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(currentPhotoPath);
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);   // Actually saving the file
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (out!= null) {
                    out.close();    // Close the stream
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // TODO: update for any changes in GUI layout that make a substantial difference upon
    // the kind of user input there is.

    /**
     * Saves the users input to the database
     * @param thisView The current programme view
     */
    public void saveDBInfo (View thisView) {
        EditText currentField;      // Current field we are reading
        Spinner teaType;            // Spinner where user selects their type of tea
        String entry;
        DataSource dbInterface;     // An interface to the SQL Database

        Tea newTea = new Tea();     // Initialising an empty tea object

        // No real good way to do this in a loop structure... just one at a time, unfortunately
        currentField = (EditText) findViewById(R.id.teaName);
        entry = currentField.getText().toString().trim();
        boolean fieldStatus = checkField(entry);

        if (fieldStatus) {
            newTea.setName(entry);
        }
        else {
            AlertHelper.createOKAlert(getResources().getString(R.string.GeneralInfoError),
                    this);   // Displaying the alert.
            return;
        }

        // Reading and validating the brew time based upon the values in the spinners
        Spinner minSpin = (Spinner) findViewById(R.id.minuteSpinnerOne);
        Spinner secSpin = (Spinner) findViewById(R.id.secondSinnerOne);
        newTea.setBrewTime(readTimeSpinners(minSpin, secSpin));

        // We only want to ensure the user has entered a proper first brew time
        if (newTea.getBrewTime() <= 0) {
            AlertHelper.createOKAlert(getResources().getString(R.string.SteepError),
                    this);
            return;
        }

        // Recycling the minSpin and secSpin objects
        minSpin = (Spinner) findViewById(R.id.minuteSpinner2);
        secSpin = (Spinner) findViewById(R.id.secondSpinner2);
        newTea.setBrewTimeSub(readTimeSpinners(minSpin, secSpin));

        // Not a required field so we can ignore it!
        currentField = (EditText) findViewById(R.id.minTempEntry);
        entry = currentField.getText().toString().trim();
        if (checkField(entry)) {
            newTea.setBrewMin(Integer.valueOf(entry));
        }
        else {
            newTea.setBrewMin(-1);
        }

        // Not a required field so we can ignore it!
        currentField = (EditText) findViewById(R.id.maxTempEntry);
        entry = currentField.getText().toString().trim();
        if (checkField(entry)) {
            newTea.setBrewMax(Integer.valueOf(entry));
        }
        else {
            newTea.setBrewMax(-1);
        }

        //Finally we extract the type of the tea from the spinner
        teaType = (Spinner) findViewById(R.id.teaTypeSelect);
        int typeNum = (int) teaType.getSelectedItemId();
        newTea.setType(typeNum);

        newTea.saveToDB(this);      // The reference to this is being passed in to be used as a context

        // Preparing to return to last activity
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
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
