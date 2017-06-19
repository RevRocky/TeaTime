package rocky.teatime.database.TeaStuff;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.gson.Gson;

import rocky.teatime.R;
import rocky.teatime.TeaTime;
import rocky.teatime.database.DataSource;

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

    public static String TEA_PAYLOAD_KEY = "Cargo";
    public static String NO_PICTURE_FLAG = "NULL";

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
        dbInterface.createEntry(name, type, brewTime, brewTimeSub, brewMin, brewMax, picLocation);
        dbInterface.close();                                        // Closing the database
    }

    /**
     * Updates the database entry corresponding to the current tea objext
     * @param programmeContext The current operating context of the programme
     */
    public void updateDBEntry(Context programmeContext) {
        DataSource dbInterface = new DataSource(programmeContext);  // Creating our interface
        dbInterface.open();                                         // Opening the database
        dbInterface.updateEntry(id, name, type, brewTime, brewTimeSub, brewMin, brewMax, picLocation);
        dbInterface.close();                                        // Closing the database
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

    /**
     * Returns the name of the tea
     * @return The string representation of the tea is its name.
     */
    public String toString() {
        return name;
    }
}
