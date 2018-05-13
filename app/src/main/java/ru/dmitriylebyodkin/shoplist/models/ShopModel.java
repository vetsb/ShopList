package ru.dmitriylebyodkin.shoplist.models;

import android.content.Context;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.RoomDb;
import ru.dmitriylebyodkin.shoplist.room.dao.ShopDao;
import ru.dmitriylebyodkin.shoplist.room.data.Shop;

public class ShopModel {
    public static long[] insert(Context context, Shop shop) {
        return RoomDb.getInstance(context).getShopDao().insert(shop);
    }

    public void update(Context context, Shop shop) {
        RoomDb.getInstance(context).getShopDao().update(shop);
    }

    public void delete(Context context, Shop shop) {
        RoomDb.getInstance(context).getShopDao().delete(shop);
    }

    public static Shop getById(Context context, int id) {
        return RoomDb.getInstance(context).getShopDao().getById(id);
    }

    public static List<Shop> getAll(Context context) {
        return RoomDb.getInstance(context).getShopDao().getAll();
    }

    public static Shop getByGoogleId(Context context, String id) {
        return RoomDb.getInstance(context).getShopDao().getByGoogleId(id);
    }

    public static List<Shop> getBySearchId(Context context, int placeSearchId) {
        return RoomDb.getInstance(context).getShopDao().getBySearchId(placeSearchId);
    }
}

