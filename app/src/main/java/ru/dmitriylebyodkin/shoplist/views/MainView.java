package ru.dmitriylebyodkin.shoplist.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface MainView extends MvpView {
    void setList(List<IListWithItems> list);

    void setAdapter();

    void initList();

    void updateAdapterItem(int position, IListWithItems iListWithItems);

    void addAdapterItemToBegin(IListWithItems iListWithItems);

    void removeAdapterItem(int position);

    void smoothScrollToBegin();

    void showLayoutNotItems();

    void hideLayoutNotItems();
}
