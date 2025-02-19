package com.example.newsapi.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newsapi.R;
import java.util.ArrayList;
import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {

        private List<String> searches = new ArrayList<>();
        private OnSearchItemClickListener listener;

        public interface OnSearchItemClickListener {
            void onSearchItemClick(String query);
            void onSearchItemLongClick(String query);
        }

    public SearchHistoryAdapter(OnSearchItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String search = searches.get(position);
        holder.textView.setText(search);
        holder.itemView.setOnClickListener(v -> listener.onSearchItemClick(search));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onSearchItemLongClick(search);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return searches.size();
    }

    public void setSearches(List<String> searches) {
        this.searches = searches;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.searchText);
        }
    }

}