package com.glebworx.pomodoro.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.activity.interfaces.ISplashActivity;
import com.glebworx.pomodoro.util.manager.AuthManager;
import com.glebworx.pomodoro.util.manager.DialogManager;
import com.glebworx.pomodoro.util.manager.SharedPrefsManager;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class SplashActivity extends AppCompatActivity implements ISplashActivity {

    private SpinKitView spinKitView;

    private SplashActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new SplashActivityPresenter(this);
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
        initSplashViews();
        this.spinKitView = findViewById(R.id.spin_kit_view);
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
        ExtendedFloatingActionButton sendConfirmationButton = findViewById(R.id.button_send_confirmation);
        AppCompatButton termsOfServiceButton = findViewById(R.id.button_terms_of_service);
        AppCompatButton privacyButton = findViewById(R.id.button_privacy);

        // init click listeners
        View.OnClickListener onClickListener = view -> {
            switch (view.getId()) {
                case R.id.button_send_confirmation:
                    validateAndSendSignInLink(emailInputLayout);
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
        termsOfServiceButton.setOnClickListener(onClickListener);
        privacyButton.setOnClickListener(onClickListener);

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
        positiveButton.setText(R.string.core_close);
        positiveButton.setOnClickListener(view -> alertDialog.dismiss());
    }

    private void validateAndSendSignInLink(TextInputLayout textInputLayout) {
        String email = getInput(textInputLayout.getEditText());
        if (email == null || email.isEmpty()) {
            textInputLayout.setError(getString(R.string.splash_err_email_invalid));
            return;
        }
        presenter.sendSignInLink(email, SplashActivity.this);
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
