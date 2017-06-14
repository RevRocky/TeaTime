package rocky.teatime;

import android.app.Application;
import android.content.Context;

/**
 * A static class which enables the easy retrieval of app-wide information at any time
 */
public class TeaTime extends Application {

    private static Context appContext;  // A static copy of the current application context

    public void onCreate(){
        super.onCreate();
        appContext = getApplicationContext();   // Getting reference to application context
    }

    /**
     * Returns a reference to the current application context
     * @return Current application context
     */
    public static Context getAppContext() {
        return appContext;
    }
}
