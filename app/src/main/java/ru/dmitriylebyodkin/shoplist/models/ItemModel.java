package ru.dmitriylebyodkin.shoplist.models;

import android.content.Context;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.activities.InfoActivity;
import ru.dmitriylebyodkin.shoplist.room.RoomDb;
import ru.dmitriylebyodkin.shoplist.room.dao.IItemDao;
import ru.dmitriylebyodkin.shoplist.room.data.IItem;

public class ItemModel {
    public static IItemDao getDao(Context context) {
        return RoomDb.getInstance(context).getIItemDao();
    }

    public static long[] insert(Context context, IItem item) {
        return getDao(context).insert(item);
    }

    public static void update(Context context, IItem item) {
        getDao(context).update(item);
    }

    public static void delete(Context context, IItem item) {
        getDao(context).delete(item);
    }

    public static List<IItem> getByListId(Context context, int listId) {
        return getDao(context).getByListId(listId);
    }

    public static IItem getByListAndProductIds(Context context, int listId, int productId) {
        return getDao(context).getByListAndProductIds(listId, productId);
    }
}
