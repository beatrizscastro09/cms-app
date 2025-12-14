package com.netflix_plus_plus.cms.models;

public class ApiResponse{
    private String message; // success message
    private String movieId; //return the movie upload
    private String error; // error message if request failed

    public ApiResponse(String message){
        this.message = message;
    }

    public String getMessage(){return message;}
    public void setMessage(String message){this.message = message;}

    public String getMovieId(){return movieId;}
    public void setMovieId(String movieId) {this.movieId = movieId;}

    public String getError() {return error;}
    public void setError(){this.error = error;}

    public boolean isSuccess(){ // see if the method was successful
        return error == null || error.isEmpty();
    }

    public String getDisplayMessage(){ // get either message or error
        if (error != null && !error.isEmpty()) return error;
        return message;
    }
}