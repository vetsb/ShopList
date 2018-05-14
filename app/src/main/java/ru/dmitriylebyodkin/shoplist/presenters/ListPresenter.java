package ru.dmitriylebyodkin.shoplist.presenters;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import ru.dmitriylebyodkin.shoplist.models.ItemModel;
import ru.dmitriylebyodkin.shoplist.models.ListModel;
import ru.dmitriylebyodkin.shoplist.models.ProductModel;
import ru.dmitriylebyodkin.shoplist.room.data.IItem;
import ru.dmitriylebyodkin.shoplist.room.data.IList;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.views.ListView;

@InjectViewState
public class ListPresenter extends MvpPresenter<ListView> {

    public void init() {
        getViewState().initAdapter();
        getViewState().initList();
    }

    public void hideSearchIcon() {
        getViewState().hideSearchIcon();
    }

    public void showSearchIcon() {
        getViewState().showSearchIcon();
    }

    /**
     * Если передается Product, значит такой товар уже есть.
     * Поэтому создать IItem с данным productId.
     * Обновить у списка updatedAt.
     * Добавить в Adapter.
     * @param list
     * @param product
     */
    public void addItem(Context context, IList list, Product product) {
        IItem item = new IItem();
        item.setListId(list.getId());
        item.setProductId(product.getId());
        item.setCount(1);

        int id = (int) ItemModel.insert(context, item)[0];
        item.setId(id);

        list.setUpdatedAt((int) (System.currentTimeMillis()/1000L));
        ListModel.update(context, list);

        getViewState().clearSearch();
        getViewState().addItem(item);
        getViewState().addProduct(product);
    }

    /**
     * Если передается String, значит такого товара нет, поэтому нужно сначала его создать, потом создать IItem, а потом добавить в список
     * @param productTitle
     */
    public void addItem(Context context, IList list, String productTitle) {
        Product product = new Product();
        product.setTitle(productTitle);

        int id = (int) ProductModel.insert(context, product)[0];
        product.setId(id);

        addItem(context, list, product);
    }
}
