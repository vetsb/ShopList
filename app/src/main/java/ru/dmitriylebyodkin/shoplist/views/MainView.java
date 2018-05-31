package ru.dmitriylebyodkin.shoplist.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.data.Category;
import ru.dmitriylebyodkin.shoplist.room.data.IList;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.Product;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface MainView extends MvpView {
    void setActivityTitle(String title);

    void smoothScrollToBegin();

    void setAdapter();

    void addAdapterItemToBegin(IListWithItems iListWithItems);

    void updateAdapterItem(int position, IListWithItems iListWithItems);

    void removeAdapterItem(int position);

    void setAdapterProducts(List<Product> productList);

    void showListNoItems();

    void showCartNoItems();

    void showCategoryEditDialog(Category category, int position);

    void showCategoryDeleteDialog(Category category, int position);

    void showListDeleteDialog(IList list, int position);
}
