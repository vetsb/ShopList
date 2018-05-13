package ru.dmitriylebyodkin.shoplist.views;

import android.support.v4.app.Fragment;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import ru.dmitriylebyodkin.shoplist.room.data.IItem;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.Product;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface InfoView extends MvpView {
    void setActivityTitle(String title);

    void checkAll();

    void resetAll();

    void deleteChecked();

    void replaceFragment(Fragment fragment, String title);

    void updateList(IListWithItems iListWithItems);
}
