package com.example.newsapi.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newsapi.R;
import com.example.newsapi.adapter.NewsAdapter;
import com.example.newsapi.adapter.SearchHistoryAdapter;
import com.example.newsapi.api.NewsApiService;
import com.example.newsapi.api.NewsResponse;
import com.example.newsapi.model.News;
import com.example.newsapi.api.NewsApiClient;
import com.example.newsapi.util.Constants;
import com.example.newsapi.util.SearchHistory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

public class HomeFragment extends Fragment implements SearchHistoryAdapter.OnSearchItemClickListener {
    private RecyclerView newsRecyclerView;
    private NewsAdapter newsAdapter;
    private RecyclerView historyRecyclerView;
    private SearchHistoryAdapter historyAdapter;
    private NewsApiService newsApiService;
    private SearchHistory searchHistory;
    private EditText searchEdit;
    private Button searchButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        searchHistory = new SearchHistory(getContext());
        initializeViews(view);
        setupRecyclerViews();
        setupSearch();


        updateSearchHistory();

        return view;
    }

    private void initializeViews(View view) {
        newsRecyclerView = view.findViewById(R.id.newsRecyclerView);
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView);
        searchEdit = view.findViewById(R.id.searchEdit);
        searchButton = view.findViewById(R.id.searchButton);

        searchButton.setOnClickListener(v -> performSearch(searchEdit.getText().toString().trim()));
    }

    private void setupRecyclerViews() {

        newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        newsAdapter = new NewsAdapter(getContext());
        newsRecyclerView.setAdapter(newsAdapter);


        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAdapter = new SearchHistoryAdapter(this);
        historyRecyclerView.setAdapter(historyAdapter);
    }

    private void setupSearch() {
        newsApiService = NewsApiClient.getInstance().getNewsApiService();

        searchNews("hrvatska");
    }

    private void performSearch(String query) {
        if (!query.isEmpty()) {
            Log.d("NewsAPI", "Započinjem pretraživanje za: " + query);
            searchHistory.addSearch(query);
            updateSearchHistory();
            searchNews(query);
            historyRecyclerView.setVisibility(View.GONE);
        } else {
            Log.d("NewsAPI", "Upit za pretragu je prazan.");
        }
    }

    private void updateSearchHistory() {
        List<String> searches = searchHistory.getSearchHistory();
        historyAdapter.setSearches(searches);
        historyRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSearchItemClick(String query) {
        performSearch(query);
    }

    private void searchNews(String query) {
        Log.d("NewsAPI", "API poziv za upit: " + query);
        newsApiService.getNews(query, Constants.API_KEY, "hr").enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<News> newsList = response.body().getArticles();
                    Log.d("NewsAPI", "Primljeno vijesti: " + newsList.size());
                    newsAdapter.setNewsList(newsList);
                } else {
                    Log.e("NewsAPI", "Greška u odgovoru: " + response.code());
                    try {
                        Log.e("NewsAPI", "Error body: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), "Greška pri dohvatu vijesti. Kod: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                Log.e("NewsAPI", "Greška u API pozivu: " + t.getMessage());
                Toast.makeText(getContext(), "Greška pri dohvatu vijesti: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}