package com.sadek.go4lunch.controllers.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;
import com.sadek.go4lunch.R;
import com.sadek.go4lunch.databinding.ActivityHomeBinding;
import com.sadek.go4lunch.manager.UserManager;
import com.sadek.go4lunch.utils.DeleteAlarmReceiver;
import com.sadek.go4lunch.utils.NotificationReceiver;

import java.util.Calendar;

public class HomeActivity extends BaseActivity<ActivityHomeBinding> {

    private AppBarConfiguration mAppBarConfiguration;
    private UserManager userManager = UserManager.getInstance();

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private View view;

    @Override
    ActivityHomeBinding getViewBinding() {
        return ActivityHomeBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = binding.navView.getHeaderView(0);
        setSupportActionBar(binding.appBarMain.toolbar);
        initBottomNavigationView();
        updateUIWithUserData();
        //setDeleteAlarmManager();
        setNotificationAlarmManager();
    }

    private void updateUIWithUserData() {
        if(userManager.isCurrentUserLogged()) {
            FirebaseUser user = userManager.getCurrentUser();
            if(user.getPhotoUrl() != null) {
                setProfilePicture(user.getPhotoUrl());
            }
            setTextUserData(user);
        }
    }

    private void setProfilePicture(Uri photoUrl) {
        Glide.with(this)
                .load(photoUrl)
                .apply(RequestOptions.circleCropTransform())
                .into((ImageView) view.findViewById(R.id.activity_main_header_user_image));
    }

    private void setTextUserData(@NonNull FirebaseUser user) {
        //Get email & username from User
        String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.info_no_email_found) : user.getEmail();
        String username = TextUtils.isEmpty(user.getDisplayName()) ? getString(R.string.info_no_username_found) : user.getDisplayName();

        //Update views with data
        ((TextView)view.findViewById(R.id.activity_main_header_username)).setText(username);
        ((TextView)view.findViewById(R.id.activity_main_header_email)).setText(email);
    }

    private void initBottomNavigationView() {
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_map_view, R.id.nav_list_view, R.id.nav_workmates,R.id.nav_lunch)
                .setOpenableLayout(binding.drawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(binding.navBottomView,navController);
        NavigationUI.setupWithNavController(binding.navView,navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    public static void navigate(Context context) {
        Intent intent = new Intent(context,HomeActivity.class);
        ActivityCompat.startActivity(context,intent,null);
    }

    // Method to disconnect from his session
    public void onClickLogout(MenuItem item) {
        userManager.signOut(this).addOnSuccessListener(unused -> LoginActivity.navigate(HomeActivity.this));
    }

    // Method to delete all restaurants and restaurants chosen by the workmates at 5:00 p.m
    private void setDeleteAlarmManager() {
        Calendar current = Calendar.getInstance();
        current.setTimeInMillis(System.currentTimeMillis());
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(System.currentTimeMillis());
        start.set(Calendar.HOUR_OF_DAY,17);


        Intent intent = new Intent(this, DeleteAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
           alarmManager.setExact(AlarmManager.RTC_WAKEUP, start.getTimeInMillis(),
                        pendingIntent);
        }
    }

    private void setNotificationAlarmManager() {
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(System.currentTimeMillis());
        start.set(Calendar.HOUR_OF_DAY,12);
        start.set(Calendar.MINUTE,25);

        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                2,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, start.getTimeInMillis(),
                    pendingIntent);
        }
    }
}