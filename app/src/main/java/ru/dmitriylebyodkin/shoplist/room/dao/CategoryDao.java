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

    @Query("SELECT * FROM Category WHERE title LIKE '%' || :text || '%' ORDER BY id")
    List<Category> getContains(String text);

    @Query("SELECT COUNT(*) FROM Category WHERE title LIKE '%' || :text || '%'")
    int getContainsCount(String text);

    @Query("SELECT * FROM Category ORDER BY id DESC")
    List<Category> getAllDesc();
}
