package com.example.imagesearch;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.imagesearch.R;
import com.example.imagesearch.model.History;
import com.example.imagesearch.service.HistoryAdapter;
import com.example.imagesearch.service.SimpleDividerItemDecoration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private Toolbar toolbar;
    private Context context;

    private List<History> historyList;

    private RecyclerView histotyListView;
    private HistoryAdapter historyAdapter;
    private LinearLayoutManager layoutManager;


    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        historyList = new ArrayList<History>();
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_history);
        histotyListView = (RecyclerView) view.findViewById(R.id.history_list_view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Setup UI components
        setToolbar();
        setHistoryListView();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load all history to list
        if (historyList != null) {
            historyList.clear();
            historyList.addAll(History.getAll());
            historyAdapter.notifyDataSetChanged();
        }
    }

    private void setToolbar(){

        toolbar.setTitle("Search History");
        // Set navigation action
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
            }
        });
        // Inflate toolbar menu layout
        toolbar.inflateMenu(R.menu.menu_history);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_clear_history:
                        // Clear history
                        History.clear();
                        historyList.clear();
                        historyAdapter.notifyDataSetChanged();
                        break;
                }
                return true;
            }
        });
    }

    // Setup history recyclerview
    private void setHistoryListView(){
        histotyListView.setHasFixedSize(true);
        histotyListView.addItemDecoration(new SimpleDividerItemDecoration(context));
        layoutManager = new LinearLayoutManager(context);
        histotyListView.setLayoutManager(layoutManager);
        historyAdapter = new HistoryAdapter(historyList);
        histotyListView.setAdapter(historyAdapter);
    }
}
