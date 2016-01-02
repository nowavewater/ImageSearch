package com.example.imagesearch.model;

import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by ypc on 12/29/2015.
 * Interface for Flickr API.
 *
 */
public interface FlickrService {

    @GET("/services/rest/")
    Call<SearchResponse> searchPhoto(@QueryMap Map<String, String> options);

}
