package com.netflix_plus_plus.cms.api;

import com.netflix_plus_plus.cms.models.Movie;
import com.netflix_plus_plus.cms.models.User;
import com.netflix_plus_plus.cms.models.ApiResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // ==================== MOVIE ENDPOINTS ====================
    @Multipart
    @POST("movies/upload")
    Call<ApiResponse> uploadMovie(
            @Part MultipartBody.Part file,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("director") RequestBody director,
            @Part("releaseYear") RequestBody releaseYear,
            @Part("duration") RequestBody duration,
            @Part("rating") RequestBody rating,
            @Part("indicativeClassification") RequestBody classification,
            @Part("languageId") RequestBody languageId
    );

    @GET("movies")
    Call<List<Movie>> getAllMovies();

    @GET("movies/{id}")
    Call<Movie> getMovie(@Path("id") String id);

    @DELETE("movies/{id}")
    Call<ApiResponse> deleteMovie(@Path("id") String id);


    // ==================== USER ENDPOINTS ====================

    @POST("users")
    Call<ApiResponse> createUser(@Body User user);

    @GET("users")
    Call<List<User>> getAllUsers();

    @GET("users/{id}")
    Call<User> getUser(@Path("id") String id);
    
    @DELETE("users/{id}")
    Call<ApiResponse> deleteUser(@Path("id") String id);
}
