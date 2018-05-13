package ru.dmitriylebyodkin.shoplist.room.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import org.parceler.Parcel;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Parcel
@Entity(foreignKeys = @ForeignKey(entity = IList.class, parentColumns = "id", childColumns = "listId", onDelete = CASCADE))
public class IItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int productId;
    private int listId;
    private int shopId;
    private int count;
    private float cost;
    private String note;
    private boolean isBought = false;
    private boolean isFinished = false;
//    private double latitude;
//    private double longitude;
//    private double altitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isBought() {
        return isBought;
    }

    public void setBought(boolean bought) {
        isBought = bought;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

}
