package com.glebworx.pomodoro.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.TransitionManager;

import com.github.ybq.android.spinkit.SpinKitView;
import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.activity.interfaces.ISplashActivity;
import com.glebworx.pomodoro.util.manager.AuthManager;
import com.glebworx.pomodoro.util.manager.DialogManager;
import com.glebworx.pomodoro.util.manager.SharedPrefsManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;


public class SplashActivity extends AppCompatActivity implements ISplashActivity {

    private static final String REGEX_EMAIL = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

    private ConstraintLayout rootView;
    private ExtendedFloatingActionButton sendConfirmationButton;
    private ExtendedFloatingActionButton openEmailButton;
    private AppCompatButton sendAgainButton;
    private SpinKitView spinKitView;
    private ConstraintSet constraintSet;
    private SplashActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new SplashActivityPresenter(this);
        constraintSet = new ConstraintSet();
        presenter.handleIntent(getIntent(), SplashActivity.this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        presenter.handleIntent(intent, SplashActivity.this);
    }

    @Override
    public void onStartMainActivity() {
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

    @Override
    public void onInflateSplashLayout() {
        setContentView(R.layout.activity_splash);
        rootView = findViewById(R.id.container_splash);
        sendConfirmationButton = findViewById(R.id.button_send_confirmation);
        openEmailButton = findViewById(R.id.button_open_email);
        sendAgainButton = findViewById(R.id.button_send_again);
        spinKitView = findViewById(R.id.spin_kit_view);
        initSplashViews();
    }

    @Override
    public void onHideViews() {

        if (rootView == null) {
            return;
        }

        TransitionManager.endTransitions(rootView);
        TransitionManager.beginDelayedTransition(rootView);
        constraintSet.clone(rootView);

        constraintSet.setVisibility(R.id.button_open_email, ConstraintSet.GONE);
        constraintSet.setVisibility(R.id.button_send_again, ConstraintSet.GONE);
        constraintSet.setVisibility(R.id.button_send_confirmation, ConstraintSet.GONE);

        constraintSet.applyTo(rootView);

    }

    @Override
    public void onShowSendConfirmationViews() {

        if (rootView == null) {
            return;
        }

        TransitionManager.endTransitions(rootView);
        TransitionManager.beginDelayedTransition(rootView);
        constraintSet.clone(rootView);

        constraintSet.setVisibility(R.id.button_open_email, ConstraintSet.GONE);
        constraintSet.setVisibility(R.id.button_send_again, ConstraintSet.GONE);
        constraintSet.setVisibility(R.id.button_send_confirmation, ConstraintSet.VISIBLE);

        constraintSet.applyTo(rootView);

    }

    @Override
    public void onShowOpenEmailViews() {

        if (rootView == null) {
            return;
        }

        TransitionManager.endTransitions(rootView);
        TransitionManager.beginDelayedTransition(rootView);
        constraintSet.clone(rootView);

        constraintSet.setVisibility(R.id.button_send_confirmation, ConstraintSet.GONE);
        constraintSet.setVisibility(R.id.button_open_email, ConstraintSet.VISIBLE);
        constraintSet.setVisibility(R.id.button_send_again, ConstraintSet.VISIBLE);

        constraintSet.applyTo(rootView);

    }

    @Override
    public void onShowSpinKit() {
        if (spinKitView != null && !spinKitView.isShown()) {
            spinKitView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onHideSpinKit() {
        if (spinKitView != null && spinKitView.isShown()) {
            spinKitView.setVisibility(View.INVISIBLE);
        }
    }

    private void initSplashViews() {

        // init interactive views
        TextInputLayout emailInputLayout = findViewById(R.id.layout_email_input);
        TextInputEditText emailEditText = findViewById(R.id.edit_text_email);
        AppCompatButton termsOfServiceButton = findViewById(R.id.button_terms_of_service);
        AppCompatButton privacyButton = findViewById(R.id.button_privacy);

        // init click listeners
        View.OnClickListener onClickListener = view -> {
            switch (view.getId()) {
                case R.id.button_send_confirmation:
                    validateAndSendSignInLink(emailInputLayout);
                    break;
                case R.id.button_open_email:
                    presenter.openEmailClient(SplashActivity.this);
                    break;
                case R.id.button_send_again:
                    validateAndSendAgainSignInLink(emailInputLayout);
                    break;
                case R.id.button_terms_of_service:
                    showInfoDialog(R.layout.dialog_terms, R.string.splash_title_terms);
                    break;
                case R.id.button_privacy:
                    showInfoDialog(R.layout.dialog_privacy, R.string.splash_title_privacy);
                    break;
            }
        };

        sendConfirmationButton.setOnClickListener(onClickListener);
        openEmailButton.setOnClickListener(onClickListener);
        sendAgainButton.setOnClickListener(onClickListener);
        termsOfServiceButton.setOnClickListener(onClickListener);
        privacyButton.setOnClickListener(onClickListener);

        // init edit text listener
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (sendConfirmationButton.getVisibility() == View.GONE) {
                    onShowSendConfirmationViews();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // if signed in before, show email previously used for sign in
        SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(SplashActivity.this);
        String email = sharedPrefsManager.getEmail();
        if (email != null) {
            emailEditText.setText(email);
        }

    }

    private void showInfoDialog(int layoutId, int titleId) {
        AlertDialog alertDialog = DialogManager.showDialog(SplashActivity.this, R.id.container_splash, layoutId);
        ((AppCompatTextView) alertDialog.findViewById(R.id.text_view_title)).setText(titleId);
        AppCompatButton positiveButton = alertDialog.findViewById(R.id.button_positive);
        Objects.requireNonNull(positiveButton).setText(R.string.core_close);
        positiveButton.setOnClickListener(view -> alertDialog.dismiss());
    }

    private boolean validateEmail(TextInputLayout textInputLayout, String email) {
        if (email == null || email.isEmpty()) {
            textInputLayout.setError(getString(R.string.splash_err_email_empty));
            return false;
        }
        if (!email.matches(REGEX_EMAIL)) {
            textInputLayout.setError(getString(R.string.splash_err_email_invalid));
            return false;
        }
        return true;
    }

    private void validateAndSendSignInLink(TextInputLayout textInputLayout) {
        String email = getInput(textInputLayout.getEditText());
        if (validateEmail(textInputLayout, email)) {
            presenter.sendSignInLink(email, false, SplashActivity.this);
        }
    }

    private void validateAndSendAgainSignInLink(TextInputLayout textInputLayout) {
        String email = getInput(textInputLayout.getEditText());
        if (validateEmail(textInputLayout, email)) {
            presenter.sendSignInLink(email, true, SplashActivity.this);
        }
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

}
