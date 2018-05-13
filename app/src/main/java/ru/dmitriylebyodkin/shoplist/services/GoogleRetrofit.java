package ru.dmitriylebyodkin.shoplist.services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleRetrofit {
    private static final String ROOT_URL = "https://maps.googleapis.com/";
    public static final String API_KEY = "AIzaSyBCrTw3PJxlCAnHYfi7ZlX4ze4zWZEvh7I";

    private static Retrofit getInstance() {
        return new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static GoogleService getGoogleService() {
        return getInstance().create(GoogleService.class);
    }
}

