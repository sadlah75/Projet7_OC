package com.sadek.go4lunch.manager;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.sadek.go4lunch.repository.WorkmateRepository;


public class UserManager {

    private static volatile UserManager instance;
    private WorkmateRepository workmateRepository;

    private UserManager() {
        workmateRepository = WorkmateRepository.getInstance();
    }

    public static UserManager getInstance() {
        UserManager result = instance;
        if(result != null) {
            return result;
        }
        synchronized (WorkmateRepository.class) {
            if(instance == null) {
                instance = new UserManager();
            }
            return instance;
        }
    }

    public FirebaseUser getCurrentUser() {
        return workmateRepository.getCurrentWorkmate();
    }

    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    public Task<Void> signOut(Context context){
        return workmateRepository.signOut(context);
    }

    public void createUser() {
        workmateRepository.createWorkmate();
    }

    public Task<Void> deleteUser(Context context){
        // Delete the user account from the Auth
        return workmateRepository.deleteUser(context).addOnCompleteListener(task -> {
            // Once done, delete the user datas from Firestore
            workmateRepository.deleteWorkmateFromFirestore();
        });
    }


}
