package com.sadek.go4lunch.ui.lunch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class YourLunchViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public YourLunchViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Choix du restaurant");
    }

    public LiveData<String> getText() {
        return mText;
    }
}