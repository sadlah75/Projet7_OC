package com.sadek.go4lunch.utils;

import static com.sadek.go4lunch.Go4LunchApplication.CHANNEL_ID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sadek.go4lunch.Go4LunchApplication;
import com.sadek.go4lunch.R;
import com.sadek.go4lunch.model.Workmate;


public class DeleteChosenRestaurantReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
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
                .setContentText("Delete all chosen restaurants")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Go4LunchApplication.getNotificationManager().notify(75,builder.build());
        /*
        NotificationManagerCompat manager  = NotificationManagerCompat.from(context);
        manager.notify(123,builder.build());
        */
    }



            /*
        RestaurantHelper.getAllRestaurants().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for(QueryDocumentSnapshot document : task.getResult()) {
                    Restaurant restaurant = document.toObject(Restaurant.class);
                    if(restaurant.getNumberOfWorkmates() != 0)
                        RestaurantHelper.updateNumberOfWorkmates(restaurant.getPlaceId(), 0);
                }
                Toast.makeText(context.getApplicationContext(), "Update Restaurants OK", Toast.LENGTH_SHORT).show();
            }
        });
         */
}
