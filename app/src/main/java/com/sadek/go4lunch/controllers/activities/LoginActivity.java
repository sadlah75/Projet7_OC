package com.sadek.go4lunch.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.sadek.go4lunch.R;
import com.sadek.go4lunch.databinding.ActivityLoginBinding;
import com.sadek.go4lunch.manager.UserManager;

import java.util.Collections;
import java.util.List;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

    private static final int RC_SIGN_IN = 123;
    private final UserManager userManager = UserManager.getInstance();

    @Override
    ActivityLoginBinding getViewBinding() {
        return ActivityLoginBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupListeners();
    }

    private void setupListeners() {
        binding.activityLoginGoogle.setOnClickListener(view -> startSignInActivity(
                new AuthUI.IdpConfig.GoogleBuilder().build()
        ));
        binding.activityLoginFacebook.setOnClickListener(view -> startSignInActivity(
                new AuthUI.IdpConfig.FacebookBuilder().build()
        ));
    }

    private void startSignInActivity(AuthUI.IdpConfig idpConfig) {
        binding.loginActivityProgressbar.setVisibility(View.VISIBLE);
        List<AuthUI.IdpConfig> providers = Collections.singletonList(idpConfig);
        
        //Launch the activity
        startActivityForResult(
                AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false,true)
                .build(),RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            this.handleResponseAfterSignIn(resultCode,data);
        }

    }

    protected void handleResponseAfterSignIn(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (resultCode == RESULT_OK) {
            userManager.createUser();
            binding.loginActivityProgressbar.setVisibility(View.GONE);
            HomeActivity.navigate(this);
        } else {
            // ERRORS
            if (response == null) {
                binding.loginActivityProgressbar.setVisibility(View.GONE);
                showSnackBar(getString(R.string.error_authentication_canceled));
            } else if (response.getError()!= null) {
                if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                    binding.loginActivityProgressbar.setVisibility(View.GONE);
                    showSnackBar(getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    binding.loginActivityProgressbar.setVisibility(View.GONE);
                    showSnackBar(getString(R.string.error_unknown_error));
                }
            }
        }
    }

    private void showSnackBar(String message) {
        Snackbar.make(binding.activityLoginContainer,message,Snackbar.LENGTH_LONG).show();
    }

    public static void navigate(Context context) {
        Intent intent = new Intent(context,LoginActivity.class);
        ActivityCompat.startActivity(context,intent,null);
    }
}