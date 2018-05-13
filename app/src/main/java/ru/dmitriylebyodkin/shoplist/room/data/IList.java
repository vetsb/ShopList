package ru.dmitriylebyodkin.shoplist.room.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.parceler.Parcel;

@Parcel
@Entity
public class IList {
    public static final String[] SORTING_TYPES = new String[] {
            "По алфавиту",
            "По категориям",
            "По мере добавления"
    };

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private int shopId;
    private boolean isCompleted = false;
    private int countExecutions;
    private int timestampNotification;
    private int sortingType = 2;
    private int createdAt = (int) (System.currentTimeMillis()/1000L);
    private int updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public int getCountExecutions() {
        return countExecutions;
    }

    public void setCountExecutions(int countExecutions) {
        this.countExecutions = countExecutions;
    }

    public int getTimestampNotification() {
        return timestampNotification;
    }

    public void setTimestampNotification(int timestampNotification) {
        this.timestampNotification = timestampNotification;
    }

    public int getSortingType() {
        return sortingType;
    }

    public void setSortingType(int sortingType) {
        this.sortingType = sortingType;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public int getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(int updatedAt) {
        this.updatedAt = updatedAt;
    }
}
