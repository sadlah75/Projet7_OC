package com.sadek.go4lunch.ui.workmate;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sadek.go4lunch.model.Workmate;
import com.sadek.go4lunch.repository.WorkmateRepository;

import java.util.ArrayList;
import java.util.List;

public class WorkmateViewModel extends ViewModel {

    private WorkmateRepository mWorkmateRepository;
    private MutableLiveData<ArrayList<Workmate>> mMutableLiveData;

    public WorkmateViewModel() {
        this.mWorkmateRepository = WorkmateRepository.getInstance();
        mMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<Workmate>> getMutableLiveData() {
        return mMutableLiveData;
    }

    public void getAllWorkmates() {
        ArrayList<Workmate> workmates = mWorkmateRepository.getAllWorkmates();
        mMutableLiveData.setValue(workmates);
    }

}
