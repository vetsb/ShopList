package ru.dmitriylebyodkin.shoplist.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.data.IList;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;

@Dao
public interface IListDao {
    @Query("SELECT * FROM IList ORDER BY id DESC")
    List<IList> getAll();

    @Query("SELECT COUNT(*) FROM IList")
    int getSize();

    @Query("SELECT * FROM IList ORDER BY id DESC")
    List<IListWithItems> getWithItems();

    @Query("SELECT * FROM IList WHERE id=:id LIMIT 1")
    IListWithItems getWithItemsById(int id);

    @Query("SELECT * FROM IList WHERE id=:id LIMIT 1")
    IList getById(int id);

    @Insert
    long[] insert(IList... iLists);

    @Update
    void update(IList... iLists);

    @Delete
    void delete(IList... iLists);

    @Query("SELECT COUNT(*) FROM IList WHERE createdAt>=:timestampFrom AND createdAt<=:timestampTo ORDER BY id")
    int getCountByTimeRange(int timestampFrom, int timestampTo);

    @Query("DELETE FROM IList WHERE id=:id")
    void deleteById(int id);

    @Query("UPDATE IList SET updatedAt=:timestamp WHERE id=:id")
    void updateUpdatedAtById(int id, int timestamp);

    @Query("SELECT * FROM IList WHERE shopId=:shopId ORDER BY id")
    List<IList> getByShopId(int shopId);
}
