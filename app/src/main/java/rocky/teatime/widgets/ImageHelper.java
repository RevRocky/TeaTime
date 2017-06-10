package rocky.teatime.widgets;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

/**
 * A static class that assists in various tasks related to image manipulation!
 */
public class ImageHelper {

    /**
     * Returns a standard set of factory options which will ensure that image fits nicely within
     * the supplied view. This version of the method deals with images in the application resources
     * @param view The view we wish to fit the image in.
     * @param resources Resource set we wish to draw from
     * @param resourceID The id pertaining to the image resource
     */
    public static void fitImagetoView(ImageView view, Resources resources, int resourceID) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        // Decoding full size image
        Bitmap image = BitmapFactory.decodeResource(resources, resourceID, options);

        // How much do we scale the image by
        int scaleFactor = Math.min(image.getWidth() / view.getWidth(),
                image.getHeight() / view.getHeight());
        options.inSampleSize = scaleFactor;
        options.inPurgeable = true;

        Bitmap scaledImage = BitmapFactory.decodeResource(resources, resourceID, options);
        view.setImageBitmap(scaledImage);   // Setting the view to be the scaled image
    }

    /**
     * Fits a bitmap at the supplied file path nicely in to the supplied image view!
     * @param view View we wish to fit the image into
     * @param filePath Path to the file we wish to fit.
     */
    public static void fitImagetoView(ImageView view, String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        // Decoding full size image
        Bitmap image = BitmapFactory.decodeFile(filePath, options);

        // How much do we scale the image by
        int scaleFactor = Math.min(image.getWidth() / view.getWidth(),
                image.getHeight() / view.getHeight());
        options.inSampleSize = scaleFactor;
        options.inPurgeable = true;

        Bitmap scaledImage = BitmapFactory.decodeFile(filePath, options);
        view.setImageBitmap(scaledImage);   // Setting the view to be the scaled image
    }
}
