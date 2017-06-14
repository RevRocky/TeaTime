package rocky.teatime.database.visualise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
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
import rocky.teatime.widgets.ItemHolder;

/**
 * Created by Rocky on 25-May-17.
 */

public abstract class DatabaseVisualiser extends RecyclerView.Adapter<ItemHolder> {
    protected ArrayList<Tea> teaList;     // An array to hold our tea objects
    protected Context context;            // Current application context
    private int position;                 // Reference to a given item's position in the database

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
     * Explicitly defined so that we don't have any nasty reference issues
     * @param holder The particular item holder we are removing the onClickListener
     *               from.
     */
    @Override
    public void onViewRecycled(ItemHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    /**
     * Returns the size of the database
     * @return Size of the database
     */
    public int getItemCount() {
        return teaList.size();
    }

    /**
     * Returns the database entry corresponding with the position supplied
     * @param pos Position of the item to retrieve
     * @return Returns an Object-Type pointer to the requested Tea Object.
     * Must be cast to a Tea type object
     */
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
     * Returns the current position of the DB visualiser object
     * @return Current position of a visualiser object
     */
    public int getPosition(){
        return position;
    }

    /**
     * Enables us to set the current position of an item in the db
     * @param position Position we wish to set
     */
    public void setPosition(int position) {
        this.position = position;
    }
}
