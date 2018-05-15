package ru.dmitriylebyodkin.shoplist.models;

import android.content.Context;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.activities.EditItemActivity;
import ru.dmitriylebyodkin.shoplist.activities.MainActivity;
import ru.dmitriylebyodkin.shoplist.room.RoomDb;
import ru.dmitriylebyodkin.shoplist.room.dao.IListDao;
import ru.dmitriylebyodkin.shoplist.room.data.IList;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;

public class ListModel {
    public static IListDao getDao(Context context) {
        return RoomDb.getInstance(context).getIListDao();
    }

    public static List<IListWithItems> getWithItems(Context context) {
        return getDao(context).getWithItems();
    }

    public static long[] insert(Context context, IList iList) {
        return getDao(context).insert(iList);
    }

    public static void update(Context context, IList iList) {
        getDao(context).update(iList);
    }

    public static void delete(Context context, IList iList) {
        getDao(context).delete(iList);
    }

    public static int getCountByTimeRange(Context context, int timestampFrom, int timestampTo) {
        return getDao(context).getCountByTimeRange(timestampFrom, timestampTo);
    }

    public static void deleteById(Context context, int id) {
        getDao(context).deleteById(id);
    }

    public static void updateUpdatedAtById(Context context, int id, int timestamp) {
        getDao(context).updateUpdatedAtById(id, timestamp);
    }
}
