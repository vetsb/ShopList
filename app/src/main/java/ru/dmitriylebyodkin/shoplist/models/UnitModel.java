package ru.dmitriylebyodkin.shoplist.models;

import android.content.Context;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.RoomDb;
import ru.dmitriylebyodkin.shoplist.room.dao.UnitDao;
import ru.dmitriylebyodkin.shoplist.room.data.Unit;

public class UnitModel {
    private static UnitDao getDao(Context context) {
        return RoomDb.getInstance(context).getUnitDao();
    }

    public static long[] insert(Context context, Unit unit) {
        return getDao(context).insert(unit);
    }

    public static void update(Context context, Unit unit) {
        getDao(context).update(unit);
    }

    public static void delete(Context context, Unit unit) {
        getDao(context).delete(unit);
    }

    public static List<Unit> getAll(Context context) {
        return getDao(context).getAll();
    }

    public static List<Unit> getAllDesc(Context context) {
        return getDao(context).getAllDesc();
    }

    public static Unit getById(Context context, int id) {
        return getDao(context).getById(id);
    }
}
