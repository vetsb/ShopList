package ru.dmitriylebyodkin.shoplist.models;

import android.content.Context;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.RoomDb;
import ru.dmitriylebyodkin.shoplist.room.data.Category;

public class CategoryModel {
    public static List<Category> getAll(Context context) {
        return RoomDb.getInstance(context).getCategoryDao().getAll();
    }

    public static long[] insert(Context context, Category category) {
        return RoomDb.getInstance(context).getCategoryDao().insert(category);
    }

    public static void update(Context context, Category category) {
        RoomDb.getInstance(context).getCategoryDao().update(category);
    }

    public static void delete(Context context, Category category) {
        RoomDb.getInstance(context).getCategoryDao().delete(category);
    }
}
