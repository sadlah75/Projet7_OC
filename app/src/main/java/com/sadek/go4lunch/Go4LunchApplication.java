package com.sadek.go4lunch;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import java.util.Objects;

public class Go4LunchApplication extends Application {

    public  static final String CHANNEL_ID = "channel1";
    private  static NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel("DeleteNotification",
                "Channel for delete all chosen restaurants",
                NotificationManager.IMPORTANCE_HIGH);
    }

    public static NotificationManager getNotificationManager() {
        return notificationManager;
    }

    private void createNotificationChannel(String name, String description, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,name,importance);
            channel.setDescription(description);
            notificationManager = getSystemService(NotificationManager.class);

            if(notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }

        }
    }
}
