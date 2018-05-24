package ru.dmitriylebyodkin.shoplist.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.data.Shop;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface AddShopView extends MvpView {
    void initList();

    void showLocationDialog();

    void hideProgressBar();

    void addAllToAdapter(List<Shop> shopList);

    void finishAdd();

    void setSelectedShop(Shop shop);

    void setInitialShop(Shop shop);
}
