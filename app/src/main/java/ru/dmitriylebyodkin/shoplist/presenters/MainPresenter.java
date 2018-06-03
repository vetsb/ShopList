package ru.dmitriylebyodkin.shoplist.presenters;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.models.ListModel;
import ru.dmitriylebyodkin.shoplist.models.ProductModel;
import ru.dmitriylebyodkin.shoplist.room.data.IList;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.views.MainView;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {
    public void updateAdapterItem(int position, IListWithItems iListWithItems) {
        getViewState().updateAdapterItem(position, iListWithItems);
    }

    public void removeAdapterItem(int position) {
        getViewState().removeAdapterItem(position);
    }

    public void setProducts(List<Product> productList) {
        getViewState().setAdapterProducts(productList);
    }

    public void setActivityTitle(String title) {
        getViewState().setActivityTitle(title);
    }

    public void remove(Context context, IList list, int position) {
        ListModel.deleteTemporarily(context, list);

        getViewState().removeAdapterItem(position);
    }
}
