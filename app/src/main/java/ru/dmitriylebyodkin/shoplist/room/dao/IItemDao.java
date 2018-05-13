package ru.dmitriylebyodkin.shoplist.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.data.IItem;

@Dao
public interface IItemDao {
    @Query("SELECT * FROM IItem ORDER BY id")
    List<IItem> getAll();

    @Query("SELECT * FROM IItem WHERE listId=:listId ORDER BY id")
    List<IItem> getByListId(int listId);

    @Query("SELECT * FROM IItem WHERE listId=:listId AND isFinished=0 ORDER BY id")
    List<IItem> getNotFinishedByListId(int listId);

    @Insert
    long[] insert(IItem... iItems);

    @Update
    void update(IItem... iItems);

    @Delete
    void delete(IItem... iItems);

    @Query("DELETE FROM IItem WHERE listId=:listId AND productId=:productId")
    void deleteByListAndProductIds(int listId, int productId);
}