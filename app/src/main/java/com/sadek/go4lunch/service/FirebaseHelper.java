package com.sadek.go4lunch.service;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FirebaseHelper {

    private static FirebaseHelper sFirebaseHelper;
    private static final String COLLECTION_PATH = "workmates";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public final CollectionReference workmateRef = db.collection(COLLECTION_PATH);

    public static FirebaseHelper getInstance() {
        if(sFirebaseHelper == null) {
            sFirebaseHelper = new FirebaseHelper();
        }
        return sFirebaseHelper;
    }

    public Task<QuerySnapshot> getAllWorkmates() { return workmateRef.get();}


}
