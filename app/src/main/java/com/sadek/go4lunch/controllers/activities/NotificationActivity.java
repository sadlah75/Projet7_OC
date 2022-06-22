package com.sadek.go4lunch.controllers.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sadek.go4lunch.databinding.ActivityNotificationBinding;
import com.sadek.go4lunch.model.Workmate;
import com.sadek.go4lunch.ui.workmate.WorkmateAdapter;
import com.sadek.go4lunch.utils.WorkmateHelper;

import java.util.ArrayList;
import java.util.Objects;

public class NotificationActivity extends BaseActivity<ActivityNotificationBinding> {

    private final ArrayList<Workmate> mWorkmates = new ArrayList<>();
    private String mName;
    private String mAddress;


    @Override
    ActivityNotificationBinding getViewBinding() {
        return ActivityNotificationBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayNameAndAddress();
        //initRecyclerView();
        displayWorkmatesList();
    }

    private void displayNameAndAddress() {
        mName = getIntent().getStringExtra("name");
        mAddress = getIntent().getStringExtra("address");
        binding.activityNotificationName.setText(mName);
        binding.activityNotificationAddress.setText(mAddress);
    }


    @SuppressLint("NotifyDataSetChanged")
    private void displayWorkmatesList() {
        WorkmateHelper.getAllWorkmates().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Workmate workmate = document.toObject(Workmate.class);
                    if(!workmate.getId().equals(Objects.requireNonNull(FirebaseAuth.getInstance()
                            .getCurrentUser()).getUid())) {
                        if(workmate.getChosenRestaurant() != null && workmate.getChosenRestaurant().equals(mName)) {
                            mWorkmates.add(workmate);
                        }
                    }
                }
                if(mWorkmates.size() > 0) {
                    //Objects.requireNonNull(binding.activityNotificationRecyclerView.getAdapter()).notifyDataSetChanged();
                    initRecyclerView();
                }
            }
        });
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.activityNotificationRecyclerView.setLayoutManager(layoutManager);
        WorkmateAdapter lAdapter = new WorkmateAdapter(mWorkmates,"joining");
        // Set CustomAdapter as the adapter for RecyclerView.
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.activityNotificationRecyclerView.getContext(),
                layoutManager.getOrientation());
        binding.activityNotificationRecyclerView.addItemDecoration(dividerItemDecoration);
        binding.activityNotificationRecyclerView.setAdapter(lAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}