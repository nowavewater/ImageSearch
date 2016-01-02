package com.example.imagesearch;


import android.app.SearchManager;
import android.content.Context;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.imagesearch.model.FlickrService;
import com.example.imagesearch.model.History;
import com.example.imagesearch.model.Photo;
import com.example.imagesearch.model.SearchResponse;
import com.example.imagesearch.service.ImageAdapter;
import com.example.imagesearch.service.OnLoadMoreListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private static final String BASE_URL = "https://api.flickr.com";
    private static final String API_KEY = "Replace your API key";

    private List<Photo> photoList;
    private FlickrService service;
    private Context context;
    private SimpleCursorAdapter mAdapter;
    private String query;
    private int pageCount;
    private Boolean isNewSearch;

    private Toolbar toolbar;
    private RecyclerView imageListView;
    private ImageAdapter imageAdapter;
    private FloatingActionButton topButton;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String query, Boolean isNewSearch) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString("query", query);
        args.putBoolean("new_search", isNewSearch);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (savedInstanceState == null) {
            photoList = new ArrayList<Photo>();
            query = getArguments().getString("query");
            isNewSearch = getArguments().getBoolean("new_search");
            pageCount = 0;
            mAdapter = new SimpleCursorAdapter(getActivity(),
                    R.layout.dropdown_item_1line,
                    null,
                    new String[] {SearchManager.SUGGEST_COLUMN_TEXT_1},
                    new int[] {android.R.id.text1},
                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            service  = retrofit.create(FlickrService.class);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_main);
        imageListView = (RecyclerView) view.findViewById(R.id.image_list_view);
        topButton = (FloatingActionButton) view.findViewById(R.id.move_to_top_button);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set UI components
        setToolBar();
        setSearchView();
        setImageListView();

        topButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageListView.scrollToPosition(0);
            }
        });

        if (isNewSearch) {
            searchImage(query);
            isNewSearch = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Hide keyboard if this fragment is stopped
        hideKeyboard();
    }

    // Set toolbar
    private void setToolBar(){

        toolbar.setTitle("ImageSearch");
        toolbar.inflateMenu(R.menu.menu_main);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_history:
                        // Enter HistoryFragment
                        HistoryFragment fragment = HistoryFragment.newInstance();
                        FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_main, fragment, HistoryFragment.class.toString())
                                .addToBackStack(null)
                                .commit();
                        break;
                }
                return true;
            }
        });

    }

    // setup searchview
    private void setSearchView() {
        // populate searchview with menu layout
        final MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setSuggestionsAdapter(mAdapter);
        // Getting selected (clicked) item suggestion
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {

                CursorAdapter c = searchView.getSuggestionsAdapter();
                MatrixCursor cur = (MatrixCursor) c.getCursor();
                cur.moveToPosition(position);
                String text = cur.getString(cur.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                searchImage(text);
                query = text;
                History.saveHistory(text);
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchItem.collapseActionView();
                imageListView.scrollToPosition(0);
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                // Your code here
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                // Fetch the data remotely
                // Reset SearchView
                searchImage(text);
                query = text;
                History.saveHistory(text);
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchItem.collapseActionView();
                imageListView.scrollToPosition(0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // Handle real time input string
                populateAdapter(s);
                return false;
            }
        });

    }

    // Setup image list view
    private void setImageListView(){
        imageListView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
        imageListView.setLayoutManager(layoutManager);
        imageAdapter = new ImageAdapter(photoList,
                imageListView, context);
        imageListView.setAdapter(imageAdapter);
        // Setup load more listener
        imageAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMoreBrief() {
                // Add a progress bar
                photoList.add(null);
                imageAdapter.notifyItemInserted(photoList.size() - 1);

                Map<String, String> options = setSearchOptions(query, Constants.NOT_NEW_SEARCH);
                Call<SearchResponse> call = service.searchPhoto(options);
                call.enqueue(new Callback<SearchResponse>() {
                    @Override
                    public void onResponse(Response<SearchResponse> response, Retrofit retrofit) {
                        SearchResponse result = response.body();
                        if (result.getStat().equals("ok")) {
                            // Remove progress bar and add more photos to list
                            photoList.remove(photoList.size() - 1);
                            imageAdapter.notifyItemRemoved(photoList.size());
                            if (result.getPhotos().getPhoto().size() != 0) {
                                int size = imageAdapter.getItemCount();
                                photoList.addAll(result.getPhotos().getPhoto());
                                imageAdapter.notifyItemRangeInserted(size, photoList.size() - 1);
                                imageAdapter.setLoaded();
                                pageCount++;
                            }
                        } else {
                            // Remove progress bar and display message if something wrong
                            photoList.remove(photoList.size() - 1);
                            imageAdapter.notifyItemRemoved(photoList.size());
                            Toast.makeText(context, result.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        // Log error here since request failed
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        // To make sure the progress bar in a single view, not in a grid
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = imageAdapter.getItemViewType(position);
                if (viewType == imageAdapter.VIEW_ITEM) {
                    return 1;
                } else if (viewType == imageAdapter.VIEW_PEOG) {
                    return 2;
                }
                return -1;
            }
        });
    }

    // Set search parameters
    // Input query is the query text, isNewSearch
    // isNewSearch = true : new search, no need add page
    // isNewSearch = false : load more page, add page count to request
    // Return: a map type of options within required parameters
    private Map<String, String> setSearchOptions(String query, Boolean isNewSearch) {

        Map<String, String> options = new HashMap<String, String>();
        options.put("method", "flickr.photos.search");
        options.put("api_key", API_KEY);
        options.put("format", "json");
        options.put("text", query);
        options.put("nojsoncallback", "1");
        options.put("sort", "relevance");
        options.put("per_page", Integer.toString(Constants.PER_PAGE));
        if (!isNewSearch) {
            options.put("page", Integer.toString(pageCount));
        }
        return options;
    }

    // Send search request to api and handle returned results
    // Input is query text
    private void searchImage(String text){

        // Set toolbar title as query string
        toolbar.setTitle(text);

        Map<String, String> options = setSearchOptions(text, Constants.NEW_SEARCH);
        Call<SearchResponse> call = service.searchPhoto(options);

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Response<SearchResponse> response, Retrofit retrofit) {
                SearchResponse result = response.body();
                if (result.getStat().equals("ok")) {
                    // Status is ok, add result to photo list
                    if (photoList != null) {
                        photoList.clear();
                        photoList.addAll(result.getPhotos().getPhoto());
                        imageAdapter.notifyDataSetChanged();
                        pageCount = 2;
                    }

                } else {
                    // Display error if something wrong with result
                    Toast.makeText(context, result.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                // Display error here since request failed
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Set returning search from HistoryFragment
    public void setNewSearch(String query){
        isNewSearch = true;
        this.query = query;
    }

    // Populate cusoradapter with suggestion results from local db
    // Input query is captured query changed text
    private void populateAdapter(String query) {
        // Create a matrixcursor, add suggtion strings into cursor
        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID,  SearchManager.SUGGEST_COLUMN_TEXT_1 });
        List<String> suggestions = History.getSuggestion(query);
        for (int i=0;i<suggestions.size();i++){
            c.addRow(new Object[] {i, suggestions.get(i)});
        }
        mAdapter.changeCursor(c);
    }

    // Hide keyboard programmingly
    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
