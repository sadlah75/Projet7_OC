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
import com.sadek.go4lunch.utils.SharedPreferencesHelper;
import com.sadek.go4lunch.utils.WorkmateHelper;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDetailsActivity extends BaseActivity<ActivityRestaurantDetailsBinding> {

    private Restaurant mCurrentRestaurant;
    private Restaurant mOldRestaurant;
    private List<Workmate> mWorkmates = new ArrayList<>();
    private String chosenRestaurantCurrentUser;
    private String uid;
    private int numberOfWorkmates;
    private Restaurant mRestaurantFromFirestore;

    @Override
    ActivityRestaurantDetailsBinding getViewBinding() {
        return ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uid = WorkmateHelper.getCurrentWorkmate().getUid();

        setupIntent();
        setupListenerCall();
        setupListenerWebsite();
        getRestaurantFromFirestore();

        getChosenRestaurantCurrentUser();
        updateUIWithWorkmates();
        configureRecyclerView();
    }

    // 1. Je récupère le nom du restaurant anciennement choisie
    // 2. J'appelle la méthode chargée de récupérer le restaurant correspondant
    private void getChosenRestaurantCurrentUser() {
        String uid = WorkmateHelper.getCurrentWorkmate().getUid();
        WorkmateHelper.getWorkmateByUID(uid).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Workmate workmate = task.getResult().toObject(Workmate.class);
                   // chosenRestaurantCurrentUser = workmate.getChosenRestaurant();
                    getOldRestaurantCurrentUser();
                }
            }
        });
    }

    // 1. je récupère le restaurant dont le nom correspond au choix de l'utilisateur
    private void getOldRestaurantCurrentUser() {
        RestaurantHelper.getAllRestaurants().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        Restaurant restaurant = document.toObject(Restaurant.class);
                        if(restaurant.getName().equals(chosenRestaurantCurrentUser)) {
                            mOldRestaurant = restaurant;
                            break;
                        }
                    }
                    setupListenerChosenRestaurant();
                }
            }
        });
    }

    private void getRestaurantFromFirestore() {
        RestaurantHelper.getRestaurantByPlaceId(mCurrentRestaurant.getPlaceId())
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            mRestaurantFromFirestore = task.getResult().toObject(Restaurant.class);
                            numberOfWorkmates = mRestaurantFromFirestore.getNumberOfWorkmates();
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
                        if(mCurrentRestaurant.getName().equals(workmate.getChosenRestaurant())) {
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
                getRestaurantFromFirestore();
                getOldRestaurantCurrentUser();

                //Si le nom du restaurant choisi est différent du restaurant selectionné
                if(!mCurrentRestaurant.getName().equals(chosenRestaurantCurrentUser)) {
                    //si l'tuilisateur n'as pas chosi de restaurant
                    if(chosenRestaurantCurrentUser != null) {
                        int nb = mOldRestaurant.getNumberOfWorkmates();
                        RestaurantHelper.updateNumberOfWorkmates(mOldRestaurant.getPlaceId(),nb-1);
                    }
                        numberOfWorkmates++;

                        WorkmateHelper.addChosenRestaurant(uid, mCurrentRestaurant.getName());
                        chosenRestaurantCurrentUser = mCurrentRestaurant.getName();

                // si identique
                }else {
                    numberOfWorkmates--;
                    WorkmateHelper.addChosenRestaurant(uid,null);
                }


                SharedPreferencesHelper.setData(RestaurantDetailsActivity.this,mCurrentRestaurant.getName(),
                        mCurrentRestaurant.getAddress());

                RestaurantHelper.updateNumberOfWorkmates(mCurrentRestaurant.getPlaceId(),numberOfWorkmates);
                mWorkmates.clear();
                updateUIWithWorkmates();
            }
        });
    }
    //----------------------------------------------------------------------------------------

    // Setup Listener website button
    private void setupListenerWebsite() {
        binding.restaurantDetailsWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCurrentRestaurant.getWebsite() != null) {
                    Intent website = new Intent(Intent.ACTION_VIEW,Uri.parse(mCurrentRestaurant.getWebsite()));
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
                if(mCurrentRestaurant.getPhone() != null) {
                    Intent call = new Intent(Intent.ACTION_DIAL);
                    call.setData(Uri.parse("tel:" + mCurrentRestaurant.getPhone()));
                    startActivity(call);
                }else {
                    binding.restaurantDetailsCall.setEnabled(false);
                }
            }
        });
    }

    private void setupIntent() {
        Intent intent = getIntent();

        mCurrentRestaurant = (Restaurant) intent.getSerializableExtra("restaurant");

        binding.restaurantDetailsName.setText(mCurrentRestaurant.getName());
        binding.restaurantDetailsAddress.setText(mCurrentRestaurant.getAddress());
        Glide.with(this)
                .load(mCurrentRestaurant.getImageUrl())
                .apply(RequestOptions.centerCropTransform())
                .into(binding.restaurantDetailsImage);

        // Display stars depending on restaurant's rating
        if (mCurrentRestaurant.getRating() < 4.0)
            binding.restaurantDetailsStar3.setVisibility(View.GONE);
        else if (mCurrentRestaurant.getRating() < 3.0)
            binding.restaurantDetailsStar2.setVisibility(View.GONE);
        else if (mCurrentRestaurant.getRating() < 2.0)
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}