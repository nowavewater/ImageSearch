package com.example.imagesearch.service;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.imagesearch.ImageFragment;
import com.example.imagesearch.MainActivity;
import com.example.imagesearch.R;
import com.example.imagesearch.model.Photo;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ypc on 12/29/2015.
 */
public class ImageAdapter extends RecyclerView.Adapter {

    private List<Photo> photoList;

    private int visibleThreshold = 5;
    private int lastVisibleItem;
    private int totalItemCount;
    private boolean loading;
    private Context context;

    public final int VIEW_ITEM = 1;
    public final int VIEW_PEOG = 0;

    private OnLoadMoreListener onLoadMoreListener;

    public ImageAdapter(List<Photo> list, RecyclerView recyclerView, Context context) {
        photoList = list;
        this.context = context;
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager layoutManager =
                    (GridLayoutManager) recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = layoutManager.getItemCount();
                    lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    if ( !loading &&
                            totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMoreBrief();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return photoList.get(position) != null ? VIEW_ITEM : VIEW_PEOG;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        if ( viewType == VIEW_ITEM ) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image, parent, false);
            viewHolder = new ImageViewHolder(itemView);
        }
        else if ( viewType == VIEW_PEOG ){
            View progressView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_progress, parent, false);
            viewHolder = new ProgressViewHolder(progressView);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageViewHolder) {
            Photo photo = photoList.get(position);
            TextView textView = ((ImageViewHolder) holder).titleView;
            textView.setText(photo.getTitle());
            ImageView imageView = ((ImageViewHolder) holder).imageView;

            Picasso.with(context).load(photo.getthumbURL()).into(imageView);
        }
        else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public void setLoaded() {
        loading = false;
    }

    public void setLoading() {
        loading = true;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView titleView;
        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.image_title);
            imageView = (ImageView) itemView.findViewById(R.id.thumb_view);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    // open image when clicked
                    Photo photo = photoList.get(getLayoutPosition());
                    if (context instanceof MainActivity) {
                        ImageFragment fragment = ImageFragment.newInstance(photo.getURL(), photo.getTitle());
                        FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.content_main, fragment, ImageFragment.class.toString())
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
        }
    }
}
