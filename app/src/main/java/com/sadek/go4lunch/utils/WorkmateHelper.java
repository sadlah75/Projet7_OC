package com.sadek.go4lunch.utils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadek.go4lunch.model.Restaurant;
import com.sadek.go4lunch.model.Workmate;

public class WorkmateHelper {

    private static final String COLLECTION_PATH = "workmates";


    public static FirebaseUser getCurrentWorkmate() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }


    // Get collection reference ("workmates")
    @NonNull
    public static CollectionReference getWorkmatesCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_PATH);
    }

    // Create workmate in Database Firestore
    public static void createWorkmate(@NonNull Workmate workmate) {
        getWorkmatesCollection().document(workmate.getId()).set(workmate);
    }

    // Get workmate from Firestore
    @NonNull
    public static Task<DocumentSnapshot> getWorkmateByUID(String uid) {
        return getWorkmatesCollection().document(uid).get();
    }
    // Get all workmates from Firestore
    @NonNull
    public static Task<QuerySnapshot> getAllWorkmates() {
        return getWorkmatesCollection().get();
    }

    // Update some workmates's fields

    public static Task<Void> addChosenRestaurant(String uid, String name) {
        return getWorkmatesCollection().document(uid).update
                ("chosenRestaurant", name);

    }

    // Delete workmate from Firestore
    public static void deleteWorkmate(String uid) {
        getWorkmatesCollection().document(uid).delete();
    }
}
