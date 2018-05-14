package ru.dmitriylebyodkin.shoplist.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import ru.dmitriylebyodkin.shoplist.data.Section;
import ru.dmitriylebyodkin.shoplist.room.data.IItem;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.room.data.Shop;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface InfoView extends MvpView {
    /**
     * Адаптеры и список
     */
    void initItemAdapter();

    void initSectionAdapter();

    void setItemAdapter();

    void setSectionAdapter();

    void initList();


    /**
     * Элементы
     * @param item
     */
    void addAdapterItem(IItem item);

    void setAdapterItem(int position, IItem item);

    void setAdapterItems(List<IItem> itemList);

    void addAdapterProduct(Product product);

    void setAdapterProduct(int productPosition, Product product);

    void sortItems(int sortingType);

    void updateList(IListWithItems iListWithItems);


    /**
     * Поиск
     */
    void showSearchIcon();

    void hideSearchIcon();

    void clearSearch();


    /**
     * Меню
     */
    void checkAll();

    void resetAll();

    void deleteChecked();

    void setActivityTitle(String title);


    /**
     * Оставшиеся и купленные товары. Итог.
     * @param finalSpentSum
     * @param spentCount
     */
    void setSummarySpent(String finalSpentSum, int spentCount);

    void setSummaryLeft(String finalLeftSum, int leftCount);

    void updateSummary();



    void setSectionList(List<Section> sectionList);

    void setShop(Shop shop);
}
