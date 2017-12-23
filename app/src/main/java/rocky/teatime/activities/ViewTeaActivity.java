package rocky.teatime.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import rocky.teatime.R;
import rocky.teatime.database.DataSource;
import rocky.teatime.database.TeaStuff.Tea;
import rocky.teatime.fragments.alerts.TeaDeletionConfirmationAlert;
import rocky.teatime.fragments.tea_detail.TeaBasicsFragment;
import rocky.teatime.helpers.AlertHelper;
import rocky.teatime.interfaces.AlertListenerInterface;

public class ViewTeaActivity extends AppCompatActivity implements AlertListenerInterface {

    public static final int VIEW_TEA_REQUEST = 859;     // You wanna see some magic?
    public static final int DATABASE_CHANGE_FLAG = 562; // Completely Magic Number
    public static final int NO_DB_CHANGE_FLAG = 563;    // I wanna make you feel magical

    private TeaBasicsFragment basicsFragment;   // A reference to the fragment tracking all of the basics.
    private boolean editedStatus;               // Tracks whether the tea has been edited.
    private Menu menu;                          // Reference to the menu

    private static final int inStockMenuIndex = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editedStatus = false;   // We have yet to edit the tea
        String jsonsedTea = getIntent().getExtras().getString(Tea.TEA_PAYLOAD_KEY);
        setContentView(R.layout.activity_view_tea);

        // Initalising the teaBasics fragment!
        basicsFragment = TeaBasicsFragment.newInstance(jsonsedTea);
        getSupportFragmentManager().beginTransaction().add(R.id.view_fragment_container,
                basicsFragment).commit();
        getSupportFragmentManager().executePendingTransactions();   // This ensures that the teaBeingViewed field of the basics fragment is instantiated

        // Establishing the particulars of the action bar
        constructActionBar();
        colourStatusBar();
    }

    /**
     * Fetches the Action Bar Menu
     * @param menu A menu class which we will inject the DBMenu
     * @return True if successful, false otherwise
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tea_view_menu, menu);
        this.menu = menu;                           // Getting that sweet reference so I can use it later!
        tickInStockBox();                           // Placing this here so it's run once the menu is inflated!
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Colours and titles the action bar
     */
    private void constructActionBar() {
        ActionBar actionBar = getSupportActionBar();
        Tea teaBeingViewed = basicsFragment.getTeaBeingViewed();

        // Setting the colour of the action bar
        actionBar.setBackgroundDrawable(new ColorDrawable(teaBeingViewed.getColour()));
        actionBar.setTitle(teaBeingViewed.toString());  // Titling bar after tea's name!
    }

    /**
     * If run on Lollipop and above, this will change the colour of the status bar. Otherwise,
     * it will do absolutely nothing!
     */
    private void colourStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();

            // clearing the Translucent Status flag
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // Adding the Draws System Bars Background to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // Changing the colour to match that of the tea's dark variant
            window.setStatusBarColor(basicsFragment.getTeaBeingViewed().getColourDark());
        }
    }

    /**
     * Sets the in stock box so that it matches the current status of the tea in question.
     */
    private void tickInStockBox() {
        MenuItem inStockItem = menu.getItem(inStockMenuIndex);
        inStockItem.setChecked(basicsFragment.getTeaBeingViewed().isInStock());
    }


    /**
     * A central clearing house for selcting all menu options
     * @param item The menu item selected
     * @return True if the operation is successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Tea teaBeingViewed = basicsFragment.getTeaBeingViewed();
        switch (item.getItemId()) {
            case R.id.edit_tea_option:
                EditTeaActivity.launchEditScreen(teaBeingViewed, this);
                editedStatus = true;
                return true;
            case R.id.delete_item_choice:
                deleteTea();
                return true;
            case R.id.in_stock_view_tea:
                flipTeaStock(teaBeingViewed);
                editedStatus = true;        // Setting the status so the database view can be updated
                // TODO: Explore greying out the image in this activity as well as the main one.
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * All we can do here is ask for confirmation. The real work is done in the on Positive and on Negative
     * click methods.
     */
    private void deleteTea() {
        DialogFragment deletionConfirmation = new TeaDeletionConfirmationAlert();
        deletionConfirmation.show(getFragmentManager(), "DELETE");
    }

    /**
     * Flips the boolean flag on the tea in question so that it the correct information concerning
     * whether or not the tea is in stock is in place. This also handles saving the updated tea to
     * the database.
     * @param teaBeingFlipped The tea we are setting as being in stock.
     */
    private void flipTeaStock(Tea teaBeingFlipped) {
        teaBeingFlipped.setInStock(!teaBeingFlipped.isInStock());
        teaBeingFlipped.updateDBEntry(this);
        tickInStockBox();                   // Tick the box. Update the UI!
    }

    /**
     * This method should only be called in order to remove a tea object from the database
     * @param fragment The Confirmation Alert Fragment
     */
    public void onDialoguePositiveClick(DialogFragment fragment) {
        Tea teaBeingDeleted = basicsFragment.getTeaBeingViewed();
        teaBeingDeleted.removeFromDB();

        // Preparing to return
        Intent returnIntent = new Intent();
        setResult(DATABASE_CHANGE_FLAG, returnIntent);  // Attaching the change flag
        finish();
    }

    /**
     * The user doesn't really want to do anything so we can simply return from the method
     * @param fragment The fragment which issued the request to delete the tea
     */
    public void onDialogueNegativeClick(DialogFragment fragment) {
        return;
    }

    /**

     * @param requestCode A code unique to the activity dispatched
     * @param resultCode A code informing us of whether or not the result was successful
     * @param data A payload containing information from the dispatch activity
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case (EditTeaActivity.EDIT_TEA_REQUEST):
                Tea newTeaBeingViewed = fetchEditedTeaObject();

                // If we actually get something from the database (we pretty much always should)
                if (newTeaBeingViewed != null) {
                    basicsFragment.setTeaBeingViewed(newTeaBeingViewed);
                    // I'll take the penalty of always executing these methods for the time being
                    constructActionBar();
                    colourStatusBar();
                }
        }
    }

    /**
     * Attempts to fetch the recently edited tea object from the database. If it is unable to fetch
     * the tea object, it will throw up an alert to the user and this activity will quit back to the
     * database activity
     * @return Copy of the tea object which has just been edited. Null if there's an issue in fetching
     * the tea from the database
     */
    private Tea fetchEditedTeaObject() {
        long teaID = basicsFragment.getTeaBeingViewed().getId();    // Edited tea will always preserve the ID
        try {
            DataSource dbHelper = new DataSource(this);             // Getting a helper to access our database!
            dbHelper.open();
            Tea teaFetched = dbHelper.getEntry(teaID);              // Returning the new tea object stored in the database
            dbHelper.close();
            return teaFetched;
        }
        catch (SQLiteException e) {
            Resources sysResources = Resources.getSystem();
            Log.e("DATABASE ERROR", e.getMessage());
            AlertHelper.createActivityKillAlert(sysResources.getString(R.string.DataBaseError),
                    this, DATABASE_CHANGE_FLAG);
            return null;    // Testing this out to see if this would be aight. We never... should reach this part
        }
    }

    /**
     * Ensures that the application passes back the correct intent when the
     * user simply hits the back button.
     */
    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();

        if (editedStatus) {
            setResult(DATABASE_CHANGE_FLAG, returnIntent);
        }
        else {
            setResult(NO_DB_CHANGE_FLAG, returnIntent);
        }
        finish();
    }

}
