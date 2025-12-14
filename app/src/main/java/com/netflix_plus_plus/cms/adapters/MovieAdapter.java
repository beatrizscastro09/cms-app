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

        holder.tvTitle.setText(movie.getTitle());
        holder.tvDirector.setText("Director: " + movie.getDirector());
        holder.tvYear.setText(String.valueOf(movie.getReleaseYear()));
        holder.tvRating.setText("★ " + movie.getRating());
        holder.tvClassification.setText(movie.getIndicativeClassification());

        // Show description or "No description"
        if (movie.getDescription() != null && !movie.getDescription().isEmpty()) {
            holder.tvDescription.setText(movie.getDescription());
        } else {
            holder.tvDescription.setText("No description available");
        }

        // Delete button click
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteMovie(movie, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    // Remove movie from list (after successful delete)
    public void removeMovie(int position) {
        movieList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, movieList.size());
    }

    // Update entire list (after refresh)
    public void updateMovies(List<Movie> newMovies) {
        this.movieList = newMovies;
        notifyDataSetChanged();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDirector, tvYear, tvRating, tvClassification, tvDescription;
        Button btnDelete;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvDirector = itemView.findViewById(R.id.tvMovieDirector);
            tvYear = itemView.findViewById(R.id.tvMovieYear);
            tvRating = itemView.findViewById(R.id.tvMovieRating);
            tvClassification = itemView.findViewById(R.id.tvMovieClassification);
            tvDescription = itemView.findViewById(R.id.tvMovieDescription);
            btnDelete = itemView.findViewById(R.id.btnDeleteMovie);
        }
    }
}