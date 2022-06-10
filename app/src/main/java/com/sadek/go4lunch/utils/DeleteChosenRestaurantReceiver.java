package com.sadek.go4lunch.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadek.go4lunch.R;
import com.sadek.go4lunch.model.Restaurant;
import com.sadek.go4lunch.model.Workmate;

public class DeleteChosenRestaurantReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        WorkmateHelper.getAllWorkmates().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        Workmate workmate = document.toObject(Workmate.class);
                        WorkmateHelper.addChosenRestaurant(workmate.getId(), null);
                    }
                    //createNotification(context);
                }
                
            }
        });
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

    private void createNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"Go4Lunch")
                .setSmallIcon(R.drawable.ic_lunch)
                .setContentTitle("Delete Alarm Manager")
                .setContentText("Delete all chosen restaurants")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat manager  = NotificationManagerCompat.from(context);
        manager.notify(123,builder.build());
    }
}
