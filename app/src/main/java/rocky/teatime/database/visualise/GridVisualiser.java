package rocky.teatime.database.visualise;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import rocky.teatime.R;
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
    public void onBindViewHolder(ItemHolder itemHolder, int dbPosition) {
        final Tea currentTea = teaList.get(dbPosition);
        // Setting itemholder attributes!
        itemHolder.getName().setText(currentTea.getName());
        itemHolder.getVariety().setText(currentTea.getType().name());

        // If there is an image we should display it. Otherwise the default image wil display on its
        // own
        if (!currentTea.getPicLocation().equals("NULL")) {
            ImageHelper.fitImagetoSquareView(itemHolder.getTeaPic(), currentTea.getPicLocation());
        }

    }

}
