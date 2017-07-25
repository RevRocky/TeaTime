package rocky.teatime.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * The Database Helper class is the class primarily responsible for managing and creating
 * the central database.
 * @version .5
 * @author Rocky Petkov
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // A bunch of Column headders!
    public static final String TABLE_TEAS = "Teas";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TEA = "Tea";
    public static final String COLUMN_TYPE = "Type";
    public static final String COLUMN_BREW_TIME = "BrewTime";
    public static final String COLUMN_BREW_SECOND = "SecondBrew";
    public static final String COLUMN_TEMP_MIN = "MinTemp";
    public static final String COLUMN_TEMP_MAX = "MaxTemp";
    public static final String COLUMN_PIC_LOCATION = "PictureLocation";
    public static final String COLUMN_TEA_STRENGTH = "TeaStrength";
    public static final String COLUMN_IN_STOCK = "InStock";


    // We have to store booleans as a short in the database. If 1 it is true. 0 for false
    public static final short BOOLEAN_TRUE = 1;

    // Some private information used internally when looking at the database
    // Constant only for a given iteration of the Database Helper?
    private static final String DATABASE_NAME = "tea.db";
    private static final int DATABASE_VERSION = 1;

    // A string specially for database creation
    private static String DATABASE_CREATE = String.format("create table %s (%s integer primary key " +
            "autoincrement, %s, %s, %s, %s, %s, %s, %s, %s, %s text not null);",
            TABLE_TEAS, COLUMN_ID, COLUMN_TEA, COLUMN_TYPE, COLUMN_BREW_TIME, COLUMN_BREW_SECOND,
            COLUMN_TEMP_MIN, COLUMN_TEMP_MAX, COLUMN_PIC_LOCATION, COLUMN_TEA_STRENGTH, COLUMN_IN_STOCK);

    /**
     * Simply calls the constructor of the parent class
     * @param context The context given by the programme when creating the DB helper
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Provides facility for for a database to be created!
     * @param database a SQLiteDatabase object which will be initialised
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);  // I won't pretend to understand what I'm doing
    }

    /**
     * Method which is called when the database needs to be updated!
     * @param database The database to be updated.
     * @param oldVersion The old version of the database.
     * @param newVersion The newer version of the database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(), String.format("Upgrading the database from ver. %d to" +
                "ver %d. This WILL destroy all old data", oldVersion, newVersion));  // Log changes
        database.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_TEAS));
        onCreate(database);         // Refresh and create a new DB!
    }

}
