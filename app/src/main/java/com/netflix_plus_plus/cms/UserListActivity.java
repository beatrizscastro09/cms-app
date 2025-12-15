package com.netflix_plus_plus.cms;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.netflix_plus_plus.cms.adapters.UserAdapter;
import com.netflix_plus_plus.cms.api.RetrofitClient;
import com.netflix_plus_plus.cms.models.ApiResponse;
import com.netflix_plus_plus.cms.models.User;
import com.netflix_plus_plus.cms.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListActivity extends AppCompatActivity implements UserAdapter.OnUserActionListener {

    private static final String TAG = "UserListActivity";

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private Button btnRefresh;

    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        tokenManager = new TokenManager(this);

        // Check if logged in
        if (!tokenManager.isLoggedIn()) {
            Log.i(TAG, "User is not logged in");
            navigateToLogin();
            return;
        }

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewUsers);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        btnRefresh = findViewById(R.id.btnRefresh);

        // Setup RecyclerView
        userList = new ArrayList<>();
        adapter = new UserAdapter(userList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Refresh button
        btnRefresh.setOnClickListener(v -> loadUsers());

        // Load users
        loadUsers();
    }

    private void loadUsers() {
        showLoading(true);


        String authHeader = tokenManager.getAuthHeader();


        if (authHeader == null) {
            Toast.makeText(this, "No authentication token found", Toast.LENGTH_SHORT).show();
            navigateToLogin();
            return;
        }

        Call<List<User>> call = RetrofitClient.getApiService().getAllUsers(authHeader);

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();

                    if (users.isEmpty()) {
                        showEmptyState(true);
                    } else {
                        showEmptyState(false);
                        userList.clear();
                        userList.addAll(users);
                        adapter.updateUsers(userList);
                    }
                } else {
                    Toast.makeText(UserListActivity.this,
                            "Failed to load users: " + response.message(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(UserListActivity.this,
                        "Error loading users: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDeleteUser(User user, int position) {
        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete user \"" + user.getUsername() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteUser(user, position);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteUser(User user, int position) {
        showLoading(true);

        Call<ApiResponse> call = RetrofitClient.getApiService().deleteUser(user.getId());

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    Toast.makeText(UserListActivity.this,
                            "User deleted successfully!",
                            Toast.LENGTH_SHORT).show();

                    // Remove from list
                    adapter.removeUser(position);

                    // Show empty state if list is now empty
                    if (userList.isEmpty()) {
                        showEmptyState(true);
                    }
                } else {
                    Toast.makeText(UserListActivity.this,
                            "Failed to delete user: " + response.message(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(UserListActivity.this,
                        "Error deleting user: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState(boolean show) {
        tvEmptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }


    private void navigateToLogin() {
        Intent intent = new Intent(UserListActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}