package com.netflix_plus_plus.cms;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button buttonUploadMovie;
    private Button buttonViewMovies;
    private Button buttonCreateUser;
    private Button buttonViewUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // inicializar os botoes
        buttonUploadMovie = findViewById(R.id.buttonUploadMovie);
        buttonViewMovies = findViewById(R.id.buttonViewMovies);
        buttonCreateUser = findViewById(R.id.buttonCreateUser);
        buttonViewUsers = findViewById(R.id.buttonViewUsers);

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
    }
}