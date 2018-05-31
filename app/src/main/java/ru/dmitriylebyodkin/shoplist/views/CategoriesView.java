package ru.dmitriylebyodkin.shoplist.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.data.Category;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface CategoriesView extends MvpView {
    void setCategoryList(List<Category> categoryList);

    void setAdapter();

    void initList();

    void showNoItems();

    void hideNoItems();

    void showEditDialog(Category category, int position);

    void showDeleteDialog(Category category, int position);

    void updateAdapterItem(int position, Category category);

    void removeAdapterItem(int position);

    void addAdapterItem(Category category);
}
