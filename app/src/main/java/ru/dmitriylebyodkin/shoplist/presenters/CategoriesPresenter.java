package ru.dmitriylebyodkin.shoplist.presenters;

import android.app.AlertDialog;
import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.models.CategoryModel;
import ru.dmitriylebyodkin.shoplist.models.ProductModel;
import ru.dmitriylebyodkin.shoplist.room.data.Category;
import ru.dmitriylebyodkin.shoplist.views.CategoriesView;

@InjectViewState
public class CategoriesPresenter extends MvpPresenter<CategoriesView> {
    public void init(Context context) {
        List<Category> categoryList = CategoryModel.getAll(context);

        if (categoryList == null || categoryList.size() == 0) {
            getViewState().showNoItems();
        }

        getViewState().setCategoryList(categoryList);
        getViewState().setAdapter();
        getViewState().initList();
    }

    public void update(Context context, Category category, int position) {
        CategoryModel.update(context, category);
        getViewState().updateAdapterItem(position, category);
    }

    /**
     * Удаляет категорию и у товаров, у которых была эта категория, устанавливает categoryId = 0
     * @param context
     * @param category
     * @param position
     */
    public void remove(Context context, Category category, int position) {
        CategoryModel.delete(context, category);
        ProductModel.setNullCategoryId(context, category.getId());

        getViewState().removeAdapterItem(position);
    }

    public void showNoItems() {
        getViewState().showNoItems();
    }

    public void hideNoItems() {
        getViewState().hideNoItems();
    }

    public void create(Context context, Category category) {
        CategoryModel.insert(context, category);

        getViewState().addAdapterItem(category);
    }
}
