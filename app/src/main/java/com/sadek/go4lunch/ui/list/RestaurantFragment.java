package com.sadek.go4lunch.ui.list;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadek.go4lunch.BuildConfig;
import com.sadek.go4lunch.R;
import com.sadek.go4lunch.controllers.activities.RestaurantDetailsActivity;

import com.sadek.go4lunch.databinding.FragmentRestaurantListBinding;
import com.sadek.go4lunch.model.NearByPlace;
import com.sadek.go4lunch.model.NearByPlacesDetails;
import com.sadek.go4lunch.model.Restaurant;
import com.sadek.go4lunch.model.Workmate;
import com.sadek.go4lunch.utils.RestaurantAPIStream;
import com.sadek.go4lunch.utils.RestaurantHelper;
import com.sadek.go4lunch.utils.WorkmateHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class RestaurantFragment extends Fragment implements RestaurantAdapter.OnClickItemRecyclerViewListener{

    // For design
    // Declare Recyclerview
    private RecyclerView mRecyclerview;

    // Declare list of Restaurants & adapter
    private List<Restaurant> mRestaurants = new ArrayList<>();
    private List<Workmate> mWorkmates = new ArrayList<>();
    private RestaurantAdapter mAdapter;

    // Declare Location's latitude & longitude
    public static double currentLat;
    public static double currentLong;

    private FragmentRestaurantListBinding binding;

    public RestaurantFragment(){}

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRestaurantListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getAllWorkmatesFromFirestore();
        populateListOfRestaurants();
    }

    private void getAllWorkmatesFromFirestore() {
        WorkmateHelper.getAllWorkmates().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        Workmate workmate = document.toObject(Workmate.class);
                        mWorkmates.add(workmate);
                    }
                }
            }
        });
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void configureRecyclerView() {
        mAdapter = new RestaurantAdapter(mRestaurants,this);
        binding.listRestaurantRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.listRestaurantRecyclerview.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        binding.listRestaurantRecyclerview.setAdapter(mAdapter);
    }



    private void populateListOfRestaurants() {
        RestaurantHelper.getAllRestaurants().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Restaurant restaurant = document.toObject(Restaurant.class);
                        restaurant.setDistance(setupDistance(restaurant));
                        mRestaurants.add(restaurant);
                    }
                    configureRecyclerView();
                    //binding.listRestaurantRecyclerview.getAdapter().notifyDataSetChanged();
                }
            }
        });
    }

    private int setupDistance(Restaurant restaurant) {
        Location currentLocation = new Location("Current");
        Location restaurantLocation = new Location("restaurant");

        currentLocation.setLatitude(currentLat);
        currentLocation.setLongitude(currentLong);
        restaurantLocation.setLatitude(restaurant.getLatitude());
        restaurantLocation.setLongitude(restaurant.getLongitude());

        return (int) currentLocation.distanceTo(restaurantLocation);
    }

    private void updateListOfRestaurants(List<Restaurant> restaurants) {
        mRestaurants.addAll(restaurants);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search_fragment,menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Search restaurants");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void OnClickItemRecyclerView(int position) {
        Intent intent = new Intent(getActivity(), RestaurantDetailsActivity.class);
        intent.putExtra("restaurant",mRestaurants.get(position));
        startActivity(intent);
    }

    public static void setLocation(Double latitude, Double longitude) {
        currentLat = latitude;
        currentLong = longitude;
    }
}