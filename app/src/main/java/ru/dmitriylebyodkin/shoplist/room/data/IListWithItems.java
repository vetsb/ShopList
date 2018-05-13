package ru.dmitriylebyodkin.shoplist.room.data;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class IListWithItems {
    @Embedded
    private IList list;
    @Relation(parentColumn = "id", entity = IItem.class, entityColumn = "listId")
    private List<IItem> items;

    public IList getList() {
        return list;
    }

    public void setList(IList list) {
        this.list = list;
    }

    public List<IItem> getItems() {
        return items;
    }

    public void setItems(List<IItem> items) {
        this.items = items;
    }
}
