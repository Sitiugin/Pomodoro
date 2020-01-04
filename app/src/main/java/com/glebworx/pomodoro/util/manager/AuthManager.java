package com.glebworx.pomodoro.util.manager;

import android.net.Uri;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthManager {

    private static AuthManager authManager = new AuthManager();
    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private AuthManager() { }

    public static AuthManager getInstance() {
        return authManager;
    }

    public boolean isSignedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public String getUid() {
        FirebaseUser user = getUser();
        if (user == null) {
            return null;
        }
        return user.getUid();
    }

    public String getName() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            return null;
        }
        String name = user.getDisplayName();
        if (name == null || name.isEmpty()) {
            name = user.getEmail();
        }
        return name;
    }

    public Uri getPhotoUrl() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            return null;
        }
        return user.getPhotoUrl();
    }

    public void signIn(String email, String emailLink, OnCompleteListener<AuthResult> onCompleteListener) {
        firebaseAuth.signInWithEmailLink(email, emailLink).addOnCompleteListener(onCompleteListener);
    }

    public void signOut() {
        firebaseAuth.signOut();
    }

    public void sendVerificationEmail(String email, OnCompleteListener<Void> onCompleteListener) {
        ActionCodeSettings actionCodeSettings = buildActionCodeSettings();
        firebaseAuth.sendSignInLinkToEmail(email, actionCodeSettings).addOnCompleteListener(onCompleteListener);
    }

    //                                                                                        HELPER

    private FirebaseUser getUser() {
        return firebaseAuth.getCurrentUser();
    }

    private ActionCodeSettings buildActionCodeSettings() {
        return ActionCodeSettings.newBuilder()
                .setUrl("https://pomodorosignin.page.link")
                .setHandleCodeInApp(true)
                //.setIOSBundleId("com.glebworx.ios")
                .setAndroidPackageName(
                        "com.glebworx.android",
                        true,
                        "1.0")
                .build();
    }
}
