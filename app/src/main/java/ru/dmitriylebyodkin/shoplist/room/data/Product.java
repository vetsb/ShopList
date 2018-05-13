package ru.dmitriylebyodkin.shoplist.room.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.parceler.Parcel;

@Parcel
@Entity
public class Product {
    public final static String[] UNITS = new String[]{
            "шт",
            "килограмм",
            "грамм",
            "литр",
            "миллилитр",
            "банка",
            "пакет",
            "упаковка",
            "десяток",
            "бутылка",
    };

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int categoryId;
    private String title;
    private int unit;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public String getUnitTitle() {
        return UNITS[this.unit];
    }
}
