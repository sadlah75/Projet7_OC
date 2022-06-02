package com.sadek.go4lunch.repository;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadek.go4lunch.model.Workmate;
import java.util.ArrayList;

public class WorkmateRepository {

    private static final String COLLECTION_PATH = "workmates";
    private static volatile WorkmateRepository instance;
    private ArrayList<Workmate> listOfWorkmates = new ArrayList<>();

    private WorkmateRepository() {}

    public static WorkmateRepository getInstance() {
        WorkmateRepository result = instance;
        if(result != null) {
            return result;
        }
        synchronized (WorkmateRepository.class) {
            if(instance == null) {
                instance = new WorkmateRepository();
            }
            return instance;
        }
    }

    @Nullable
    public FirebaseUser getCurrentWorkmate() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public String getCurrentWorkmateUID() {
        FirebaseUser user = getCurrentWorkmate();
        return (user != null) ? user.getUid() : null;
    }

    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        return AuthUI.getInstance().delete(context);
    }

    // Get the Collection Reference
    private CollectionReference getWorkmatesCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_PATH);
    }

    //  Create Workmate in Firestore
    public void createWorkmate() {
        FirebaseUser user = this.getCurrentWorkmate();
        if(user != null) {
            String urlPhoto = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String username = user.getDisplayName();
            String uid = user.getUid();
            String email = user.getEmail();

            getWorkmatesCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        boolean isExist = false;
                        if(!task.getResult().getDocuments().isEmpty()) {
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                if(isExist = document.getId().equals(uid))
                                    break;
                            }
                        }
                        Log.i("isExist","value: " + isExist);

                        if(!isExist) {
                            Workmate workmateToCreate = new Workmate(uid,username,email,urlPhoto,null,null,false);
                            getWorkmatesCollection().document(uid).set(workmateToCreate);
                        }
                    }
                }
            });
        }
    }

    // Get All Workmates
    public ArrayList<Workmate> getAllWorkmates() {
        getWorkmatesCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    //ArrayList<Workmate> workmates = new ArrayList<>();
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        listOfWorkmates.add(document.toObject(Workmate.class));
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
        Log.i("workmates","workmates size():" + listOfWorkmates.size());
        return listOfWorkmates;
    }

    // Get user data from Firestore
    public Task<DocumentSnapshot> getWorkmateData() {
        String uid = this.getCurrentWorkmateUID();
        if(uid != null) {
            return this.getWorkmatesCollection().document(uid).get();
        }else {
            return null;
        }
    }

    // Delete the user from Firestore
    public void deleteWorkmateFromFirestore() {
        String uid = this.getCurrentWorkmateUID();
        if(uid != null) {
            this.getWorkmatesCollection().document(uid).delete();
        }
    }

}