package ru.dmitriylebyodkin.shoplist.presenters;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;

import ru.dmitriylebyodkin.shoplist.models.ProductModel;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.views.ProductsView;

@InjectViewState
public class ProductsPresenter extends MvpPresenter<ProductsView> {
    public void init(Context context, int categoryId) {
        List<Product> productList;

        if (categoryId == 0) {
            productList = ProductModel.getAllDesc(context);
        } else {
            productList = ProductModel.getAllDescByCategoryId(context, categoryId);
        }

        if (productList == null || productList.size() == 0) {
            getViewState().showNoItems();
            productList = new ArrayList<>();
        }

        getViewState().setList(productList);
        getViewState().setAdapter();
        getViewState().initList();
    }

    public void remove(Context context, Product product, int position) {
        ProductModel.delete(context, product);

        getViewState().delete(position);
        getViewState().showDeleteToast();
    }

    public void showNoItems() {
        getViewState().showNoItems();
    }

    public void hideNoItems() {
        getViewState().hideNoItems();
    }
}
