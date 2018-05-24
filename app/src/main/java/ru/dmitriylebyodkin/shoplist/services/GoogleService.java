package ru.dmitriylebyodkin.shoplist.services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.dmitriylebyodkin.shoplist.services.Data.GooglePlaces;

public interface GoogleService {
    @GET("maps/api/place/nearbysearch/json")
    Call<GooglePlaces> getPlaces(@Query("location") String location, @Query("radius") String radius, @Query("type") String type, @Query("language") String language, @Query("key") String key, @Query("pagetoken") String token);
}
