package ru.dmitriylebyodkin.shoplist.services.Data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GoogleGeometry {
    @SerializedName("location")
    @Expose
    private GoogleLocation location;

    public GoogleLocation getLocation() {
        return location;
    }

    public void setLocation(GoogleLocation location) {
        this.location = location;
    }
}
