package com.sadek.go4lunch.utils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadek.go4lunch.model.Restaurant;

public class RestaurantHelper {

    private static final String COLLECTION_PATH = "Restaurants";

    // Get collection reference ("Restaurants")
    @NonNull
    public static CollectionReference getRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_PATH);
    }

    public static void createRestaurant(@NonNull Restaurant restaurant) {
        getRestaurantsCollection().document(restaurant.getPlaceId()).set(restaurant);
    }

    //Get Restaurant by placeId
    @NonNull
    public static Task<DocumentSnapshot> getRestaurantByPlaceId(String placeId) {
        return getRestaurantsCollection().document(placeId).get();
    }

    // Get all restaurants
    public static Task<QuerySnapshot> getAllRestaurants() {
        return getRestaurantsCollection().get();
    }

    public static Task<Void> updateNumberOfWorkmates(String uid, int number) {
        return getRestaurantsCollection().document(uid).update
                ("numberOfWorkmates", number);
    }

    public static Task<Void> updateOpenTime(String uid, String result) {
        return getRestaurantsCollection().document(uid).update("openingTime",result);
    }
}
