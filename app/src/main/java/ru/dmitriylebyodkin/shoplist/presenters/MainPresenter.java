package ru.dmitriylebyodkin.shoplist.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.views.MainView;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {
    public void setActivityTitle(String title) {
        getViewState().setActivityTitle(title);
    }

    public void smoothScrollToBegin() {
        getViewState().smoothScrollToBegin();
    }

    public void addAdapterItemToBegin(IListWithItems iListWithItems) {
        getViewState().setAdapter();
        getViewState().addAdapterItemToBegin(iListWithItems);
    }

    public void updateAdapterItem(int position, IListWithItems iListWithItems) {
        getViewState().updateAdapterItem(position, iListWithItems);
    }

    public void removeAdapterItem(int position) {
        getViewState().removeAdapterItem(position);
    }

    public void setProducts(List<Product> productList) {
        getViewState().setAdapterProducts(productList);
    }
}
