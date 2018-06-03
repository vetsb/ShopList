package ru.dmitriylebyodkin.shoplist.views;

import com.arellomobile.mvp.MvpView;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.data.Category;

public interface EditProductView extends MvpView {
    void setCategoryIndex(int categoryIndex);

    void setCategoryList(List<Category> categoryList);
}
