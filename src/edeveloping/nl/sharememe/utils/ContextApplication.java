package edeveloping.nl.sharememe.utils;

import android.app.Application;
import android.content.Context;

public class ContextApplication extends Application{

    private static Context context;

    public void onCreate(){
        super.onCreate();
        ContextApplication.context = getApplicationContext();
    }
}