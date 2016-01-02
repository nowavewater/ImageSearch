package com.example.imagesearch.service;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imagesearch.Constants;
import com.example.imagesearch.ImageFragment;
import com.example.imagesearch.MainActivity;
import com.example.imagesearch.R;
import com.example.imagesearch.SearchFragment;
import com.example.imagesearch.model.History;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by ypc on 12/30/2015.
 *
 */
public class HistoryAdapter extends RecyclerView.Adapter {

    private List<History> historyList;

    public HistoryAdapter(List<History> list) {
        historyList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View listView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        viewHolder = new HistoryViewHolder(listView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        History history =  historyList.get(position);
        TextView historyView = ((HistoryViewHolder) holder).historyView;
        historyView.setText(history.getText());
        TextView dateView = ((HistoryViewHolder) holder).dateView;
        dateView.setText(history.getDate());

    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        public TextView historyView;
        public TextView dateView;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            historyView = (TextView) itemView.findViewById(R.id.history_text);
            dateView = (TextView) itemView.findViewById(R.id.date_text);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // Search the item when clicked
                    int position = getLayoutPosition();
                    History history = historyList.get(position);
                    Context context = view.getContext();
                    if (context instanceof MainActivity) {
                        History.saveHistory(history.getText());
                        FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                        SearchFragment fragment = (SearchFragment)fragmentManager.findFragmentByTag(SearchFragment.class.toString());
                        if (fragment!=null){
                            fragment.setNewSearch(history.getText());
                            if (fragmentManager.getBackStackEntryCount() > 0) {
                                fragmentManager.popBackStack();
                            }
                        } else {
                            fragment = SearchFragment.newInstance(history.getText(),Constants.NEW_SEARCH);
                            fragmentManager.beginTransaction()
                                    .replace(R.id.content_main, fragment, SearchFragment.class.toString())
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                }
            });
        }
    }
}