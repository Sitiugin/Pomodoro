package com.glebworx.pomodoro.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.github.ybq.android.spinkit.SpinKitView;
import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.util.manager.AuthManager;
import com.glebworx.pomodoro.util.manager.SharedPrefsManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class SplashActivity extends AppCompatActivity {

    private SpinKitView spinKitView;

    private boolean isSplashLayoutInflated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isSplashLayoutInflated = false;

        // listen for dynamic link
        if (AuthManager.getInstance().isSignedIn()) { // already signed in, start Main Activity
            startMainActivity();
        } else { // not signed in, check for dynamic link
            handleIntent(getIntent());
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent == null) {
            inflateSplashLayout();
            return;
        }
        Uri uri = intent.getData();
        if (uri == null) {
            inflateSplashLayout();
            return;
        }
        String dynamicLink = uri.toString();
        SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(SplashActivity.this);
        String email = sharedPrefsManager.getEmail();
        if (email == null) {
            inflateSplashLayout();
            return;
        }
        signInAndStartMainActivity(email, dynamicLink); // dynamic link present
    }

    private void startMainActivity() {
        String name = AuthManager.getInstance().getName();
        String message = name == null
                ? getString(R.string.splash_toast_sign_in_success)
                : getString(R.string.splash_toast_signed_in_as, name);
        Toast.makeText(SplashActivity.this, message, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finishAffinity();
    }

    private void inflateSplashLayout() {

        isSplashLayoutInflated = true;

        setContentView(R.layout.activity_splash);
        initSplashViews();

        this.spinKitView = findViewById(R.id.spin_kit_view);

    }

    private void initSplashViews() {

        // init interactive views
        TextInputLayout emailInputLayout = findViewById(R.id.layout_email_input);
        TextInputEditText emailEditText = findViewById(R.id.edit_text_email);
        ExtendedFloatingActionButton signInButton = findViewById(R.id.button_sign_in);
        AppCompatButton termsOfServiceButton = findViewById(R.id.button_terms_of_service);
        AppCompatButton privacyButton = findViewById(R.id.button_privacy);

        // init click listeners
        View.OnClickListener onClickListener = view -> {
            switch (view.getId()) {
                case R.id.button_sign_in:
                    validateAndSendSignInLink(emailInputLayout);
                    break;
                case R.id.button_terms_of_service:
                    // TODO implement
                    break;
                case R.id.button_privacy:
                    // TODO implement
                    break;
            }
        };
        signInButton.setOnClickListener(onClickListener);
        termsOfServiceButton.setOnClickListener(onClickListener);
        privacyButton.setOnClickListener(onClickListener);

        // if signed in before, show email previously used for sign in
        SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(SplashActivity.this);
        String email = sharedPrefsManager.getEmail();
        if (email != null) {
            emailEditText.setText(email);
        }

    }

    private void validateAndSendSignInLink(TextInputLayout textInputLayout) {
        String email = getInput(textInputLayout.getEditText());
        if (email == null || email.isEmpty()) {
            textInputLayout.setError(getString(R.string.splash_err_email_invalid));
            return;
        }
        sendSignInLink(email);
    }

    private String getInput(@Nullable EditText editText) {
        if (editText == null) {
            return null;
        }
        Editable editable = editText.getText();
        if (editable == null) {
            return null;
        }
        return editable.toString();
    }

    private void sendSignInLink(String email) {

        showSpinKit();

        AuthManager authManager = AuthManager.getInstance();
        authManager.sendVerificationEmail(email, task -> {
            hideSpinKit();
            if (task.isSuccessful()) {
                SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(SplashActivity.this);
                sharedPrefsManager.setEmail(email);
                Toast.makeText(SplashActivity.this, getString(R.string.splash_toast_confirmation_email_sent_success, email), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SplashActivity.this, getString(R.string.splash_toast_confirmation_email_sent_failed, email), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void showSpinKit() {
        if (spinKitView != null && !spinKitView.isShown()) {
            spinKitView.setVisibility(View.VISIBLE);
        }
    }

    private void hideSpinKit() {
        if (spinKitView != null && spinKitView.isShown()) {
            spinKitView.setVisibility(View.INVISIBLE);
        }
    }

    private void signInAndStartMainActivity(@NonNull String email, @NonNull String emailLink) {
        Toast.makeText(SplashActivity.this, getString(R.string.splash_toast_signing_in), Toast.LENGTH_SHORT).show();
        showSpinKit();
        AuthManager.getInstance().signIn(email, emailLink, task -> {
            if (task.isSuccessful()) {
                startMainActivity();
            } else {
                Toast.makeText(SplashActivity.this, getString(R.string.splash_toast_sign_in_failed), Toast.LENGTH_LONG).show();
                hideSpinKit();
                if (!isSplashLayoutInflated) {
                    inflateSplashLayout();
                }
            }
        });
    }

}
