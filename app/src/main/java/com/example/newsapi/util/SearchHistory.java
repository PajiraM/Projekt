package com.example.newsapi.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SearchHistory {
    private static final String PREF_NAME = "SearchHistoryPrefs";
    private static final String KEY_SEARCH_HISTORY = "search_history";
    private SharedPreferences preferences;
    private Gson gson;

    public SearchHistory(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void addSearch(String query) {
        List<String> searches = getSearchHistory();

        if (!searches.contains(query)) {
            searches.add(0, query);

            if (searches.size() > 10) {
                searches = searches.subList(0, 10);
            }
            saveSearchHistory(searches);
        }
    }

    public List<String> getSearchHistory() {
        String json = preferences.getString(KEY_SEARCH_HISTORY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void saveSearchHistory(List<String> searches) {
        String json = gson.toJson(searches);
        preferences.edit().putString(KEY_SEARCH_HISTORY, json).apply();
    }

    public void clearHistory() {
        preferences.edit().remove(KEY_SEARCH_HISTORY).apply();
    }
}