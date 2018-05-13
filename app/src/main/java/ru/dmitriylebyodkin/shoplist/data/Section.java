package ru.dmitriylebyodkin.shoplist.data;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.adapters.IItemAdapter;
import ru.dmitriylebyodkin.shoplist.room.data.IItem;

public class Section {
    private String title;
    private List<IItem> list;
    private IItemAdapter adapter;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<IItem> getList() {
        return list;
    }

    public void setList(List<IItem> list) {
        this.list = list;
    }

    public IItemAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(IItemAdapter adapter) {
        this.adapter = adapter;
    }
}
