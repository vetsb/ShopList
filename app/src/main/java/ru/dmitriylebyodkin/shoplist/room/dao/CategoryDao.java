package ru.dmitriylebyodkin.shoplist.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.data.Category;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM Category ORDER BY id")
    List<Category> getAll();

    @Insert
    long[] insert(Category... categories);

    @Update
    void update(Category... categories);

    @Delete
    void delete(Category... categories);
}
