package ru.dmitriylebyodkin.shoplist.presenters;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ru.dmitriylebyodkin.shoplist.models.ListModel;
import ru.dmitriylebyodkin.shoplist.room.data.IItem;
import ru.dmitriylebyodkin.shoplist.room.data.IList;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.views.ListsView;

@InjectViewState
public class ListsPresenter extends MvpPresenter<ListsView> {
    private static final String TAG = "myLogs";

    public void init(Context context) {
        List<IListWithItems> list = ListModel.getWithItems(context);
        List<IItem> itemList;

        if (list == null || list.size() == 0) {
            getViewState().showLayoutNotItems();
        } else {
            for (int i = 0; i < list.size(); i++) {
                itemList = list.get(i).getItems();
                Collections.reverse(itemList);

                for (Iterator<IItem> iterator = itemList.iterator(); iterator.hasNext();) {
                    IItem item = iterator.next();

                    if (item.isFinished()) {
                        iterator.remove();
                    }
                }

                list.get(i).setItems(itemList);
            }
        }

        getViewState().setList(list);
        getViewState().setAdapter();
        getViewState().initList();
    }

    public void setAdapter() {
        getViewState().setAdapter();
    }

    public int createList(Context context, IList list) {
        return (int) ListModel.insert(context, list)[0];
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

    public void initList() {
        getViewState().initList();
    }

    public void removeAdapterItem(int position) {
        getViewState().removeAdapterItem(position);
    }

    public void setProducts(List<Product> productList) {
        getViewState().setAdapterProducts(productList);
    }
}
