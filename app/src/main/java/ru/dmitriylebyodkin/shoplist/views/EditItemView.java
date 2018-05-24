package ru.dmitriylebyodkin.shoplist.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.room.data.Category;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface EditItemView extends MvpView {
    void setCategoryIndex(int categoryIndex);

    void setCategories(List<String> categories);

    void setCategoryList(List<Category> categoryList);
}
