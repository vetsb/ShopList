package ru.dmitriylebyodkin.shoplist.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface CartView extends MvpView {
    void setLists(List<IListWithItems> lists);

    void setAdapter();

    void initList();

    void showNoItems();

    void hideNoItems();
}
