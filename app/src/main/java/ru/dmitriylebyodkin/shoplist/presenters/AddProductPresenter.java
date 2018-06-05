package ru.dmitriylebyodkin.shoplist.presenters;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;

import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.models.CategoryModel;
import ru.dmitriylebyodkin.shoplist.models.ProductModel;
import ru.dmitriylebyodkin.shoplist.room.data.Category;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.views.EditItemView;

@InjectViewState
public class AddProductPresenter extends MvpPresenter<EditItemView> {
    private List<Category> categoryList = new ArrayList<>();
    private List<String> categories = new ArrayList<>();
    private int categoryIndex = 0;

    public void init(Context context, Product product) {
        if (categories == null || categories.size() == 0) {
            if (categoryList == null || categoryList.size() == 0) {
                categoryList = CategoryModel.getAllDesc(context);
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

    public void update(Context context, Product product) {
        ProductModel.update(context, product);
    }

    public long[] insert(Context context, Product product) {
        return ProductModel.insert(context, product);
    }
}
