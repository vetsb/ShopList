package ru.dmitriylebyodkin.shoplist.services.Data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GooglePlaces {
//    @SerializedName("next_page_token")
//    @Expose
//    private String nextPageToken;
    @SerializedName("next_page_token")
    @Expose
    private String nextPageToken;
    @SerializedName("results")
    @Expose
    private List<GooglePlaceResult> results = null;

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public List<GooglePlaceResult> getResults() {
        return results;
    }

    public void setResults(List<GooglePlaceResult> results) {
        this.results = results;
    }
}
