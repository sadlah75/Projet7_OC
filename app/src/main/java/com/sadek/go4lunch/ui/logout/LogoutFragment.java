package com.sadek.go4lunch.ui.logout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.sadek.go4lunch.databinding.FragmentLogoutBinding;
import com.sadek.go4lunch.manager.UserManager;

public class LogoutFragment extends DialogFragment {

    private UserManager userManager = UserManager.getInstance();
    private FragmentLogoutBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LogoutViewModel slideshowViewModel =
                new ViewModelProvider(this).get(LogoutViewModel.class);

        binding = FragmentLogoutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        final TextView textView = binding.textLogout;
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}