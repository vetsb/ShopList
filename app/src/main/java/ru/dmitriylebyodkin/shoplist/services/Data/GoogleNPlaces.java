package ru.dmitriylebyodkin.shoplist.services.Data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GoogleNPlaces {
//    @SerializedName("next_page_token")
//    @Expose
//    private String nextPageToken;
    @SerializedName("next_page_token")
    @Expose
    private String nextPageToken;
    @SerializedName("results")
    @Expose
    private List<GoogleNPlaceResult> results = null;

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public List<GoogleNPlaceResult> getResults() {
        return results;
    }

    public void setResults(List<GoogleNPlaceResult> results) {
        this.results = results;
    }
}
