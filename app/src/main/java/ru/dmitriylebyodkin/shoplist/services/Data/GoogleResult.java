package ru.dmitriylebyodkin.shoplist.services.Data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GoogleResult {
    @SerializedName("geometry")
    @Expose
    private GoogleGeometry geometry;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("reference")
    @Expose
    private String reference;

    public GoogleGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(GoogleGeometry geometry) {
        this.geometry = geometry;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

}
