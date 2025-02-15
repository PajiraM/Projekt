package com.example.newsapi.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newsapi.R;
import com.example.newsapi.adapter.NewsAdapter;
import com.example.newsapi.api.NewsApiClient;
import com.example.newsapi.api.NewsApiService;
import com.example.newsapi.api.NewsResponse;
import com.example.newsapi.model.News;
import com.example.newsapi.util.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

public class NewsFragment extends Fragment {
    private static final String ARG_CATEGORY = "category";
    private RecyclerView newsRecyclerView;
    private NewsAdapter newsAdapter;
    private NewsApiService newsApiService;
    private String category;

    private static long lastApiCall = 0;
    private static final long API_CALL_DELAY = 1000;

    public static NewsFragment newInstance(String category) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
            Log.d("NewsFragment", "Fragment kreiran s kategorijom: " + category);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        newsRecyclerView = view.findViewById(R.id.newsRecyclerView);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        newsAdapter = new NewsAdapter(getContext());
        newsRecyclerView.setAdapter(newsAdapter);

        newsApiService = NewsApiClient.getInstance().getNewsApiService();
        Log.d("NewsFragment", "Učitavam vijesti za kategoriju: " + category);

        loadNewsByCategory();

        return view;
    }

    private void loadNewsByCategory() {
        if (category == null || category.isEmpty()) {
            Log.e("NewsFragment", "Kategorija je prazna!");
            return;
        }

        Log.d("NewsFragment", "Pozivam API za kategoriju: " + category);

        newsApiService.getNewsByCategory(
                category.toLowerCase(),
                "us",
                Constants.API_KEY
        ).enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                Log.d("NewsFragment", "API URL: " + call.request().url());

                if (response.isSuccessful() && response.body() != null) {
                    List<News> news = response.body().getArticles();
                    // Dodano za debugging
                    if (news == null || news.isEmpty()) {
                        Log.d("NewsFragment", "API je vratio prazan popis vijesti");
                        showError("Nema dostupnih vijesti za ovu kategoriju");
                        return;
                    }

                    Log.d("NewsFragment", "Primljeno " + news.size() + " vijesti za kategoriju " + category);
                    // Ispis prve vijesti za provjeru
                    if (!news.isEmpty()) {
                        News firstNews = news.get(0);
                        Log.d("NewsFragment", "Prva vijest: " +
                                "\nNaslov: " + firstNews.getTitle() +
                                "\nOpis: " + firstNews.getDescription());
                    }
                    newsAdapter.setNewsList(news);
                } else {
                    Log.e("NewsFragment", "Greška u odgovoru: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Nepoznata greška";
                        Log.e("NewsFragment", "Error body: " + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    showError("Greška pri dohvatu vijesti. Kod: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                Log.e("NewsFragment", "Greška pri dohvatu vijesti: " + t.getMessage());
                showError("Greška pri dohvatu vijesti: " + t.getMessage());
            }
        });

    }
    private void showError(String message) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }
}