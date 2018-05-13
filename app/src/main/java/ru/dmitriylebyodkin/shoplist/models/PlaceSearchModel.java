package ru.dmitriylebyodkin.shoplist.models;

import android.content.Context;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.RoomDb;
import ru.dmitriylebyodkin.shoplist.room.data.PlaceSearch;

public class PlaceSearchModel {
    public static List<PlaceSearch> getAll(Context context) {
        return RoomDb.getInstance(context).getPlaceSearchDao().getAll();
    }

    public static long[] insert(Context context, PlaceSearch placeSearch) {
        return RoomDb.getInstance(context).getPlaceSearchDao().insert(placeSearch);
    }

    public static void update(Context context, PlaceSearch placeSearch) {
        RoomDb.getInstance(context).getPlaceSearchDao().update(placeSearch);
    }

    public static void delete(Context context, PlaceSearch placeSearch) {
        RoomDb.getInstance(context).getPlaceSearchDao().delete(placeSearch);
    }
}
