package rocky.teatime.helpers;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.widget.ImageView;

import java.io.FileOutputStream;
import java.io.IOException;

import rocky.teatime.widgets.SquareImageView;

import static android.graphics.BitmapFactory.decodeFile;

/**
 * Created by rocky on 17-07-07.
 */

public class ImageHelper {
    /**
     * Prepares an appropriately sized bitmap thumbnail of the image which has just been
     * taken
     * @param pathToFile Path to the image file on Disk
     * @param width Width of the image button
     * @param height Height of the image button
     * @return The image resized to fit within the button
     */
    public static Bitmap saveImageAsSize(String pathToFile, int width, int height) {
        // Getting the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        decodeFile(pathToFile, bmOptions);
        int imgW = bmOptions.outWidth;
        int imgH = bmOptions.outHeight;

        // Determine how much to scale the image by
        int scaleFactor = Math.min(imgW/width, imgH/height);

        // Decoding the image file to a bitmap sized to fill our view
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap resizedImg =  BitmapFactory.decodeFile(pathToFile, bmOptions);
        saveImage(pathToFile, resizedImg);        // Save the resized version over the original
        return resizedImg;
    }

    /**
     * Saves a bitmap image to disk at currentPhotoPath as a JPEG image!
     * @param currentPhotoPath Photo path we wish to save the file to
     * @param image The image to be saved to disk.
     */
    public static void saveImage(String currentPhotoPath, Bitmap image) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(currentPhotoPath);
            image.compress(Bitmap.CompressFormat.JPEG, 100, out);   // Actually saving the file
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (out!= null) {
                    out.close();    // Close the stream
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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

        //Decoding full size image
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
        Bitmap image = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        // Decoding full size image
        try {
            image = decodeFile(filePath, options);
        } catch (OutOfMemoryError e) {    // Incase we run out of memory decoding the bitmap, this is for safety!
            options.inSampleSize = 2;
            image = decodeFile(filePath, options);
        } finally {
            // How much do we scale the image by
            int scaleFactor = Math.min(image.getWidth() / view.getWidth(),
                    image.getHeight() / view.getHeight());
            options.inSampleSize = scaleFactor;

            Bitmap scaledImage = decodeFile(filePath, options);
            view.setImageBitmap(scaledImage);   // Setting the view to be the scaled image
        }
    }

    /**
     * Fits a bitmap at the supplied file path into a square image view
     * @param view View we wish to fit image into
     * @param resources System resources
     * @param resourceID The resource id corresponding to the image we wish to fit the image to.
     */
    public static void fitImagetoSquareView(SquareImageView view, Resources resources, int resourceID) {
        Bitmap scaledImage = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;             // Shrink image by 2?
        options.inJustDecodeBounds = true;
        try {
            scaledImage = BitmapFactory.decodeResource(resources, resourceID, options);
        }
        catch (Error | Exception e) {
            e.printStackTrace();

        }
        view.setImageBitmap(scaledImage);   // Setting the view to be the scaled image
    }

    /**
     * Fits a bitmap at the supplied file path into a square image view
     * @param view View we wish to fit the image into
     * @param filePath Path to the file we wish to fit.
     */
    public static void fitImagetoSquareView(SquareImageView view, String filePath) {
        Bitmap image = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        // Decoding full size image
        try {
            image = decodeFile(filePath, options);
        }
        catch (OutOfMemoryError e) {    // Incase we run out of memory decoding the bitmap, this is for safety!
            options.inSampleSize = 2;
            image = decodeFile(filePath, options);
        }
        finally {
            view.setImageBitmap(image);   // Setting the view to be the scaled image
        }
    }

    /**
     * Fits a bitmap of a black and white version of the supplied image to a square image view.
     * This allows us to grey out an image when a given tea is not in stock.
     * @param view View we wish to fit the image into
     * @param filePath Path to the file we wish to fit.
     */
    public static void fitImagetoSquareViewBW(SquareImageView view, String filePath) {
        Bitmap image = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        // Decoding full size image
        try {
            image = decodeFile(filePath, options);
            image = colourToGreyScale(image);
        }
        catch (OutOfMemoryError e) {    // Incase we run out of memory decoding the bitmap, this is for safety!
            options.inSampleSize = 2;
            image = decodeFile(filePath, options);
        }
        finally {
            view.setImageBitmap(image);   // Setting the view to be the scaled image
        }
    }

    /**
     * Turns an image into a black and white version of itself.
     * @param colourImage This is the colour version of the image we wish to transform.
     * @return A greyscale version of the image supplied to the method.
     */
    public static Bitmap colourToGreyScale(Bitmap colourImage) {
        ColorMatrix colourMatrix = new ColorMatrix();
        colourMatrix.setSaturation(0.0f);       // Setting extremely low saturation!

        // Making a filter based upon the above settings.
        ColorMatrixColorFilter colourMatrixFilter = new ColorMatrixColorFilter(colourMatrix);

        Paint paint = new Paint();
        paint.setColorFilter(colourMatrixFilter);

        Bitmap greyScaleImage = colourImage.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(greyScaleImage); // Make a canvas based on the image
        canvas.drawBitmap(greyScaleImage, 0, 0, paint);     // Draw the bitmap put through the filter
        return greyScaleImage;

    }
}
