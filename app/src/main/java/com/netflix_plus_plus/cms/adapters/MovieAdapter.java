package com.netflix_plus_plus.cms.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.netflix_plus_plus.cms.R;
import com.netflix_plus_plus.cms.models.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private OnMovieActionListener listener;

    public interface OnMovieActionListener {
        void onDeleteMovie(Movie movie, int position);
    }

    public MovieAdapter(List<Movie> movieList, OnMovieActionListener listener) {
        this.movieList = movieList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        // Title
        holder.tvMovieTitle.setText(movie.getTitle());

        // Description
        if (movie.getDescription() != null && !movie.getDescription().isEmpty()) {
            holder.tvMovieDescription.setText(movie.getDescription());
        } else {
            holder.tvMovieDescription.setText("No description available");
        }

        // Year, Duration, and Rating in one line
        StringBuilder info = new StringBuilder();

        if (movie.getReleaseYear() != null) {
            info.append(movie.getReleaseYear());
        }

        if (movie.getDurationMinutes() != null) {
            if (info.length() > 0) info.append(" • ");
            info.append(movie.getDurationMinutes()).append(" min");
        }

        if (movie.getRating() != null) {
            if (info.length() > 0) info.append(" • ");
            info.append(String.format("⭐ %.1f", movie.getRating()));
        }

        holder.tvInfo.setText(info.toString());

        // Director
        if (movie.getDirector() != null && !movie.getDirector().isEmpty()) {
            holder.tvMovieDirector.setText("Director: " + movie.getDirector());
            holder.tvMovieDirector.setVisibility(View.VISIBLE);
        } else {
            holder.tvMovieDirector.setVisibility(View.GONE);
        }

        // Delete button
        holder.btnDeleteMovie.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteMovie(movie, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    // Remove movie from list after delete
    public void removeMovie(int position) {
        movieList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, movieList.size());
    }

    // Update list after refresh
    public void updateMovies(List<Movie> newMovies) {
        this.movieList = newMovies;
        notifyDataSetChanged();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieTitle, tvMovieDescription, tvMovieDirector, tvInfo;
        Button btnDeleteMovie;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvMovieDescription = itemView.findViewById(R.id.tvMovieDescription);
            tvMovieDirector = itemView.findViewById(R.id.tvMovieDirector);
            tvInfo = itemView.findViewById(R.id.tvInfo);
            btnDeleteMovie = itemView.findViewById(R.id.btnDeleteMovie);
        }
    }
}