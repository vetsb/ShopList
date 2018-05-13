package ru.dmitriylebyodkin.shoplist.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ru.dmitriylebyodkin.shoplist.room.dao.CategoryDao;
import ru.dmitriylebyodkin.shoplist.room.dao.IItemDao;
import ru.dmitriylebyodkin.shoplist.room.dao.IListDao;
import ru.dmitriylebyodkin.shoplist.room.dao.PlaceSearchDao;
import ru.dmitriylebyodkin.shoplist.room.dao.ProductDao;
import ru.dmitriylebyodkin.shoplist.room.dao.ShopDao;
import ru.dmitriylebyodkin.shoplist.room.data.Category;
import ru.dmitriylebyodkin.shoplist.room.data.IItem;
import ru.dmitriylebyodkin.shoplist.room.data.IList;
import ru.dmitriylebyodkin.shoplist.room.data.PlaceSearch;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.room.data.Shop;

@Database(entities = { IList.class, IItem.class, Product.class, Category.class, Shop.class, PlaceSearch.class }, version = 1, exportSchema = false)

@TypeConverters(RoomDb.Converter.class)
public abstract class RoomDb extends RoomDatabase {

    private static final String DB_NAME = "roomDatabase.db";
    private static volatile RoomDb instance;

    public static synchronized RoomDb getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static RoomDb create(final Context context) {
        return Room.databaseBuilder(
                context,
                RoomDb.class,
                DB_NAME)
                .allowMainThreadQueries()
                .build();
    }

    public abstract IListDao getIListDao();

    public abstract IItemDao getIItemDao();

    public abstract ProductDao getProductDao();

    public abstract CategoryDao getCategoryDao();

    public abstract ShopDao getShopDao();

    public abstract PlaceSearchDao getPlaceSearchDao();

    public static class Converter {
        @TypeConverter
        public static ArrayList<String> fromString(String value) {
            Type listType = new TypeToken<ArrayList<String>>() {}.getType();
            return new Gson().fromJson(value, listType);
        }
        @TypeConverter
        public static String fromArrayLisr(ArrayList<String> list) {
            Gson gson = new Gson();
            String json = gson.toJson(list);
            return json;
        }
    }
}
