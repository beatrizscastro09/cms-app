package com.netflix_plus_plus.cms;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.netflix_plus_plus.cms.api.RetrofitClient;
import com.netflix_plus_plus.cms.models.LoginRequest;
import com.netflix_plus_plus.cms.models.LoginResponse;
import com.netflix_plus_plus.cms.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    //private SessionManager sessionManager;

    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

/*        // Initialize session manager
        sessionManager = new SessionManager(this);

        // Check if already logged in
        if (sessionManager.isLoggedIn() && sessionManager.isAdmin()) {
            // Already logged in as admin, go to MainActivity
            navigateToMain();
            return;
        }*/

        tokenManager = new TokenManager(this);

        // Check if already logged in
        if (tokenManager.isLoggedIn()) {
            navigateToMain();
            return;
        }


        setContentView(R.layout.activity_login);

        // Initialize views
        initializeViews();

        // Set click listener
        btnLogin.setOnClickListener(v -> login());
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
    }

    private void login() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        showProgress(true);

        Log.i(TAG, "Trying to login... ");

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        LoginRequest loginRequest = new LoginRequest(email, password);

        Call<LoginResponse> call = RetrofitClient.getApiService().login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                showProgress(false);

                /*if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse.isSuccess()) {
                        // ver se o user é admin
                        if (loginResponse.isAdmin()) {
                            // guardar sessao
                            sessionManager.createLoginSession(
                                    loginResponse.getToken(),
                                    loginResponse.getUser().getId(),
                                    loginResponse.getUser().getUsername(),
                                    loginResponse.getUser().getEmail(),
                                    loginResponse.getUser().getRole()
                            );

                            Toast.makeText(LoginActivity.this,
                                    "Welcome, " + loginResponse.getUser().getUsername() + "!",
                                    Toast.LENGTH_SHORT).show();

                            // Navigate to main
                            navigateToMain();
                        } else {
                            // User is not admin
                            Toast.makeText(LoginActivity.this,
                                    "Access denied. Only administrators can access the CMS.",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Login failed: " + loginResponse.getError(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Invalid email or password";
                    if (response.code() == 401) {
                        errorMsg = "Invalid credentials";
                    }
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }*/

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    // Save token and user data
                    tokenManager.saveToken(loginResponse.getToken());
                    tokenManager.saveUserData(
                            loginResponse.getUser().getId(),
                            loginResponse.getUser().getUsername(),
                            loginResponse.getUser().getEmail()
                    );

                    Log.i(TAG, "Login successful. Token: " + loginResponse.getToken());
                    Toast.makeText(LoginActivity.this,
                            "Welcome " + loginResponse.getUser().getUsername(),
                            Toast.LENGTH_SHORT).show();

                    navigateToMain();
                } else {
                    Log.e(TAG, "Login failed: " + response.code());
                    Toast.makeText(LoginActivity.this,
                            "Invalid email or password",
                            Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showProgress(false);
                Toast.makeText(LoginActivity.this,
                        "Login error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInputs() {
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return false;
        }

        String password = etPassword.getText().toString().trim();
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
        etEmail.setEnabled(!show);
        etPassword.setEnabled(!show);
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Prevent going back from login screen
        // User must login to access the app
        super.onBackPressed();
        finishAffinity(); // Close the app
    }
}