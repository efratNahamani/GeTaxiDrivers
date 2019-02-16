package com.example.shosh.get_taxi_driver.controller;

import android.app.Application;
import android.content.Context;

//this class returns the Application Context (we changed something in the manifest in order it will work...)
public class ApplicationContextProvider extends Application {
    private static Context context;


    @Override
    /**
     * when this class is created
     */
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    /**
     * @return the context of the application
     */
    public static Context getContext(){return context;}
}
