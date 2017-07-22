package rocky.teatime.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import rocky.teatime.R;
import rocky.teatime.database.DataSource;
import rocky.teatime.database.TeaStuff.JsonTea;
import rocky.teatime.database.TeaStuff.Tea;
import rocky.teatime.database.visualise.DatabaseVisualiser;
import rocky.teatime.database.visualise.GridVisualiser;

public class DatabaseActivity extends AppCompatActivity {

    // TODO Implement it such that the usr can customise the span count
    private final int GRID_WIDTH = 3;

    public final int NEW_TEA_REQUEST = 40; // A code given to the new tea request

    // Some short integer codes which we can set to ensure the database is sorted how the user
    // wishes it.
    private static final byte SORT_BY_TYPE = 0;
    private static final byte SORT_BY_AZ = 1;
    private static final byte SORT_BY_ZA = 2;
    private static final byte SORT_BY_NEW = 3;
    private static final byte SORT_BY_OLD = 4;

    private static final boolean CHANGED_DB_SORT = true;

    private DataSource dbHelper;

    private RecyclerView recyclerView;      // The overall recycler view object
    private DatabaseVisualiser visualiser;  // The specific visualier used to display things from the database
    private RecyclerView.LayoutManager layoutManager;
    private byte sortType = SORT_BY_OLD;    // Default to order added to DB first.


    /**
     * Establishes the basics so that a grid based recycler view can be used to view the different
     * types of teas.
     *
     * @param savedInstanceState Saved state of the last time the actiity was ran
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        recyclerView = (RecyclerView) findViewById(R.id.tea_database_view);

        // Enforcing a single size on all items in the recycler view
        recyclerView.setHasFixedSize(true);

        // Establishing our layout manager
        layoutManager = new GridLayoutManager(this, GRID_WIDTH);
        recyclerView.setLayoutManager(layoutManager);

        // Opening our database helper
        dbHelper = new DataSource(this);
        updateTeaList(!CHANGED_DB_SORT);

        // Registering our recycler view for a context menu
        registerForContextMenu(recyclerView);
    }

    /**
     * Fetches the Action Bar Menu
     *
     * @param menu A menu class which we will inject the DBMenu
     * @return True if successful, false otherwise
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dbmunu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * A central clearing house for selcting all menu options
     *
     * @param item The menu item selected
     * @return True if the operation is successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addTeaChoice:
                launchAddScreen();
                return true;
            case R.id.settings:
                // Why have a method call here?
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.submitFeedback:
                launchEmailActivity();
                return true;
            case R.id.TypeSort:
                sortType = SORT_BY_TYPE;
                updateTeaList(CHANGED_DB_SORT);
                return true;
            case R.id.AtoZSort:
                sortType = SORT_BY_AZ;
                updateTeaList(CHANGED_DB_SORT);
                return true;
            case R.id.ZtoASort:
                sortType = SORT_BY_ZA;
                updateTeaList(CHANGED_DB_SORT);
                return true;
            case R.id.oldestFirst:
                sortType = SORT_BY_OLD;
                updateTeaList(CHANGED_DB_SORT);
                return true;
            case R.id.newestFirst:
                sortType = SORT_BY_OLD;
                updateTeaList(CHANGED_DB_SORT);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * A central clearing house for all context menu options
     *
     * @param item Menu item selected
     * @return True if operation is successful
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;  // Initialising to an impossible value

        // Attempt to retrieve position
        try {
            position = visualiser.getPosition();
        } catch (Exception e) {
            Log.d("ERROR WITH CONTEXT MENU", e.getLocalizedMessage(),
                    e);
            return super.onContextItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.editTeaOption:
                EditTeaActivity.launchEditScreen(visualiser.getTea(position), this);
                return true;
            case R.id.brewTeaOption:
                launchBrewScreen(visualiser.getTea(position));
                return true;
            case R.id.deleteTeaOption:
                deleteTea(visualiser.getTea(position));
            default:
                // Functionality not implemented yet. So make toast
                Toast.makeText(this, "Feature Not Implemented", Toast.LENGTH_SHORT).show();
                return true;
        }
    }

    /**
     * Launches the add tea screen!
     */
    public void launchAddScreen() {
        Intent addTeaIntent = new Intent(this, AddTeaActivity.class);
        startActivityForResult(addTeaIntent, NEW_TEA_REQUEST);
    }

