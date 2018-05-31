package ru.dmitriylebyodkin.shoplist.models;

import android.content.Context;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.RoomDb;
import ru.dmitriylebyodkin.shoplist.room.dao.ShopDao;
import ru.dmitriylebyodkin.shoplist.room.data.Shop;

public class ShopModel {
    public static ShopDao getDao(Context context) {
        return RoomDb.getInstance(context).getShopDao();
    }

    public static long[] insert(Context context, Shop shop) {
        return getDao(context).insert(shop);
    }

    public void update(Context context, Shop shop) {
        getDao(context).update(shop);
    }

    public void delete(Context context, Shop shop) {
        getDao(context).delete(shop);
    }

    public static Shop getById(Context context, int id) {
        return getDao(context).getById(id);
    }

    public static List<Shop> getAll(Context context) {
        return getDao(context).getAll();
    }

    public static Shop getByGoogleId(Context context, String id) {
        return getDao(context).getByGoogleId(id);
    }

    public static List<Shop> getBySearchId(Context context, int placeSearchId) {
        return getDao(context).getBySearchId(placeSearchId);
    }
}

