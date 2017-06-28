package rocky.teatime.database.visualise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;

import rocky.teatime.R;
import rocky.teatime.TeaTime;
import rocky.teatime.activities.ViewTeaActivity;
import rocky.teatime.database.TeaStuff.JsonTea;
import rocky.teatime.database.TeaStuff.Tea;
import rocky.teatime.widgets.ImageHelper;
import rocky.teatime.widgets.ItemHolder;

/**
 * Handles the display of database items in a nice grid based format!
 */
public class GridVisualiser extends DatabaseVisualiser {

    /**
     * A standard constructor accepting a list of teas and the programme context
     * @param teaList A list of teas stored in the database.
     * @param context Current programme state.
     */
    public GridVisualiser(ArrayList<Tea> teaList, Context context) {
        super(teaList, context);
    }

    /**
     * Inflates the teaGrid layout and returns a new ItemHolder object representing the new view
     * created
     * @param viewGroup The parent viewgroup of the grid items
     * @param viewType Some integer. It's not really... used. Represents which object in the array
     *                we are dealing with.
     * @return An ItemHolder object representing the view that was created
     */
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tea_grid, viewGroup,
                    false);
        ItemHolder itemHolster = new ItemHolder(view);
        return itemHolster;
    }

    /**
     * Binds the view to a particular Database entry and populates the itemHolder with attributes
     * pertaining to the tea item to be shown to the viewer.
     * @param itemHolder Coordinates the display and functionality of each grid cell.
     * @param dbPosition The position in the array that the current tea which were working on id
     *                   found.
     */
    public void onBindViewHolder(final ItemHolder itemHolder, final int dbPosition) {
        final Tea currentTea = teaList.get(dbPosition);
        // Setting itemholder attributes!
        itemHolder.getName().setText(currentTea.getName());
        itemHolder.getVariety().setText(currentTea.getType().name());

        // If there is an image we should display it. Otherwise the default image wil display on its
        // own
        if (!currentTea.getPicLocation().equals("NULL")) {
            ImageHelper.fitImagetoSquareView(itemHolder.getTeaPic(), currentTea.getPicLocation());
        }
        // Setting the footer colour
        itemHolder.setFooterColour(currentTea.getColour());

        // Adding a listener so we can capture the position of the menu item if a user
        // uses the context menu
        itemHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(dbPosition);
                return true;
            }
        });

        // Setting an On Click listener which will take us to the view tea activity
        itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPosition(dbPosition);

                // We should always take the branch under a standard environment as context is always
                // instantiated by passing in "this" from the database activity.
                if (context instanceof Activity) {
                    Intent viewTeaIntent = new Intent(context, ViewTeaActivity.class);
                    JsonTea jsonTea = new JsonTea(getTea(dbPosition));
                    viewTeaIntent.putExtra(Tea.TEA_PAYLOAD_KEY, new Gson().toJson(jsonTea, JsonTea.class));

                    // Starting the activity
                    ((Activity) context).startActivityForResult(viewTeaIntent,
                            ViewTeaActivity.VIEW_TEA_REQUEST);
                }
                else {
                    Log.e("ERROR", "The context we have is not an instance of an Activity");
                }

            }


        });
    }

}