    /**
     * Launches an email app so the user can email me should anything be amiss with this programme!
     */
    private void launchEmailActivity() {
        // Build the standard email header
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" +
                getString(R.string.devEmail)));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.defaultSubject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.defaultBody));

        startActivity(Intent.createChooser(emailIntent, getString(R.string.emailChooserTitle)));
    }

    /**
     * Launches the tea brew timer. If two times are present for the tea, it asks the user which
     * steeping the tea is.
     *
     * @param teaToBrew Tea we wish to brew!
     */
    private void launchBrewScreen(Tea teaToBrew) {
        Intent timerIntent = new Intent(this, TimerActivity.class);

        // If the there is a second brew time... create alert for the user
        if (teaToBrew.getBrewTimeSub() > 0) {
            // TODO Create alert so user can pick. For now defaulting to first time
            // for testing purposes
            timerIntent.putExtra(TimerActivity.START_KEY, teaToBrew.getBrewTime());
        } else {
            timerIntent.putExtra(TimerActivity.START_KEY, teaToBrew.getBrewTime());
        }
        startActivity(timerIntent);     // Start the timer activity!
    }

    /**
     * Removes the specified tea from the database
     *
     * @param teaToDelete Tea we wish to remove from the database
     */
    private void deleteTea(Tea teaToDelete) {
        teaToDelete.createTeaRemoveAlert(this);

        if (teaToDelete.getId() == Tea.TEA_REMOVED_ID_FLAG) {
            updateTeaList(!CHANGED_DB_SORT);    // We only need to update the list if we've deleted something
        }
    }

    /**
     * A central clearing house for handling all results of dispatched activities
     *
     * @param requestCode A code unique to the activity dispatched
     * @param resultCode  A code informing us of whether or not the result was successful
     * @param data        A payload containing information from the dispatch activity
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case (NEW_TEA_REQUEST):
                updateTeaList(!CHANGED_DB_SORT);    // We don't actually care to
                return;
            case (EditTeaActivity.EDIT_TEA_REQUEST):
                updateTeaList(!CHANGED_DB_SORT);
                return;
            case (ViewTeaActivity.VIEW_TEA_REQUEST):
                if (resultCode == ViewTeaActivity.DATABASE_CHANGE_FLAG) {
                    updateTeaList(!CHANGED_DB_SORT);    // Changes have happened so we want to update the display
                }
                return;
        }
    }

    /**
     * Updates the list view with the latest information from the database. Should the database not
     * exist, it will catch the Null Pointer Error and... do nothing!
     *
     * @param changeSort A simple flag informing the method as to whether we should spend the time
     *                   to sort the list returned from the data base.
     */
    private void updateTeaList(boolean changeSort) {
        // If the database does not exist a Null Pointer exception will be thrown. This is no good.
        try {
            dbHelper.open();
            ArrayList<Tea> teaList = dbHelper.getAllEntries();

            if (changeSort) {
                teaList = sortTeaList(teaList);
            }

            // Here I'm making use of a custom database visualiser!
            visualiser = new GridVisualiser(teaList, this);
            recyclerView.setAdapter(visualiser);
            dbHelper.close();

        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    /**
     * Sorts the supplied list of teas in a manner in accordance with the field "sortType"
     * @param teaList Unsorted list of teas
     * @return List of teas sorted by the parameter of the users choosing. This figure can be
     * ascertained by looking at the field "sortType"
     */
    private ArrayList<Tea> sortTeaList(final ArrayList<Tea> teaList) {
        switch (sortType) {
            case (SORT_BY_TYPE):
                Collections.sort(teaList, new Comparator<Tea> () {
                    @Override
                    public int compare(Tea teaOne, Tea teaTwo) {
                        return  ((Integer)teaOne.getType().ordinal()).compareTo(teaTwo.getType().ordinal());
                    }
                });
                return teaList;
            case (SORT_BY_AZ):
                Collections.sort(teaList, new Comparator<Tea>() {
                    @Override
                    public int compare(Tea teaOne, Tea teaTwo) {
                        return teaOne.getName().compareTo(teaTwo.getName());
                    }
                });
                return teaList;
            case (SORT_BY_ZA):
                Collections.sort(teaList, new Comparator<Tea>() {
                    @Override
                    public int compare(Tea teaOne, Tea teaTwo) {
                        return teaOne.getName().compareTo(teaTwo.getName());
                    }
                });
                Collections.reverse(teaList);
                return teaList;
            case (SORT_BY_NEW):
                // Idea is that the ID is sequential and thus will track what is most recently added
                // to the database
                Collections.sort(teaList, new Comparator<Tea>() {
                    @Override
                    public int compare(Tea teaOne, Tea teaTwo) {
                        return ((Long) teaOne.getId()).compareTo(teaTwo.getId());
                    }
                });
                return teaList;
            case (SORT_BY_OLD):
                // Again ID is a perfect indicator of when something was added
                Collections.sort(teaList, new Comparator<Tea>() {
                    @Override
                    public int compare(Tea teaOne, Tea teaTwo) {
                        return ((Long) teaTwo.getId()).compareTo(teaTwo.getId());
                    }
                });
                return teaList;
            default:
                return teaList;
        }
    }
}
