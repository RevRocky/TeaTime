package rocky.teatime.database.visualise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import rocky.teatime.R;
import rocky.teatime.activities.DatabaseActivity;
import rocky.teatime.activities.EditTeaActivity;
import rocky.teatime.activities.TimerActivity;
import rocky.teatime.database.TeaStuff.Tea;

/**
 * Created by Rocky on 25-May-17.
 */

public class DatabaseVisualiser extends BaseAdapter implements ListAdapter {
    private ArrayList<Tea> teaList;     // An array to hold our tea objects
    private Context context;            // Current application context

    private final int MAX_CHAR_VERT = 16;   // Maximum amount of characters to display when in vertical allignment

    /**
     * A straightforward constructor supplying a list of teas and some application context
     * @param list An ArrayList with the entries in the database.
     * @param context Current application context
     */
    public DatabaseVisualiser(ArrayList<Tea> list, Context context) {
        teaList = list;
        this.context = context;
    }

    /**
     * Returns the size of the database
     * @return Size of the database
     */
    @Override
    public int getCount() {
        return teaList.size();
    }

    /**
     * Returns the database entry corresponding with the position supplied
     * @param pos Position of the item to retrieve
     * @return Returns an Object-Type pointer to the requested Tea Object.
     * Must be cast to a Tea type object
     */
    @Override
    public Object getItem(int pos) {
        return teaList.get(pos);
    }

    /**
     * An alternate version of the getItem method where we instead return
     * a Tea-type pointer to the requested object
     * @param pos Position of the item to retrieve
     * @return Returns reference to the requested tea object
     */
    public Tea getTea(int pos) {
        return (Tea) teaList.get(pos);
    }

    /**
     * Returns the Database id corresponding with the requested object
     * @param pos Position of the item in question
     * @return Database id corresponding with the requested object
     */
    @Override
    public long getItemId(int pos) {
        return teaList.get(pos).getId();
    }

    /**
     * Establishes a List-View entry for each of the teas in the database
     * @param position Position in the database of the tea being added
     * @param convertView The view which we will transform into the list entry
     * @param parent The parent view of the database listings!
     * @return Returns the view all nice, set up and customised for the tea in question!
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Tea currentTea = teaList.get(position);

        // If the convertView is null we have to initialise it!
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_layout_tea_db_view, null);
        }

        //Handling the TextView to display the tea name!
        TextView listItemText = (TextView)convertView.findViewById(R.id.teaNameList);
        listItemText.setText(snipText(currentTea.toString()));   // Show snipped version!

        // TODO: Implement the code to change icon colour, handle brewing and editing
        // And now we handle the attachment of the brewing method to the brew button. I've opted
        // for doing a Lambda function so that I can easily get reference to the tea in question
        Button brewButton = (Button)convertView.findViewById(R.id.brewButton);
        brewButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent timerIntent = new Intent(context, TimerActivity.class);

                // If the there is a second brew time... create alert for the user
                if (currentTea.getBrewTimeSub() > 0) {
                    // TODO Create alert so user can pick. For now defaulting to first time
                    // for testing purposes
                    timerIntent.putExtra("BrewTime", currentTea.getBrewTime());
                }
                else {
                    timerIntent.putExtra("BrewTime", currentTea.getBrewTime());
                }
                context.startActivity(timerIntent);     // Start the timer activity!
            }
        });

        // Setting the onclick method for the edit button now!
        ImageButton editButton = (ImageButton) convertView.findViewById(R.id.editTeaButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent editIntent = new Intent(context, EditTeaActivity.class);
                editIntent.putExtra(Tea.TEA_PAYLOAD_KEY, new Gson().toJson(currentTea, Tea.class));
                ((Activity)context).startActivityForResult(editIntent, DatabaseActivity.EDIT_TEA_REQUEST);
            }
        });
        // database entries
        return convertView;
    }

    /**
     * If the text is too long we snip the text so it fits in the area alotted to it!
     * @param teaToDisplay The name of the tea that we want to display
     * @return The original string if 16 characters or fewer. Otherwise a truncated version of the
     * string.
     */
    private String snipText(String teaToDisplay) {
        int screenMode = context.getResources().getConfiguration().orientation;
        int stringLength = teaToDisplay.length();

        // If the screen is in portrait mode and the string is too long, snip it!
        if (screenMode == Configuration.ORIENTATION_PORTRAIT && stringLength > MAX_CHAR_VERT) {
            return String.format("%s%s", teaToDisplay.substring(0, 15), "...");
        }
        // implicit else
        return teaToDisplay;
    }
}
