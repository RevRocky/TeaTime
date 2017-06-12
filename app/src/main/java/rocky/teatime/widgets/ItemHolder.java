package rocky.teatime.widgets;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import rocky.teatime.R;

/**
 * Again much of the code for this comes from understanding the Timber music player project.
 */
public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView name;
    private TextView variety;
    private LinearLayout footer;
    private SquareImageView teaPic;

    /**
     * Very basic constructor which sets up the real basics of the view plus assigns the onclick listener
     * @param view
     */
    public ItemHolder(View view) {
        super(view);
        name = (TextView) view.findViewById(R.id.tea_name_grid);
        variety = (TextView) view.findViewById(R.id.tea_style_grid);
        teaPic = (SquareImageView) view.findViewById(R.id.teaImage);
        footer = (LinearLayout) view.findViewById(R.id.grid_footer);
        view.setOnClickListener(this);
    }

    //TODO Implement some actual functionality here other than making toast

    /**
     * Currently this is a rump method simply to test that everything works. I do not yet know
     * what I want this to do.
     * @param v The specific view that was clicked on!
     */
    @Override
    public void onClick(View v) {
        System.out.println("You clicked on me.");
    }

    /**
     * Sets the colour of the footer to be the specified colour
     * @param colour A resources id corresponding to a particular colour
     */
    public void setFooterColour(int colour) {
        footer.setBackgroundColor(colour);
    }

    // Here be some getters!
    public TextView getName() {
        return name;
    }

    public TextView getVariety() {
        return variety;
    }

    public SquareImageView getTeaPic() {
        return teaPic;
    }
}