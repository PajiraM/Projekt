package com.example.newsapi.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsApiService {
    @GET("everything")
    Call<NewsResponse> getNews(
            @Query("q") String query,
            @Query("apiKey") String apiKey,
            @Query("language") String language
    );

    @GET("top-headlines")
    Call<NewsResponse> getNewsByCategory(
            @Query("category") String category,
            @Query("country") String country,
            @Query("apiKey") String apiKey
    );

    @GET("everything")
    Call<NewsResponse> getAdvancedNews(
            @Query("q") String query,
            @Query("from") String fromDate,
            @Query("to") String toDate,
            @Query("language") String language,
            @Query("sortBy") String sortBy,
            @Query("apiKey") String apiKey
    );
}