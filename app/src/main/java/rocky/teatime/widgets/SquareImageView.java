package rocky.teatime.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatImageView;

/**
 * This code is largely copped from the Timber Open source Media Player
 */
public class SquareImageView extends AppCompatImageView {

    /**
     * Simple one paramater constructor.
     * @param context Application context.
     */
    public SquareImageView(Context context) {
        super(context);
    }

    /**
     * Pretty standard constructor.
     * @param context Application context
     * @param attributes Additional attributes for the square image to display
     * @param style Style to display the image in
     */
    public SquareImageView(Context context, AttributeSet attributes, int style) {
        super(context, attributes, style);
    }

    /**
     * Ensures that the image displayed is always a square
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());   // Setting image to square
    }
}
