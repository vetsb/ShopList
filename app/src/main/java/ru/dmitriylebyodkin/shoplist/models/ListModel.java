package ru.dmitriylebyodkin.shoplist.models;

import android.content.Context;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.activities.MainActivity;
import ru.dmitriylebyodkin.shoplist.room.RoomDb;
import ru.dmitriylebyodkin.shoplist.room.dao.IListDao;
import ru.dmitriylebyodkin.shoplist.room.data.IList;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;

public class ListModel {
    public static List<IListWithItems> getWithItems(Context context) {
        return RoomDb.getInstance(context).getIListDao().getWithItems();
    }

    public static long[] insert(Context context, IList iList) {
        return RoomDb.getInstance(context).getIListDao().insert(iList);
    }

    public static void update(Context context, IList iList) {
        RoomDb.getInstance(context).getIListDao().update(iList);
    }

    public static void delete(Context context, IList iList) {
        RoomDb.getInstance(context).getIListDao().delete(iList);
    }

    public static int getCountByTimeRange(Context context, int timestampFrom, int timestampTo) {
        return RoomDb.getInstance(context).getIListDao().getCountByTimeRange(timestampFrom, timestampTo);
    }
}
