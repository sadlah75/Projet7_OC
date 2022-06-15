package com.sadek.go4lunch.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.sadek.go4lunch.BuildConfig;
import com.sadek.go4lunch.R;
import com.sadek.go4lunch.controllers.activities.RestaurantDetailsActivity;
import com.sadek.go4lunch.databinding.FragmentMapViewBinding;
import com.sadek.go4lunch.model.NearByPlace;
import com.sadek.go4lunch.model.NearByPlacesDetails;
import com.sadek.go4lunch.model.Restaurant;
import com.sadek.go4lunch.ui.list.RestaurantFragment;
import com.sadek.go4lunch.utils.RestaurantAPIStream;
import com.sadek.go4lunch.utils.RestaurantHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 16;
    private static final String KEY_CAMERA_POSITION = "camera position";
    private static final String KEY_LOCATION = "location";
    private boolean locationPermissionGranted;
    private GoogleMap mMap;
    private CameraPosition cameraPosition;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationClient;

    /* A default location (Paris, France) and default zoom to use when location permission is
     not granted.
     */
    private final LatLng defaultLocation = new LatLng(48.856614, 2.3522219);

    private Location currentLocation;
    private List<Restaurant> mRestaurantsWithRetrofit = new ArrayList<>();
    private List<Restaurant> mRestaurantsFromFirebase = new ArrayList<>();
    private FragmentMapViewBinding binding;

    public MapViewFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapViewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Construct a FusedLocationProviderClient.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        getCurrentLocation();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, currentLocation);
        }
        super.onSaveInstanceState(outState);

    }

    private void getAllRestaurantsFromFirebase() {
        RestaurantHelper.getAllRestaurants().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                List<DocumentSnapshot> list = task.getResult().getDocuments();
                if(!list.isEmpty()) {
                    for (DocumentSnapshot documentSnapshot : list) {
                        Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
                        mRestaurantsFromFirebase.add(restaurant);
                    }
                    populateMapWithMarkers();
                }else {
                    populateRestaurantsWithRetrofit();
                }
            }
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }

        Task<Location> task = mFusedLocationClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;

                RestaurantFragment.setLocation(currentLocation.getLatitude(), currentLocation.getLongitude());
                //Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                assert supportMapFragment != null;
                supportMapFragment.getMapAsync(MapViewFragment.this);

                getAllRestaurantsFromFirebase();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                // If the permission is granted, get the location,
                // otherwise, show a Toast
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMarkerClickListener(onMarkerClickListener);

        //Add a marker in current location  and move the camera
        LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));

    }

    private void populateRestaurantsWithRetrofit() {
        executeHttpRequestWithRetrofit();
    }

    private void executeHttpRequestWithRetrofit() {
        String type = "restaurant";
        String location = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
        String key = BuildConfig.GOOGLE_API_KEY;

        Disposable disposable = RestaurantAPIStream.streamFetchNearByPlace(location,type,500,key)
                .subscribeWith(new DisposableObserver<NearByPlace>() {
                    @Override
                    public void onNext(NearByPlace nearByPlace) {
                        mRestaurantsWithRetrofit = populateListOfRestaurants(nearByPlace.getResults());
                        populateListOfRestaurantsWithDetails();
                    }
                    @Override
                    public void onError(Throwable e) {}
                    @Override
                    public void onComplete() {}
                });
    }

    private void executeRetrofitWithDetails(Restaurant restaurant) {
        String key = BuildConfig.GOOGLE_API_KEY;
        String placeId = restaurant.getPlaceId();

        Disposable disposable = RestaurantAPIStream.streamFetchNearByPlaceDetails(key,placeId)
                .subscribeWith(new DisposableObserver<NearByPlacesDetails>() {
                    @Override
                    public void onNext(NearByPlacesDetails nearByPlacesDetails) {
                        boolean isExist = false;
                        restaurant.addDataFromNearByPlacesDetails(nearByPlacesDetails.getResult());
                        RestaurantHelper.createRestaurant(restaurant);
                    }
                    @Override
                    public void onError(Throwable e) {}
                    @Override
                    public void onComplete() {}
                });
    }

    @NonNull
    private List<Restaurant> populateListOfRestaurants(@NonNull List<NearByPlace.Result> results) {
        List<Restaurant> lRestaurants = new ArrayList<>();
        for(NearByPlace.Result result : results) {
            Restaurant restaurant = Restaurant.addDataFromNearByPlace(result);
            lRestaurants.add(restaurant);
        }
        return lRestaurants;
    }

    private void populateListOfRestaurantsWithDetails() {
        for(Restaurant restaurant : mRestaurantsWithRetrofit) {
            executeRetrofitWithDetails(restaurant);
            mRestaurantsFromFirebase.add(restaurant);
        }
        populateMapWithMarkers();
    }


    private void populateMapWithMarkers() {
        for (Restaurant restaurant : mRestaurantsFromFirebase) {
            Marker marker = mMap.addMarker(new MarkerOptions()
            .position(new LatLng(restaurant.getLatitude(), restaurant.getLongitude())));
            if(restaurant.getNumberOfWorkmates() > 0) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            }
        }
    }


    GoogleMap.OnMarkerClickListener onMarkerClickListener = marker -> {
        LatLng currentMarker = marker.getPosition();
        Restaurant restaurant = null;
        // Do something with the marker
        for (Restaurant r : mRestaurantsFromFirebase) {
            if (currentMarker.equals(new LatLng(r.getLatitude(),r.getLongitude()))) {
                restaurant = r;
                break;
            }
        }
        Intent intent = new Intent(getActivity(), RestaurantDetailsActivity.class);
        intent.putExtra("restaurant",restaurant);
        startActivity(intent);
        return false;
    };


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search_fragment,menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
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

}