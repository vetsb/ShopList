package ru.dmitriylebyodkin.shoplist.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.data.PlaceSearch;

@Dao
public interface PlaceSearchDao {
    @Insert
    long[] insert(PlaceSearch... searches);

    @Update
    void update(PlaceSearch... searches);

    @Delete
    void delete(PlaceSearch... searches);

    @Query("SELECT * FROM PlaceSearch ORDER BY id")
    List<PlaceSearch> getAll();
}
