package rocky.teatime.database.TeaStuff;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.google.gson.Gson;

import rocky.teatime.R;
import rocky.teatime.TeaTime;
import rocky.teatime.database.DataSource;
import rocky.teatime.database.DatabaseHelper;

/**
 * Holds information that a tea drinker might find of interest!
 * @author Rocky Petkov
 * TODO: Ensure we can go back and forth between Farenheit and Celsius
 */
public class Tea {

    private long id;                             // The id to quickly retrieve the entry in the database
    private String name;                         // Name of the tea
    private TeaType type;                        // What kind of tea is it?
    private int brewTime;                        // Brew Time (in seconds)
    private int brewTimeSub;                     // Brew Time for subsequent steepings! -1 If not set
    private int brewMin;                         // Min temp to brew the tea. -1 if not set
    private int brewMax;                         // Max temp to brew the tea. -1 If not set
    private String picLocation;                  // Location of the picture on disk.
    private float idealStrength;                 // Ounces per Cup water for ideal strength
    private boolean inStock;                     // A boolean which lets us know if a given tea is in stock

    public static long TEA_REMOVED_ID_FLAG = -505;   // A flag which we set if the tea has been removed from the DB

    public static String TEA_PAYLOAD_KEY = "Cargo";
    public static String NO_PICTURE_FLAG = "NULL";
    public static int EMPTY_ENTRY_FLAG = -1;

    /**
     * Creates a tea object from a given database entry
     * @param dbEntry A SQLite database entry.
     */
    public Tea(Cursor dbEntry) {
        setId(dbEntry.getLong(0));
        setName(dbEntry.getString(1));
        setType(dbEntry.getInt(2));
        setBrewTime(dbEntry.getInt(3));
        setBrewTimeSub(dbEntry.getInt(4));
        setBrewMin(dbEntry.getInt(5));
        setBrewMax(dbEntry.getInt(6));
        setPicLocation(dbEntry.getString(7));
        setIdealStrength(dbEntry.getFloat(8));

        if (dbEntry.getShort(9) == DatabaseHelper.BOOLEAN_TRUE) {
            setInStock(true);
        }
        else {  // If it is not explictly true we assume it is false!
            setInStock(false);
        }
    }

    /**
     * Initialises an empty tea object
     */
    public Tea() {
    }

    /**
     * Saves the current Tea object to the application's database
     * @param programmeContext The current operating context of the programme.
     */
    public void saveToDB(Context programmeContext) {
        DataSource dbInterface = new DataSource(programmeContext);  // Creating our interface
        dbInterface.open();                                         // Opening the database
        dbInterface.createEntry(name, type, brewTime, brewTimeSub, brewMin, brewMax, picLocation,
                idealStrength, inStock);
        dbInterface.close();                                        // Closing the database
    }

    /**
     * Updates the database entry corresponding to the current tea objext
     * @param programmeContext The current operating context of the programme
     */
    public void updateDBEntry(Context programmeContext) {
        DataSource dbInterface = new DataSource(programmeContext);  // Creating our interface
        dbInterface.open();                                         // Opening the database
        dbInterface.updateEntry(id, name, type, brewTime, brewTimeSub, brewMin, brewMax, picLocation,
                idealStrength, inStock);
        dbInterface.close();                                        // Closing the database
    }

    /**
     * Asks the user if they are sure to remove the tea from the data base. This method only handles
     * the display of the alert, the actual heavy lifting is done on the onPositiveButton() method
     * @param activityContext The application context of the current activity
     */
    public void createTeaRemoveAlert(Context activityContext) {
        // Due to the nature of this method (the choice of the button really quite mattering
        // I will forgo placing this alert in the AlertHelper Class
        Resources resources = activityContext.getResources();
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activityContext);
        alertBuilder.setMessage(resources.getString(R.string.DeletionAffirmation));
        alertBuilder.setCancelable(false);

