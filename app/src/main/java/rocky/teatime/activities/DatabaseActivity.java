package rocky.teatime.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

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
    private DataSource dbHelper;

    private RecyclerView recyclerView;      // The overall recycler view object
    private DatabaseVisualiser visualiser;  // The specific visualier used to display things from the database
    private RecyclerView.LayoutManager layoutManager;

    /**
     * Establishes the basics so that a grid based recycler view can be used to view the different
     * types of teas.
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
        updateTeaList();

        // Registering our recycler view for a context menu
        registerForContextMenu(recyclerView);
    }

    /**
     * Fetches the Action Bar Menu
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * A central clearing house for all context menu options
     * @param item Menu item selected
     * @return True if operation is successful
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = -1;  // Initialising to an impossible value

        // Attempt to retrieve position
        try {
            position = visualiser.getPosition();
        }
        catch (Exception e) {
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
     * Launches the tea brew timer. If two times are present for the tea, it asks the user which
     * steeping the tea is.
     * @param teaToBrew Tea we wish to brew!
     */
    private void launchBrewScreen(Tea teaToBrew) {
        Intent timerIntent = new Intent(this, TimerActivity.class);

        // If the there is a second brew time... create alert for the user
        if (teaToBrew.getBrewTimeSub() > 0) {
            // TODO Create alert so user can pick. For now defaulting to first time
            // for testing purposes
            timerIntent.putExtra(TimerActivity.START_KEY, teaToBrew.getBrewTime());
        }
        else {
            timerIntent.putExtra(TimerActivity.START_KEY, teaToBrew.getBrewTime());
        }
        startActivity(timerIntent);     // Start the timer activity!
    }

    /**
     * Removes the specified tea from the database
     * @param teaToDelete
     */
    private void deleteTea(Tea teaToDelete) {
        // TODO: Ask user if the are really really sure
        dbHelper.deleteEntry(teaToDelete);
        updateTeaList();
    }

    /**
     * A central clearing house for handling all results of dispatched activities
     * @param requestCode A code unique to the activity dispatched
     * @param resultCode A code informing us of whether or not the result was successful
     * @param data A payload containing information from the dispatch activity
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case (NEW_TEA_REQUEST):
                updateTeaList();    // We don't actually care to
                return;
            case (EditTeaActivity.EDIT_TEA_REQUEST):
                updateTeaList();
                return;
            case (ViewTeaActivity.VIEW_TEA_REQUEST) :
                if (resultCode == ViewTeaActivity.DATABASE_CHANGE_FLAG) {
                    updateTeaList();    // Changes have happened so we want to update the display
                }
                return;
        }
    }

    /**
     * Updates the list view with the latest information from the database. Should the database not
     * exist, it will catch the Null Pointer Error and... do nothing!
     */
    private void updateTeaList() {
        // If the database does not exist a Null Pointer exception will be thrown. This is no good.
        try {
            dbHelper.open();
            ArrayList<Tea> teaList = dbHelper.getAllEntries();

            //TODO check to see if user wants list or grid mode
            // Here I'm making use of a custom database visualiser!
            visualiser = new GridVisualiser(teaList, this);
            recyclerView.setAdapter(visualiser);
            dbHelper.close();

        }
        catch (Exception | Error e) {
            e.printStackTrace();
        }
    }
}
