package com.netflix_plus_plus.cms;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.netflix_plus_plus.cms.adapters.MovieAdapter;
import com.netflix_plus_plus.cms.api.RetrofitClient;
import com.netflix_plus_plus.cms.models.ApiResponse;
import com.netflix_plus_plus.cms.models.Movie;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListActivity extends AppCompatActivity implements MovieAdapter.OnMovieActionListener {

    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private List<Movie> movieList;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private Button btnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewMovies);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        btnRefresh = findViewById(R.id.btnRefresh);

        // Setup RecyclerView
        movieList = new ArrayList<>();
        adapter = new MovieAdapter(movieList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Refresh button
        btnRefresh.setOnClickListener(v -> loadMovies());

        // Load movies
        loadMovies();
    }

    private void loadMovies() {
        showLoading(true);

        Call<List<Movie>> call = RetrofitClient.getApiService().getAllMovies();

        call.enqueue(new Callback<List<Movie>>() {
            @Override
            public void onResponse(Call<List<Movie>> call, Response<List<Movie>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body();

                    if (movies.isEmpty()) {
                        showEmptyState(true);
                    } else {
                        showEmptyState(false);
                        movieList.clear();
                        movieList.addAll(movies);
                        adapter.updateMovies(movieList);
                    }
                } else {
                    Toast.makeText(MovieListActivity.this,
                            "Failed to load movies: " + response.message(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Movie>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(MovieListActivity.this,
                        "Error loading movies: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDeleteMovie(Movie movie, int position) {
        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Delete Movie")
                .setMessage("Are you sure you want to delete \"" + movie.getTitle() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteMovie(movie, position);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteMovie(Movie movie, int position) {
        showLoading(true);

        Call<ApiResponse> call = RetrofitClient.getApiService().deleteMovie(movie.getId());

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                showLoading(false);

                if (response.isSuccessful()) {
                    Toast.makeText(MovieListActivity.this,
                            "Movie deleted successfully!",
                            Toast.LENGTH_SHORT).show();

                    // Remove from list
                    adapter.removeMovie(position);

                    // Show empty state if list is now empty
                    if (movieList.isEmpty()) {
                        showEmptyState(true);
                    }
                } else {
                    Toast.makeText(MovieListActivity.this,
                            "Failed to delete movie: " + response.message(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(MovieListActivity.this,
                        "Error deleting movie: " + t.getMessage(),
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
}