package com.example.imagesearch;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.example.imagesearch.R;
import com.example.imagesearch.model.History;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends Fragment {

    private String url;
    private String title;
    private boolean isToolBarShow;

    private Toolbar toolbar;
    private ImageView imageView;

    public ImageFragment() {
        // Required empty public constructor
    }

    public static ImageFragment newInstance(String url, String title) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString("image_url", url);
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments().getString("image_url");
        title = getArguments().getString("title");
        isToolBarShow = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar_image);
        imageView = (ImageView) view.findViewById(R.id.fullscreen_image);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set UI components
        hideSystemBar();
        setToolBar();
        setImageView();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load image
        Picasso.with(getContext()).load(url).into((imageView));
    }

    // Set toolbar
    private void setToolBar() {
        toolbar.setTitle(getTitle());
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.inflateMenu(R.menu.menu_image);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_share:
                        setShareIntent();
                        break;
                }
                return true;
            }
        });
    }

    // Set image view
    private void setImageView(){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isToolBarShow) {
                    hideSystemBar();
                    isToolBarShow = false;
                } else {
                    showSystemBar();
                    isToolBarShow = true;
                }
            }
        });
    }

    private void setShareIntent() {
        // Get access to the URI for the bitmap
        Uri imageUri = getLocalBitmapUri(imageView);
        // Construct a ShareIntent with link to image
        Intent shareIntent = new Intent();
        // Construct a ShareIntent with link to image
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("*/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, title);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        // Launch share menu
        startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }

    // Returns the URI path to the Bitmap displayed in cover imageview
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".jpg");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    // Hide toolbar and status bar
    private void hideSystemBar() {
        toolbar.setVisibility(View.GONE);
        View decorView = getActivity().getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    // Show toolbar and status bar
    private void showSystemBar() {
        toolbar.setVisibility(View.VISIBLE);
        View decorView = getActivity().getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    // Cut some string if the title is too long
    private String getTitle() {
        if (title.length() <= 20)
            return title;
        else
            return title.substring(0,18) + "...";
    }
}