        // Constructing the buttons
        alertBuilder.setPositiveButton(resources.getString(R.string.Yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();        // We just want to close our dialogue
                removeDBEntry();
            }
        });

        // Constructing the buttons
        alertBuilder.setNegativeButton(resources.getString(R.string.No), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();        // We just want to close our dialogue

            }
        });

        AlertDialog newAlert = alertBuilder.create();
        newAlert.show();
    }

    /**
     * Removes the database entry corresponding to this Tea object
     */
    private void removeDBEntry() {
        Context appContext = TeaTime.getAppContext();
        DataSource dbInterface = new DataSource(appContext);
        dbInterface.open();
        dbInterface.deleteEntry(this);
        dbInterface.close();    // TODO: I don't think we should have an issue of this not working.
    }



    /**
     * Reads a tea object from a json string that has been serialised using Gson.
     * @param someBundle The bundle of extras containing the tea object
     * @return Returns a fully instantiated tea object corresponding to the json object. If the bundle
     * does not have a JSON tea object attached then it will simply return a non initialised tea object.
     */
    public static Tea readFromBundle(Bundle someBundle){
        String jsonTea; // A Json representation of a tea object
        Tea newTea;

        if (someBundle != null) {
            jsonTea = someBundle.getString(TEA_PAYLOAD_KEY);
            newTea = new Gson().fromJson(jsonTea, JsonTea.class).makeTea();
        }
        else {
            newTea = new Tea(); // If the bundle isn't attached simply return a null tea object
        }
        return newTea;
    }

    // From here on out be Getters and Setters!
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // TODO: Flagged for deletion
    public TeaType getType() {
        return type;
    }
    Resources stringResources = Resources.getSystem();  // Allowing us to get our string values
    public String getTypeName() {
        switch (type) {
            case BLACK:
                return stringResources.getString(R.string.Black);
            case GREEN:
                return stringResources.getString(R.string.Green);
            case WHITE:
                return stringResources.getString(R.string.White);
            case YELLOW:
                return stringResources.getString(R.string.Yellow);
            case OOLONG:
                return stringResources.getString(R.string.Oolong);
            case PUERH:
                return stringResources.getString(R.string.Puerh);
            case HERBAL:
                return stringResources.getString(R.string.Herbal);
            default:
                return "NULL";  // Null if we can not find a type.
        }
    }

    /**
     * Returns the colour resource correspinding with the type of tea it is.
     * @return The id of the colour resource of the tea in question
     */
    public int getColour() {
        // Getting the current application context as it is needed to call most up to date colour retrieval
        // methods
        Context appContext = TeaTime.getAppContext();

        switch (type) {
            case BLACK:
                return ContextCompat.getColor(appContext, R.color.blackTea);
            case GREEN:
                return ContextCompat.getColor(appContext, R.color.greenTea);
            case WHITE:
                return ContextCompat.getColor(appContext, R.color.whiteTea);
            case YELLOW:
                return ContextCompat.getColor(appContext, R.color.yellowTea);
            case OOLONG:
                return ContextCompat.getColor(appContext, R.color.oolongTea);
            case PUERH:
                return ContextCompat.getColor(appContext, R.color.puerhTea);
            case HERBAL:
                return ContextCompat.getColor(appContext, R.color.herbalTea);
            default:
                // Should never be called but if need be we'll default to black tea
                return ContextCompat.getColor(appContext, R.color.blackTea);
        }
    }

    /**
     * Returns a darker variant of the colour corresponding with the type of tea this is. This
     * darker colour is mostly used to colour the status bar.
     * @return Id of the darker variant of the colour associated with the tea in quwstion
     */
    public int getColourDark() {
        // Getting the current application context as it is needed to call most up to date colour retrieval
        // methods
        Context appContext = TeaTime.getAppContext();

        switch (type) {
            case BLACK:
                return ContextCompat.getColor(appContext, R.color.blackTeaDark);
            case GREEN:
                return ContextCompat.getColor(appContext, R.color.greenTeaDark);
            case WHITE:
                return ContextCompat.getColor(appContext, R.color.whiteTeaDark);
            case YELLOW:
                return ContextCompat.getColor(appContext, R.color.yellowTeaDark);
            case OOLONG:
                return ContextCompat.getColor(appContext, R.color.oolongTeaDark);
            case PUERH:
                return ContextCompat.getColor(appContext, R.color.puerhTeaDark);
            case HERBAL:
                return ContextCompat.getColor(appContext, R.color.herbalTeaDark);
            default:
                // Should never be called but if need be we'll default to black tea
                return ContextCompat.getColor(appContext, R.color.blackTeaDark);
        }
    }

    /**
     * Checks for equality between two tea objects
     * @param otherObject Object with which we wish to compare our tea object
     * @return True if the two teas have the same name, type, brewing parameters and picture,
     * false otherwise.
     */
    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) return true;
        if (otherObject == null || getClass() != otherObject.getClass()) return false;

        Tea tea = (Tea) otherObject;

        if (getBrewTime() != tea.getBrewTime()) return false;
        if (getBrewTimeSub() != tea.getBrewTimeSub()) return false;
        if (getBrewMin() != tea.getBrewMin()) return false;
        if (getBrewMax() != tea.getBrewMax()) return false;
        if (!getName().equals(tea.getName())) return false;
        if (getType() != tea.getType()) return false;
        return getPicLocation() != null ? getPicLocation().equals(tea.getPicLocation()) : tea.getPicLocation() == null;

    }

    /**
     * Allows for basic hashing of a tea object.
     * @return A hash code generated based upon the attributes of the tea
     */
    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getType().hashCode();
        result = 31 * result + getBrewTime();
        result = 31 * result + getBrewTimeSub();
        result = 31 * result + getBrewMin();
        result = 31 * result + getBrewMax();
        result = 31 * result + (getPicLocation() != null ? getPicLocation().hashCode() : 0);
        return result;
    }

    /**
     * Sets the tea type based on the supplied integer
     * @param typeVal An int which will correspond with a given value of the TeaType
     *                enum.
     */
    public void setType(int typeVal) {
        type = TeaType.fromInt(typeVal);
    }

    public void setType(TeaType type) {
        this.type = type;
    }

    public int getBrewTime() {
        return brewTime;
    }

    public void setBrewTime(int brewTime) {
        this.brewTime = brewTime;
    }

    public int getBrewTimeSub() {
        return brewTimeSub;
    }

    public void setBrewTimeSub(int brewTimeSub) {
        this.brewTimeSub = brewTimeSub;
    }

    public int getBrewMin() {
        return brewMin;
    }

    public void setBrewMin(int brewMin) {
        this.brewMin = brewMin;
    }

    public int getBrewMax() {
        return brewMax;
    }

    public void setBrewMax(int brewMax) {
        this.brewMax = brewMax;
    }

    public String getPicLocation() {
        return picLocation;
    }

    public void setPicLocation(String picLocation) {
        this.picLocation = picLocation;
    }

    public float getIdealStrength() {
        return idealStrength;
    }

    public void setIdealStrength(float idealStrength) {
        this.idealStrength = idealStrength;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    /**
     * Flags the tea object that it has been removed from the database.
     */
    public void flagAsRemoved() {
        id = Tea.TEA_REMOVED_ID_FLAG;
    }

    /**
     * Returns the name of the tea
     * @return The string representation of the tea is its name.
     */
    public String toString() {
        return name;
    }
}
