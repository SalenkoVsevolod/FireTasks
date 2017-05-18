package com.example.portable.firebasetests.network.listeners;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

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

public class FirebaseLoginListener extends AsyncTask<Void, Void, Void> {
    public static final int DONE = 0;
    private LoginActivity.OnLoginListener mLoginListener;
    private FirebaseAuth mAuth;
    private String mTokenId;
    private Activity mActivity;
    private int mResultCode;

    public FirebaseLoginListener(Activity activity, String mTokenId) {
        this.mActivity = activity;
        mAuth = FirebaseAuth.getInstance();
        this.mTokenId = mTokenId;
    }

    @Override
    protected Void doInBackground(Void... params) {
        AuthCredential credential = GoogleAuthProvider.getCredential(mTokenId, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mResultCode = DONE;
                        } else {
                            mResultCode = 1;
                        }
                    }
                });
        mLoginListener.onLogin(mResultCode);
        return null;
    }

    public void setOnLoginListener(LoginActivity.OnLoginListener onLoginListener) {
        this.mLoginListener = onLoginListener;
    }
}