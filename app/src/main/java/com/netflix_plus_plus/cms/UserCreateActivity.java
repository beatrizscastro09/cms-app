package com.netflix_plus_plus.cms;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.netflix_plus_plus.cms.api.RetrofitClient;
import com.netflix_plus_plus.cms.models.ApiResponse;
import com.netflix_plus_plus.cms.models.User;
import com.netflix_plus_plus.cms.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserCreateActivity extends AppCompatActivity {
    private static final String TAG = "UserCreateActivity";

    private EditText etUsername, etEmail, etPassword;
    private Button btnCreateUser;
    private ProgressBar progressBar;
    private TextView tvStatus;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_create);

        tokenManager = new TokenManager(this);

        // Check if logged in
        if (!tokenManager.isLoggedIn()) {
            Log.i(TAG, "User is not logged in");
            navigateToLogin();
            return;
        }

        // Initialize views
        initializeViews();

        // Set click listener
        btnCreateUser.setOnClickListener(v -> createUser());
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnCreateUser = findViewById(R.id.btnCreateUser);
        progressBar = findViewById(R.id.progressBar);
        tvStatus = findViewById(R.id.tvStatus);
    }

    private void createUser() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        // Show progress
        showProgress(true);
        tvStatus.setText("Creating user...");

        // Get form data
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Create user object
        User user = new User(username, email, password);

        String authHeader = tokenManager.getAuthHeader();

        if (authHeader == null) {
            Toast.makeText(this, "No authentication token found", Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }

        // Make API call
        Call<ApiResponse> call = RetrofitClient.getApiService().createUser(authHeader, user);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                showProgress(false);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(UserCreateActivity.this,
                            "User created successfully!",
                            Toast.LENGTH_LONG).show();

                    // Clear form
                    clearForm();

                    // finish();
                } else {
                    String errorMessage = "Failed to create user";
                    if (response.code() == 400) {
                        errorMessage = "Invalid user data. Please check your inputs.";
                    } else if (response.code() == 409) {
                        errorMessage = "User already exists with this email or username.";
                    }
                    Toast.makeText(UserCreateActivity.this,
                            errorMessage,
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showProgress(false);
                Toast.makeText(UserCreateActivity.this,
                        "Error creating user: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInputs() {
        // Validate username
        String username = etUsername.getText().toString().trim();
        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return false;
        }
        if (username.length() < 3) {
            etUsername.setError("Username must be at least 3 characters");
            etUsername.requestFocus();
            return false;
        }

        // Validate email
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return false;
        }

        // Validate password
        String password = etPassword.getText().toString().trim();
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void clearForm() {
        etUsername.setText("");
        etEmail.setText("");
        etPassword.setText("");
        etUsername.requestFocus();
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        tvStatus.setVisibility(show ? View.VISIBLE : View.GONE);
        btnCreateUser.setEnabled(!show);
        etUsername.setEnabled(!show);
        etEmail.setEnabled(!show);
        etPassword.setEnabled(!show);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(UserCreateActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}