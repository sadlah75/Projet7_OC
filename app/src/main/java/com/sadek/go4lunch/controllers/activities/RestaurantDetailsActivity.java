package com.sadek.go4lunch.controllers.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

    private Restaurant restaurant;
    private List<Workmate> mWorkmates = new ArrayList<>();

    @Override
    ActivityRestaurantDetailsBinding getViewBinding() {
        return ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupIntent();

        setupListenerCall();
        setupListenerWebsite();
        setupListenerChosenRestaurant();
        configureRecyclerView();
        updateUIWithWorkmates();
    }

    private void updateUIWithWorkmates() {
        WorkmateHelper.getAllWorkmates().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Workmate workmate = document.toObject(Workmate.class);
                        if(restaurant.getName().equals(workmate.getChosenRestaurant())) {
                            mWorkmates.add(workmate);
                        }
                    }
                    binding.restaurantDetailsRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        });
    }

    private void setupListenerChosenRestaurant() {
        binding.floatingActionButtonChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                WorkmateHelper.addChosenRestaurant(uid,restaurant.getName());
                int currentNb = restaurant.getNumberOfWorkmates();
                RestaurantHelper.updateNumberOfWorkmates(restaurant.getPlaceId(),currentNb + 1);
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
                if(restaurant.getWebsite() != null) {
                    Intent website = new Intent(Intent.ACTION_VIEW,Uri.parse(restaurant.getWebsite()));
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
                if(restaurant.getPhone() != null) {
                    Intent call = new Intent(Intent.ACTION_DIAL);
                    call.setData(Uri.parse("tel:" + restaurant.getPhone()));
                    startActivity(call);
                }else {
                    binding.restaurantDetailsCall.setEnabled(false);
                }
            }
        });
    }

    private void setupIntent() {
        Intent intent = getIntent();

        restaurant = (Restaurant) intent.getSerializableExtra("restaurant");

        binding.restaurantDetailsName.setText(restaurant.getName());
        binding.restaurantDetailsAddress.setText(restaurant.getAddress());
        Glide.with(this)
                .load(restaurant.getImageUrl())
                .apply(RequestOptions.centerCropTransform())
                .into(binding.restaurantDetailsImage);

        // Display stars depending on restaurant's rating
        if (restaurant.getRating() < 4.0)
            binding.restaurantDetailsStar3.setVisibility(View.GONE);
        else if (restaurant.getRating() < 3.0)
            binding.restaurantDetailsStar2.setVisibility(View.GONE);
        else if (restaurant.getRating() < 2.0)
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