package com.example.wallpaperappdemo.Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Response {

    @SerializedName("total")
    @Expose
    private int total;

    @SerializedName("totalHits")
    @Expose
    private int totalHits;

    @SerializedName("hits")
    @Expose
    private ArrayList<Image> hits;

    public ArrayList<Image> getHits() {
        return hits;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }

    public void setHits(ArrayList<Image> hits) {
        this.hits = hits;
    }

    public Response(int total, int totalHits, ArrayList<Image> hits) {
        this.total = total;
        this.totalHits = totalHits;
        this.hits = hits;
    }
}
