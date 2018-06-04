package ru.dmitriylebyodkin.shoplist.presenters;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.models.ListModel;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.views.CartView;

@InjectViewState
public class CartPresenter extends MvpPresenter<CartView> {
    private static final String TAG = "myLogs";

    public void init(Context context) {
        List<IListWithItems> lists = ListModel.getDeletedWithItems(context);

        if (lists.size() == 0) {
            getViewState().showNoItems();
        }

        getViewState().setLists(lists);
        getViewState().setAdapter();
        getViewState().initList();
    }
}
