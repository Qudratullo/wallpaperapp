package com.example.wallpaperappdemo.Classes;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Image implements Serializable {
    @SerializedName("previewURL")
    @Expose
    private String previewURL;

    @SerializedName("webformatURL")
    @Expose
    private String webformatURL;

    @SerializedName("largeImageURL")
    @Expose
    private String largeImageURL;

    public String getPreviewURL() {
        return previewURL;
    }

    public void setPreviewURL(String previewURL) {
        this.previewURL = previewURL;
    }

    public String getWebformatURL() {
        return webformatURL;
    }

    public void setWebformatURL(String webformatURL) {
        this.webformatURL = webformatURL;
    }

    public String getLargeImageURL() {
        return largeImageURL;
    }

    public void setLargeImageURL(String largeImageURL) {
        this.largeImageURL = largeImageURL;
    }

    public Image(String previewURL, String webformatURL, String largeImageURL) {
        this.previewURL = previewURL;
        this.webformatURL = webformatURL;
        this.largeImageURL = largeImageURL;
    }

    public String getAll() {
        return (this.largeImageURL + " " + this.webformatURL+ " " + this.previewURL);
    }
}
