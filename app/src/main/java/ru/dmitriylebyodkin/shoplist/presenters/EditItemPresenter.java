package ru.dmitriylebyodkin.shoplist.presenters;

import android.content.Context;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;

import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.activities.EditItemActivity;
import ru.dmitriylebyodkin.shoplist.models.CategoryModel;
import ru.dmitriylebyodkin.shoplist.models.ItemModel;
import ru.dmitriylebyodkin.shoplist.models.ListModel;
import ru.dmitriylebyodkin.shoplist.models.ProductModel;
import ru.dmitriylebyodkin.shoplist.room.data.Category;
import ru.dmitriylebyodkin.shoplist.room.data.IItem;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.views.EditItemView;

@InjectViewState
public class EditItemPresenter extends MvpPresenter<EditItemView> {
    private static final String TAG = "myLogs";

    private List<Category> categoryList = new ArrayList<>();
    private List<String> categories = new ArrayList<>();
    private int categoryIndex = 0;

    public void init(Context context, Product product) {
        if (categories == null || categories.size() == 0) {
            if (categoryList == null || categoryList.size() == 0) {
                categoryList = CategoryModel.getAll(context);
            }

            int index = 0;

            categories.add(context.getString(R.string.not_selected_w));

            for (Category category: categoryList) {
                categories.add(category.getTitle());

                if (category.getId() == product.getCategoryId()) {
                    categoryIndex = index;
                }

                index++;
            }

            if (product.getCategoryId() != 0) {
                categoryIndex++;
            }
        }

        getViewState().setCategories(categories);
        getViewState().setCategoryList(categoryList);
        getViewState().setCategoryIndex(categoryIndex);
    }

    public void updateUpdatedAtList(Context context, int listId) {
        int updatedTimestamp = (int) (System.currentTimeMillis()/1000L);
        ListModel.updateUpdatedAtById(context, listId, updatedTimestamp);
    }

    public void updateItem(Context context, IItem item) {
        ItemModel.update(context, item);
    }

    public void updateProduct(Context context, Product product) {
        ProductModel.update(context, product);
    }
}
