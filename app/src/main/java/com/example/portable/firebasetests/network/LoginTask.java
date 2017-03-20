package com.example.portable.firebasetests.utils;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.portable.firebasetests.ui.activities.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Created by Salenko Vsevolod on 22.02.2017.
 */

public class LoginTask extends AsyncTask<Void, Void, Void> {
    public static final int DONE = 0;
    private LoginActivity.OnLoginListener onLoginListener;
    private FirebaseAuth mAuth;
    private String tokenId;
    private Activity activity;
    private int resultCode;

    public LoginTask(Activity activity, String tokenId) {
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        this.tokenId = tokenId;
    }

    @Override
    protected Void doInBackground(Void... params) {
        AuthCredential credential = GoogleAuthProvider.getCredential(tokenId, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            resultCode = DONE;
                        } else {
                            resultCode = 1;
                        }
                    }
                });
        onLoginListener.onLogin(resultCode);
        return null;
    }

    public void setOnLoginListener(LoginActivity.OnLoginListener onLoginListener) {
        this.onLoginListener = onLoginListener;
    }
}