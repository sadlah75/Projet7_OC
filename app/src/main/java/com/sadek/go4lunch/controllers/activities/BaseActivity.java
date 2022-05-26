package com.sadek.go4lunch.controllers.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.sadek.go4lunch.R;


/**
 * Base Activity class that allow to manage all the common code for the activities
 * @param <T> Should be the type of the viewBinding of your activity
 */
public abstract class BaseActivity<T extends ViewBinding> extends AppCompatActivity {
    abstract T getViewBinding();
    protected T binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
    }

    /**
     * Initialise the binding object and the layout of the activity
     */
    private void initBinding() {
        binding = getViewBinding();
        setContentView(binding.getRoot());
    }
}
