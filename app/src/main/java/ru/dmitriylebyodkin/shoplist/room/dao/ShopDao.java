package ru.dmitriylebyodkin.shoplist.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.data.Shop;

@Dao
public interface ShopDao {
    @Query("SELECT * FROM Shop ORDER BY id")
    List<Shop> getAll();

    @Insert
    long[] insert(Shop... shops);

    @Update
    void update(Shop... shops);

    @Delete
    void delete(Shop... shops);

    @Query("SELECT * FROM Shop WHERE id=:id LIMIT 1")
    Shop getById(int id);

    @Query("SELECT * FROM Shop WHERE googleId=:id LIMIT 1")
    Shop getByGoogleId(String id);

    @Query("SELECT * FROM Shop WHERE searchId=:placeSearchId ORDER BY id")
    List<Shop> getBySearchId(int placeSearchId);
}
