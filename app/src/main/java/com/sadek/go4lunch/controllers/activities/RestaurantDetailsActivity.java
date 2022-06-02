package com.sadek.go4lunch.controllers.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadek.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import com.sadek.go4lunch.model.Restaurant;
import com.sadek.go4lunch.model.Workmate;
import com.sadek.go4lunch.ui.workmate.WorkmateAdapter;
import com.sadek.go4lunch.utils.RestaurantHelper;
import com.sadek.go4lunch.utils.WorkmateHelper;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDetailsActivity extends BaseActivity<ActivityRestaurantDetailsBinding> {

    private Restaurant mRestaurant;
    private List<Workmate> mWorkmates = new ArrayList<>();
    String chosenRestaurantCurrentUser;
    private Restaurant mRestaurantFromFirestore;

    @Override
    ActivityRestaurantDetailsBinding getViewBinding() {
        return ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupIntent();
        init();

        setupListenerCall();
        setupListenerWebsite();
        setupListenerChosenRestaurant();
        configureRecyclerView();
        updateUIWithWorkmates();
    }

    private void init() {
        String uid = WorkmateHelper.getCurrentWorkmate().getUid();
        WorkmateHelper.getWorkmateByUID(uid).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Workmate workmate = task.getResult().toObject(Workmate.class);
                    chosenRestaurantCurrentUser = workmate.getChosenRestaurant();
                }
            }
        });

        RestaurantHelper.getRestaurantByPlaceId(mRestaurant.getPlaceId())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    mRestaurantFromFirestore = task.getResult().toObject(Restaurant.class);
                }
            }
        });
    }

    private void updateUIWithWorkmates() {
        WorkmateHelper.getAllWorkmates().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Workmate workmate = document.toObject(Workmate.class);
                        if(mRestaurant.getName().equals(workmate.getChosenRestaurant())) {
                            mWorkmates.add(workmate);
                        }
                    }
                    binding.restaurantDetailsRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        });
    }

    //---------------------------------------------------------------------------------------
    private void setupListenerChosenRestaurant() {
        binding.floatingActionButtonChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = WorkmateHelper.getCurrentWorkmate().getUid();
                if(!mRestaurant.getName().equals(chosenRestaurantCurrentUser)) {
                    WorkmateHelper.addChosenRestaurant(uid,mRestaurant.getName());
                    int number = mRestaurantFromFirestore.getNumberOfWorkmates() + 1;
                    RestaurantHelper.updateNumberOfWorkmates(mRestaurant.getPlaceId(),number);
                }else {
                    WorkmateHelper.addChosenRestaurant(uid,"");
                    int number = mRestaurantFromFirestore.getNumberOfWorkmates() - 1;
                    RestaurantHelper.updateNumberOfWorkmates(mRestaurant.getPlaceId(),number);
                }
                mWorkmates.clear();
                updateUIWithWorkmates();
            }
        });
    }

    // Setup Listener website button
    private void setupListenerWebsite() {
        binding.restaurantDetailsWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mRestaurant.getWebsite() != null) {
                    Intent website = new Intent(Intent.ACTION_VIEW,Uri.parse(mRestaurant.getWebsite()));
                    startActivity(website);
                }else {
                    binding.restaurantDetailsWebsite.setEnabled(false);
                }
            }
        });
    }

    // Setup Listener call button
    private void setupListenerCall() {
        binding.restaurantDetailsCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mRestaurant.getPhone() != null) {
                    Intent call = new Intent(Intent.ACTION_DIAL);
                    call.setData(Uri.parse("tel:" + mRestaurant.getPhone()));
                    startActivity(call);
                }else {
                    binding.restaurantDetailsCall.setEnabled(false);
                }
            }
        });
    }

    private void setupIntent() {
        Intent intent = getIntent();

        mRestaurant = (Restaurant) intent.getSerializableExtra("restaurant");

        binding.restaurantDetailsName.setText(mRestaurant.getName());
        binding.restaurantDetailsAddress.setText(mRestaurant.getAddress());
        Glide.with(this)
                .load(mRestaurant.getImageUrl())
                .apply(RequestOptions.centerCropTransform())
                .into(binding.restaurantDetailsImage);

        // Display stars depending on restaurant's rating
        if (mRestaurant.getRating() < 4.0)
            binding.restaurantDetailsStar3.setVisibility(View.GONE);
        else if (mRestaurant.getRating() < 3.0)
            binding.restaurantDetailsStar2.setVisibility(View.GONE);
        else if (mRestaurant.getRating() < 2.0)
            binding.restaurantDetailsStar1.setVisibility(View.GONE);
    }

    // Configure RecyclerView to display workmates
    private void configureRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.restaurantDetailsRecyclerView.setLayoutManager(layoutManager);
        WorkmateAdapter lAdapter = new WorkmateAdapter(mWorkmates,"joining");
        // Set CustomAdapter as the adapter for RecyclerView.
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.restaurantDetailsRecyclerView.getContext(),
                layoutManager.getOrientation());
        binding.restaurantDetailsRecyclerView.addItemDecoration(dividerItemDecoration);
        binding.restaurantDetailsRecyclerView.setAdapter(lAdapter);
    }


}