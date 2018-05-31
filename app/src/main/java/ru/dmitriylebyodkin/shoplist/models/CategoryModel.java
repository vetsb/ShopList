package ru.dmitriylebyodkin.shoplist.models;

import android.content.Context;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.RoomDb;
import ru.dmitriylebyodkin.shoplist.room.dao.CategoryDao;
import ru.dmitriylebyodkin.shoplist.room.data.Category;

public class CategoryModel {
    public static CategoryDao getDao(Context context) {
        return RoomDb.getInstance(context).getCategoryDao();
    }

    public static List<Category> getAll(Context context) {
        return getDao(context).getAll();
    }

    public static long[] insert(Context context, Category category) {
        return getDao(context).insert(category);
    }

    public static void update(Context context, Category category) {
        getDao(context).update(category);
    }

    public static void delete(Context context, Category category) {
        getDao(context).delete(category);
    }
}
