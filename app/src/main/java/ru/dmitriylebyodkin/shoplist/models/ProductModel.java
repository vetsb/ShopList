package ru.dmitriylebyodkin.shoplist.models;

import android.content.Context;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.RoomDb;
import ru.dmitriylebyodkin.shoplist.room.dao.ProductDao;
import ru.dmitriylebyodkin.shoplist.room.data.Product;

public class ProductModel {
    public static ProductDao getDao(Context context) {
        return RoomDb.getInstance(context).getProductDao();
    }

    public static long[] insert(Context context, Product product) {
        return getDao(context).insert(product);
    }

    public static void update(Context context, Product product) {
        getDao(context).update(product);
    }

    public static void delete(Context context, Product product) {
        getDao(context).delete(product);
    }

    public static List<Product> getAll(Context context) {
        return getDao(context).getAll();
    }

    public static void setNullCategoryId(Context context, int categoryId) {
        getDao(context).setNullCategoryId(categoryId);
    }

    public static Product getById(Context context, int productId) {
        return getDao(context).getById(productId);
    }

    public static List<Product> getAllDesc(Context context) {
        return getDao(context).getAllDesc();
    }

    public static List<Product> getAllDescByCategoryId(Context context, int categoryId) {
        return getDao(context).getAllDescByCategoryId(categoryId);
    }
}
