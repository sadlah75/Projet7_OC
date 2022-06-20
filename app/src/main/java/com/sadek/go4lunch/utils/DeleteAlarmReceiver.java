package com.sadek.go4lunch.utils;

import static com.sadek.go4lunch.Go4LunchApplication.CHANNEL_ID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sadek.go4lunch.R;
import com.sadek.go4lunch.model.Workmate;


public class DeleteAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        RestaurantHelper.getAllRestaurants().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for(DocumentSnapshot document : task.getResult()) {
                    document.getReference().delete();
                }
                deleteAllChosenRestaurants(context);
            }
        });
    }

    private void deleteAllChosenRestaurants(Context context) {
        WorkmateHelper.getAllWorkmates().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Workmate workmate = document.toObject(Workmate.class);
                    WorkmateHelper.addChosenRestaurant(workmate.getId(), null);
                }
                createNotification(context);
            }
        });
    }

    private void createNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_lunch)
                .setContentTitle("Delete Alarm Manager")
                .setContentText("Delete all restaurants around me and chosen restaurants")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123,builder.build());
    }
}


