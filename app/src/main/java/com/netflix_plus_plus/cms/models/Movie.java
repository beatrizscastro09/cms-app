package com.netflix_plus_plus.cms.models;

public class Movie {
    // Core movie data
    private String id;
    private String title;
    private String description;
    private String director;
    private Integer releaseYear;
    private Integer durationMinutes;  // Backend uses this field name
    private Double rating;
    private String indicativeClassification;

    // Images
    private String coverImage;
    private String sliderImage;

    // Editorial / Home
    private Boolean featured;
    private Integer featuredOrder;

    // Empty constructor
    public Movie() {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getIndicativeClassification() {
        return indicativeClassification;
    }

    public void setIndicativeClassification(String indicativeClassification) {
        this.indicativeClassification = indicativeClassification;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getSliderImage() {
        return sliderImage;
    }

    public void setSliderImage(String sliderImage) {
        this.sliderImage = sliderImage;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public Integer getFeaturedOrder() {
        return featuredOrder;
    }

    public void setFeaturedOrder(Integer featuredOrder) {
        this.featuredOrder = featuredOrder;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", releaseYear=" + releaseYear +
                ", rating=" + rating +
                '}';
    }
}