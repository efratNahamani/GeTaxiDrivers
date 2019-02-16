package com.example.shosh.get_taxi_driver.controller;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.example.shosh.get_taxi_driver.model.backend.BackendFactory;
import com.example.shosh.get_taxi_driver.model.datasource.DatabaseFB;
import com.example.shosh.get_taxi_driver.model.entities.Ride;

import java.util.List;

public class MyService extends Service {

        private int lastCount = 0;
        Context context;
        DatabaseFB dbManager;

    /**
     * This method is called when another component (such as an activity) requests that the service be started
     * @param intent The Intent supplied to Context.startService(Intent).
     * @param flags Additional data about this start request.
     * @param startId A unique integer representing this specific request to start
     * @return value indicates what semantics the system should use for the service's current started state.
     */
        @Override
        public int onStartCommand(final Intent intent, int flags, int startId) {
            dbManager = (DatabaseFB) BackendFactory.getInstance(ApplicationContextProvider.getContext());
            context = getApplicationContext();
            dbManager.notifyToRideList(new DatabaseFB.NotifyDataChange<List<Ride>>() {

                @Override
                public void onDataChanged(List<Ride> obj) {//sending a broadcast
                    try {
                        Intent intent = new Intent(context, MyBroadcastReceiver.class);
                        sendBroadcast(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Exception exception) {
                }
            });
            return START_REDELIVER_INTENT;
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
}

