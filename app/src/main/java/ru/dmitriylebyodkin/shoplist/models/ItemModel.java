package ru.dmitriylebyodkin.shoplist.models;

import android.content.Context;

import ru.dmitriylebyodkin.shoplist.room.RoomDb;
import ru.dmitriylebyodkin.shoplist.room.dao.IItemDao;
import ru.dmitriylebyodkin.shoplist.room.data.IItem;

public class ItemModel {
    public static long[] insert(Context context, IItem item) {
        return RoomDb.getInstance(context).getIItemDao().insert(item);
    }

    public static void update(Context context, IItem item) {
        RoomDb.getInstance(context).getIItemDao().update(item);
    }

    public static void delete(Context context, IItem item) {
        RoomDb.getInstance(context).getIItemDao().delete(item);
    }
}
