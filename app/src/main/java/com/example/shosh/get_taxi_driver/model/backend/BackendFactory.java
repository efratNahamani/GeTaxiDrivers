package com.example.shosh.get_taxi_driver.model.backend;

import android.content.Context;

import com.example.shosh.get_taxi_driver.model.backend.Backend;

public class BackendFactory {
    static Backend instance=null;
    public static String mode="fb";
    /**
     * this function return the instance of Backend
     * @return Backend
     * @param context the activity
     */
    public final static Backend getInstance(Context context){
        if(mode=="lists") {//DataBaseList
            if (instance == null)
                //instance = new com.example.shosh.get_taxi_driver.model.datasource.DatabaseList();
            return instance;
        }
        if(mode=="fb") {//DataBaseFB
            if (instance == null)
                instance = new com.example.shosh.get_taxi_driver.model.datasource.DatabaseFB();
            return instance;
        }
        else
            return null;


    }
}
