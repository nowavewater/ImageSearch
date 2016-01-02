package com.example.imagesearch.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ypc on 12/29/2015.
 * Data mode used to map json results
 * Generated automatically from jsonschema2pojo.org
 * Refer https://www.flickr.com/services/api/explore/flickr.photos.search for more info
 *
 */
public class SearchResponse {

    @SerializedName("photos")
    @Expose
    private Photos photos;
    @SerializedName("stat")
    @Expose
    private String stat;

    @SerializedName("code")
    @Expose
    private String code;

    @SerializedName("message")
    @Expose
    private String message;


    /**
     *
     * @return
     * The photos
     */
    public Photos getPhotos() {
        return photos;
    }

    /**
     *
     * @param photos
     * The photos
     */
    public void setPhotos(Photos photos) {
        this.photos = photos;
    }

    /**
     *
     * @return
     * The stat
     */
    public String getStat() {
        return stat;
    }

    public String getCode() { return code;}

    public void setCode(String code) { this.code = code;}

    public String getMessage() {return message;}

    public void setMessage(String message) {this.message = message;}

    /**
     *
     * @param stat
     * The stat
     */
    public void setStat(String stat) {
        this.stat = stat;
    }}

