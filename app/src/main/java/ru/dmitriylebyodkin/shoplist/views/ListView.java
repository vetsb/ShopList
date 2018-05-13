package ru.dmitriylebyodkin.shoplist.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import ru.dmitriylebyodkin.shoplist.room.data.IItem;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.Product;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface ListView extends MvpView {
    void initAdapter();

    void updateList(IListWithItems iListWithItems);

    void initList();

    void addItem(IItem item);

    void showSearchIcon();

    void hideSearchIcon();

    void clearSearch();

    void addProduct(Product product);

    void showCost();

    void checkAll();

    void resetAll();

    void deleteChecked();

    void setActivityTitle(String title);
}
