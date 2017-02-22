package com.example.portable.firebasetests.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.portable.firebasetests.MySharedPreferences;
import com.example.portable.firebasetests.R;
import com.example.portable.firebasetests.listeners.OnLoginListener;
import com.example.portable.firebasetests.tasks.LoginTask;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class LoginActivity extends AppCompatActivity {
    private GoogleApiClient mGoogleApiClient;
    private Button loginButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClick();
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.loginProgressBar);
        String idToken = MySharedPreferences.readTokenId(this);
        if (idToken != null) {
            firebaseAuthWithGoogle(idToken);
        } else {
            loginWithGoogle();
        }
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

    }

    private void loginClick() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 18);
    }

    private void startTasksActivity() {
        startActivity(new Intent(this, TasksActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 18) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                if (account != null && account.getId() != null) {
                    MySharedPreferences.writeUserId(this, account.getId());
                    MySharedPreferences.writeTokenId(this, account.getIdToken());
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(final String tokenId) {
        LoginTask task = new LoginTask(this, tokenId);
        setButtonVisibility(false);
        task.setOnLoginListener(new OnLoginListener() {
            @Override
            public void onLogin(int resultCode) {
                if (resultCode == LoginTask.DONE) {
                    startTasksActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    setButtonVisibility(true);
                }
            }
        });
        task.execute();
    }

    private void setButtonVisibility(boolean b) {
        if (b) {
            loginButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            loginButton.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

}
