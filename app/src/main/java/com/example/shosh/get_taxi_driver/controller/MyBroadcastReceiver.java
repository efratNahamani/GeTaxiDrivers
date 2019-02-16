package com.example.shosh.get_taxi_driver.controller;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.shosh.get_taxi_driver.R;

public class MyBroadcastReceiver extends BroadcastReceiver {

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent broadcast
     * @param context is the Context in which the receiver is running
     * @param intent is the Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);//the react of click on the notification

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);

                // Configure the notification channel.
                notificationChannel.setDescription("Channel description");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.CYAN);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            //build the notification
            NotificationCompat.Builder b = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

            b.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_action_taxi)
                    .setContentTitle("new request")
                    .setContentText("you have a new relevant request")
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                    .setContentIntent(contentIntent)
                    .setContentInfo("Info");

            notificationManager.notify(1, b.build());
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }


    }
}

