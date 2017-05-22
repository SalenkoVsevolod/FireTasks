package com.example.portable.firebasetests.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.core.FireTasksApp;
import com.example.portable.firebasetests.core.Preferences;
import com.example.portable.firebasetests.network.FirebaseReferenceManager;
import com.example.portable.firebasetests.network.listeners.FirebaseLoginListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

public class LoginActivity extends BaseActivity {
    private GoogleApiClient mGoogleApiClient;

    public static boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) FireTasksApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnectedOrConnecting());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.login_imv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    loginClick();
                } else {
                    showToast(getString(R.string.internet_connection_error), true);
                }
            }
        });
        loginWithGoogle();
    }

    private void loginWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, null)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        OptionalPendingResult<GoogleSignInResult> pendingResult =
                Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (pendingResult.isDone()) {
            login(pendingResult.get().getSignInAccount());
        } else {
            pendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult result) {
                    login(result.getSignInAccount());
                }
            });
        }
    }

    private void loginClick() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 18);
    }

    private void startTasksActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 18) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                login(result.getSignInAccount());
            }
        }
    }

    private void login(GoogleSignInAccount account) {
        if (account != null && account.getId() != null) {
            Preferences.getInstance().writeUserId(account.getId());
            FirebaseReferenceManager.getInstance().refresh();
            firebaseAuthWithGoogle(account.getIdToken());
        }
    }

    private void firebaseAuthWithGoogle(final String tokenId) {
        if (isOnline()) {
            FirebaseLoginListener task = new FirebaseLoginListener(this, tokenId);
            task.setOnLoginListener(new OnLoginListener() {
                @Override
                public void onLogin(int resultCode) {
                    if (resultCode == FirebaseLoginListener.DONE) {
                        startTasksActivity();
                    } else {
                        showToast(getString(R.string.authentication_failed), true);
                    }
                }
            });
            task.execute();
        } else {
            showToast(getString(R.string.internet_connection_error), true);
        }
    }

    public interface OnLoginListener {
        void onLogin(int resultCode);
    }
}
