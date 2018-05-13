package ru.dmitriylebyodkin.shoplist.models;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.RoomDb;
import ru.dmitriylebyodkin.shoplist.room.dao.ProductDao;
import ru.dmitriylebyodkin.shoplist.room.data.Product;

public class ProductModel {
    public static long[] insert(Context context, Product product) {
        return RoomDb.getInstance(context).getProductDao().insert(product);
    }

    public static void update(Context context, Product product) {
        RoomDb.getInstance(context).getProductDao().update(product);
    }

    public static void delete(Context context, Product product) {
        RoomDb.getInstance(context).getProductDao().delete(product);
    }

    public static List<Product> getAll(Context context) {
        return RoomDb.getInstance(context).getProductDao().getAll();
    }
}
