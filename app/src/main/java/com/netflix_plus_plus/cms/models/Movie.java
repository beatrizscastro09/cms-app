package com.netflix_plus_plus.cms.models;

import java.util.UUID;

public class Movie {
    private String id;
    private String title;
    private String description;
    private String director;
    private int releaseYear;
    private int durationMinutes;
    private double rating;
    private String indicativeClassification;
    private String coverImage;

    public Movie() {
        this.id = UUID.randomUUID().toString();
        this.rating = 5.0; // default
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public int getReleaseYear() { return releaseYear; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getIndicativeClassification() { return indicativeClassification; }
    public void setIndicativeClassification(String indicativeClassification) { this.indicativeClassification = indicativeClassification; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
}