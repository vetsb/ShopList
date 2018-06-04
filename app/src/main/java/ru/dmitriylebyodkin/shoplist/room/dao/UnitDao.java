package ru.dmitriylebyodkin.shoplist.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.data.Unit;

@Dao
public interface UnitDao {
    @Insert
    long[] insert(Unit... units);

    @Update
    void update(Unit... units);

    @Delete
    void delete(Unit... units);

    @Query("SELECT * FROM Unit ORDER BY id")
    List<Unit> getAll();

    @Query("SELECT * FROM Unit ORDER BY id DESC")
    List<Unit> getAllDesc();

    @Query("SELECT * FROM Unit WHERE id=:id LIMIT 1")
    Unit getById(int id);
}
