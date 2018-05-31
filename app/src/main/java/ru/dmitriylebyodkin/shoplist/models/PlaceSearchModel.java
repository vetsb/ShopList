package ru.dmitriylebyodkin.shoplist.models;

import android.content.Context;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.RoomDb;
import ru.dmitriylebyodkin.shoplist.room.dao.PlaceSearchDao;
import ru.dmitriylebyodkin.shoplist.room.data.PlaceSearch;

public class PlaceSearchModel {
    public static PlaceSearchDao getDao(Context context) {
        return RoomDb.getInstance(context).getPlaceSearchDao();
    }

    public static List<PlaceSearch> getAll(Context context) {
        return getDao(context).getAll();
    }

    public static long[] insert(Context context, PlaceSearch placeSearch) {
        return getDao(context).insert(placeSearch);
    }

    public static void update(Context context, PlaceSearch placeSearch) {
        getDao(context).update(placeSearch);
    }

    public static void delete(Context context, PlaceSearch placeSearch) {
        getDao(context).delete(placeSearch);
    }
}
