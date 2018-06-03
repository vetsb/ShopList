package ru.dmitriylebyodkin.shoplist.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.data.Product;

@Dao
public interface ProductDao {
    @Query("SELECT * FROM Product ORDER BY id")
    List<Product> getAll();

    @Query("SELECT * FROM Product ORDER BY id DESC")
    List<Product> getAllDesc();

    @Query("UPDATE Product SET title=:title WHERE id=:id")
    void updateTitleById(int id, String title);

    @Insert
    long[] insert(Product... products);

    @Update
    void update(Product... products);

    @Delete
    void delete(Product... products);

    @Query("SELECT * FROM Product WHERE id=:productId LIMIT 1")
    Product getById(int productId);

    @Query("UPDATE Product SET categoryId=0 WHERE categoryId=:categoryId")
    void setNullCategoryId(int categoryId);

    @Query("SELECT * FROM Product WHERE categoryId=:categoryId ORDER BY id")
    List<Product> getAllByCategoryId(int categoryId);

    @Query("SELECT * FROM Product WHERE categoryId=:categoryId ORDER BY id DESC")
    List<Product> getAllDescByCategoryId(int categoryId);
}
