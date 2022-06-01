package com.sadek.go4lunch.ui.workmate;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sadek.go4lunch.databinding.FragmentWorkmatesListBinding;
import com.sadek.go4lunch.model.Workmate;
import com.sadek.go4lunch.utils.WorkmateHelper;

import java.util.ArrayList;

public class WorkmateFragment extends Fragment {

    private FragmentWorkmatesListBinding binding;
    private ArrayList<Workmate> mWorkmates = new ArrayList<>();
    private WorkmateViewModel mWorkmateViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWorkmatesListBinding.inflate(inflater, container, false);
        initRecyclerView();
        displayWorkmatesList();
        return binding.getRoot();
    }


    private void displayWorkmatesList() {
        WorkmateHelper.getAllWorkmates().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Workmate workmate = document.toObject(Workmate.class);

                        if(!workmate.getId().equals(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid())) {
                            mWorkmates.add(workmate);
                        }
                    }
                    binding.listWorkmatesRecyclerview.getAdapter().notifyDataSetChanged();
                }
            }
        });
    }


    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.listWorkmatesRecyclerview.setLayoutManager(layoutManager);
        WorkmateAdapter lAdapter = new WorkmateAdapter(mWorkmates,"eating");
        // Set CustomAdapter as the adapter for RecyclerView.
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.listWorkmatesRecyclerview.getContext(),
                layoutManager.getOrientation());
        binding.listWorkmatesRecyclerview.addItemDecoration(dividerItemDecoration);
        binding.listWorkmatesRecyclerview.setAdapter(lAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}