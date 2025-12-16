package com.netflix_plus_plus.cms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.netflix_plus_plus.cms.utils.TokenManager;

public class MainActivity extends AppCompatActivity {
    private Button buttonUploadMovie;
    private Button buttonViewMovies;
    private Button buttonCreateUser;
    private Button buttonViewUsers;
    private Button buttonLogout;
    private TokenManager tokenManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tokenManager = new TokenManager(this);

        if (!tokenManager.isLoggedIn()){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // inicializar os botoes
        buttonUploadMovie = findViewById(R.id.buttonUploadMovie);
        buttonViewMovies = findViewById(R.id.buttonViewMovies);
        buttonCreateUser = findViewById(R.id.buttonCreateUser);
        buttonViewUsers = findViewById(R.id.buttonViewUsers);
        buttonLogout = findViewById(R.id.buttonLogout);

        // chama a funcao dos botoes
        setClickListeners();
    }

    private void setClickListeners() {
        buttonUploadMovie.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MovieUploadActivity.class);
            startActivity(intent);
        });

        buttonViewMovies.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MovieListActivity.class);
            startActivity(intent);
        });

        buttonCreateUser.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserCreateActivity.class);
            startActivity(intent);
        });

        buttonViewUsers.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserListActivity.class);
            startActivity(intent);
        });

        buttonLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    tokenManager.clearToken();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}