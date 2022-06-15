package com.sadek.go4lunch.utils;

import static com.sadek.go4lunch.Go4LunchApplication.CHANNEL_ID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.sadek.go4lunch.Go4LunchApplication;
import com.sadek.go4lunch.R;


public class DeleteAllRestaurantsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        RestaurantHelper.getAllRestaurants().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for(DocumentSnapshot document : task.getResult()) {
                    document.getReference().delete();
                }
                //createNotification(context);
            }
        });
    }

    private void createNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_lunch)
                .setContentTitle("Delete Alarm Manager")
                .setContentText("Delete all restaurants")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Go4LunchApplication.getNotificationManager().notify(80,builder.build());
        /*NotificationManagerCompat manager  = NotificationManagerCompat.from(context);
        manager.notify(123,builder.build());*/
    }
}


