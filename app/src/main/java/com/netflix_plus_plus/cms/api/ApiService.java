package com.netflix_plus_plus.cms.api;

import com.netflix_plus_plus.cms.models.ApiResponse;
import com.netflix_plus_plus.cms.models.LoginRequest;
import com.netflix_plus_plus.cms.models.LoginResponse;
import com.netflix_plus_plus.cms.models.Movie;
import com.netflix_plus_plus.cms.models.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // ===== AUTHENTICATION =====
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    // ===== MOVIES =====
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
            @Part("languageId") RequestBody languageId,
            @Part MultipartBody.Part coverImage,
            @Part MultipartBody.Part sliderImage
    );

    @GET("movies")
    Call<List<Movie>> getAllMovies();

    @DELETE("movies/{id}")
    Call<ApiResponse> deleteMovie(@Path("id") String id);

    // ===== USERS =====
    @POST("users")
    Call<ApiResponse> createUser(@Body User user);

    @GET("users")
    Call<List<User>> getAllUsers(@Header("Authorization") String authorization);

    @DELETE("users/{id}")
    Call<ApiResponse> deleteUser(@Path("id") String id);
}