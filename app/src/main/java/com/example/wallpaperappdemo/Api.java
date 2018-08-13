package com.example.wallpaperappdemo;

import com.example.wallpaperappdemo.Classes.Response;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {
    @GET(Constants.IMAGES)
    Call<Response> getImages(@Query("key") String key, @Query("image_type") String imageType, @Query("per_page") int count, @Query("page") int page, @Query("q") String query);
}
