package com.example.imagesearch;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Register from https://www.flickr.com/services/apps/create/
 * Then change you api key in SearchFragment
 *
 */



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Start with SearchFragment
        if(savedInstanceState == null) {
            SearchFragment fragment = SearchFragment.newInstance(null, Constants.NOT_NEW_SEARCH);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_main, fragment, SearchFragment.class.toString())
                    .commit();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
