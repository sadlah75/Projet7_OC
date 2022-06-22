package com.sadek.go4lunch.utils;

import static com.sadek.go4lunch.Go4LunchApplication.CHANNEL_ID;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadek.go4lunch.R;
import com.sadek.go4lunch.controllers.activities.NotificationActivity;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(SharedPreferencesHelper.getRestaurantName(context)!= null
            && SharedPreferencesHelper.getRestaurantAddress(context) != null) {
            setNotification(context, intent);
        }

    }

    private void setNotification(Context context, Intent intent) {
        Intent i = new Intent(context, NotificationActivity.class);
        i.putExtra("name",SharedPreferencesHelper.getRestaurantName(context));
        i.putExtra("address",SharedPreferencesHelper.getRestaurantAddress(context));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_lunch)
                .setContentTitle("Notification Workmates Alarm")
                .setContentText("Who joins you for lunch ?!")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(456,builder.build());
    }
}
