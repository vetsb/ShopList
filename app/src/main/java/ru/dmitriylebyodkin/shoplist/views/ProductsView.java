package ru.dmitriylebyodkin.shoplist.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.data.Product;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface ProductsView extends MvpView {
    void setList(List<Product> productList);

    void setAdapter();

    void initList();

    void insert(Product product);

    void update(Product product, int position);

    void delete(int position);

    void showNoItems();

    void hideNoItems();

    void showDeleteDialog(Product product, int position);

    void updateList();

    void showDeleteToast();
}
