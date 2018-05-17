package ru.dmitriylebyodkin.shoplist.views;

import com.arellomobile.mvp.MvpView;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.Product;

public interface ListsView extends MvpView {
    void setList(List<IListWithItems> list);

    void setAdapter();

    void initList();

    void updateAdapterItem(int position, IListWithItems iListWithItems);

    void addAdapterItemToBegin(IListWithItems iListWithItems);

    void setAdapterProducts(List<Product> productList);

    void removeAdapterItem(int position);

    void smoothScrollToBegin();

    void showLayoutNotItems();

    void hideLayoutNotItems();
}
