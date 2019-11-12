package com.glebworx.pomodoro.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.activity.interfaces.ISplashActivity;
import com.glebworx.pomodoro.ui.activity.interfaces.ISplashActivityPresenter;
import com.glebworx.pomodoro.util.manager.AuthManager;
import com.glebworx.pomodoro.util.manager.SharedPrefsManager;

public class SplashActivityPresenter implements ISplashActivityPresenter {

    private ISplashActivity presenterListener;
    private boolean isSplashLayoutInflated;

    public SplashActivityPresenter(ISplashActivity presenterListener) {
        this.presenterListener = presenterListener;
        init();
    }

    @Override
    public void init() {
        isSplashLayoutInflated = false;
    }


    @Override
    public void handleIntent(Intent intent, Context context) {
        if (AuthManager.getInstance().isSignedIn()) { // already signed in, start Main Activity
            presenterListener.onStartMainActivity();
        } else { // not signed in, check for dynamic link
            if (intent == null) {
                isSplashLayoutInflated = true;
                presenterListener.onInflateSplashLayout();
                return;
            }
            Uri uri = intent.getData();
            if (uri == null) {
                isSplashLayoutInflated = true;
                presenterListener.onInflateSplashLayout();
                return;
            }
            String dynamicLink = uri.toString();
            SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(context);
            String email = sharedPrefsManager.getEmail();
            if (email == null) {
                isSplashLayoutInflated = true;
                presenterListener.onInflateSplashLayout();
                return;
            }
            signInAndStartMainActivity(email, dynamicLink, context); // dynamic link present
        }
    }

    @Override
    public void sendSignInLink(String email, Context context) {

        presenterListener.onShowSpinKit();

        AuthManager authManager = AuthManager.getInstance();
        authManager.sendVerificationEmail(email, task -> {
            presenterListener.onHideSpinKit();
            if (task.isSuccessful()) {
                SharedPrefsManager sharedPrefsManager = new SharedPrefsManager(context);
                sharedPrefsManager.setEmail(email);
                Toast.makeText(context, context.getString(R.string.splash_toast_confirmation_email_sent_success, email), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, context.getString(R.string.splash_toast_confirmation_email_sent_failed, email), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void signInAndStartMainActivity(@NonNull String email, @NonNull String emailLink, Context context) {
        Toast.makeText(context, context.getString(R.string.splash_toast_signing_in), Toast.LENGTH_SHORT).show();
        presenterListener.onShowSpinKit();
        AuthManager.getInstance().signIn(email, emailLink, task -> {
            if (task.isSuccessful()) {
                presenterListener.onStartMainActivity();
            } else {
                Toast.makeText(context, context.getString(R.string.splash_toast_sign_in_failed), Toast.LENGTH_LONG).show();
                presenterListener.onHideSpinKit();
                if (!isSplashLayoutInflated) {
                    isSplashLayoutInflated = true;
                    presenterListener.onInflateSplashLayout();
                }
            }
        });
    }
}
