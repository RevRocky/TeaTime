package rocky.teatime.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import java.util.ArrayList;

import rocky.teatime.database.TeaStuff.Tea;
import rocky.teatime.database.TeaStuff.TeaType;

/**
 * Acts as an intermediary between the user/application and the
 * underlying database. I acknolge that this does come at a
 * small performance penalty but for a small database, this
 * should not be too much of an issue.
 * @author Rocky Petkov
 */
public class DataSource {

    // Some database fields
    private SQLiteDatabase database;    // Java representation of our database
    private DatabaseHelper dbHelper;    // DB helper object
    private String[] allColumns = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_TEA,
            DatabaseHelper.COLUMN_TYPE, DatabaseHelper.COLUMN_BREW_TIME, DatabaseHelper.COLUMN_BREW_SECOND,
            DatabaseHelper.COLUMN_TEMP_MIN, DatabaseHelper.COLUMN_TEMP_MAX};

    /**
     * A simple constructor that constructs this object as well as a database helper object
     * @param context The current programme context. Used to construct DB helper object
     */
    public DataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /**
     * Opens a writeable version of the Database.
     * @throws SQLException if there is an issue in obtaining a writable database
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();  // Error will be thrown by this method
    }

    /**
     * Responsibly closes the database
     */
    public void close() {
        dbHelper.close();
    }


    //TODO: Check to see if returning the new instance of the tea object is even worth it...
    /**
     * Adds a new item to the database?
     * @param teaName The name of tea being added to the DB
     * @param teaType The type of tea (an enum, thus an int)
     * @param brewTime Brewtime of tea being added
     * @param brewSecond Brew time on second steeping
     * @param minTemp an integer
     * @param maxTemp an Integer, nothing surprising
     * @return A tea object corresponding with the new object
     */
    public Tea createEntry(String teaName, TeaType teaType, int brewTime, int brewSecond,
                           int minTemp, int maxTemp) {
        ContentValues values = new ContentValues();

        // Sigh no real better way to do this so I'll do it here
        // Adding a bunch of things to our column values
        values.put(allColumns[1], teaName);
        values.put(allColumns[2], teaType.ordinal());
        values.put(allColumns[3], brewTime);
        values.put(allColumns[4], brewSecond);
        values.put(allColumns[5], minTemp);
        values.put(allColumns[6], maxTemp);

        long insertId = database.insert(DatabaseHelper.TABLE_TEAS, null, values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_TEAS, allColumns,
                String.format("%s = %d", allColumns[0], insertId), null, null, null, null);
        cursor.moveToFirst();   // Move our cursor
        Tea newTea = readEntry(cursor);     // Creating a Tea object from our entry
        cursor.close();                     // ALWAYS close the pointer.
        return newTea;
    }

    /**
     *
     * @param id Id pertaining to the entry being edited
     * @param teaName The name of tea being modified to the DB
     * @param teaType The type of tea (an enum, thus an int)
     * @param brewTime Brewtime of tea being added
     * @param brewSecond Brew time on second steeping
     * @param minTemp an integer
     * @param maxTemp an Integer, nothing surprising
     */
    public void updateEntry(long id, String teaName, TeaType teaType, int brewTime, int brewSecond,
                           int minTemp, int maxTemp) {
        ContentValues values = new ContentValues();

        // Again there's no real better way to deal with this other than 1-by-1
        values.put(allColumns[1], teaName);
        values.put(allColumns[2], teaType.ordinal());
        values.put(allColumns[3], brewTime);
        values.put(allColumns[4], brewSecond);
        values.put(allColumns[5], minTemp);
        values.put(allColumns[6], maxTemp);

        database.update(DatabaseHelper.TABLE_TEAS, values, String.format("_id=%d", id), null);
        /*Cursor cursor = database.query(DatabaseHelper.TABLE_TEAS, allColumns,
                String.format("%s = %d", allColumns[0], id), null, null, null, null);
        cursor.moveToFirst();   // Move our cursor
        Tea newTea = readEntry(cursor);     // Creating a Tea object from our entry
        cursor.close();                     // ALWAYS close the pointer.
        return newTea; */
    }

    /**
     * Reads a database entry into a Tea object
     * @param cursor Cursor pointing to a given database entry
     * @return A Tea object corresponding to the database entry!
     */
    private Tea readEntry(Cursor cursor) {
        return new Tea(cursor);    // Initialise a null Tea object
    }

    /**
     * Retrieves all entries from the database as Tea objects.
     * @return An ArrayList containing all entries in the database.
     */
    public ArrayList<Tea> getAllEntries() {
        // I can't imagine databases being larger than 25 entries
        ArrayList<Tea> dbEntries = new ArrayList<>(25);

        Cursor cursor = database.query(DatabaseHelper.TABLE_TEAS, allColumns, null, null, null,
                null, null);

        cursor.moveToFirst();   // Move to the first result

        // Terminates on all finite DB
        while (!cursor.isAfterLast()) {
            dbEntries.add(readEntry(cursor));
            cursor.moveToNext();    // Iterate the cursor to the nexxt entry
        }
        cursor.close(); // Closing the cursor for safety
        return dbEntries;
    }

    /**
     * Deletes the database entry corresponding to the supplied tea object
     * @param tea A tea object which dictates which Database entry will be deleted.
     */
    public void deleteEntry(Tea tea) {
        long id = tea.getId();
        System.out.println(String.format("Entry with id %d has been deleted!", id));
        database.delete(DatabaseHelper.TABLE_TEAS, String.format("%s = %d", allColumns[0], id),
                null);
    }
}
