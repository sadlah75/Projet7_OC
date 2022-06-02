package com.sadek.go4lunch.ui.lunch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.sadek.go4lunch.databinding.FragmentYourLunchBinding;
import com.sadek.go4lunch.model.Workmate;
import com.sadek.go4lunch.utils.WorkmateHelper;

public class YourLunchFragment extends Fragment {

    private FragmentYourLunchBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentYourLunchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLunch();
    }

    private void setLunch() {
        String uid = WorkmateHelper.getCurrentWorkmate().getUid();
        WorkmateHelper.getWorkmateByUID(uid).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Workmate workmate = task.getResult().toObject(Workmate.class);
                    binding.textLunch.setText(workmate.getChosenRestaurant());
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}