package ru.dmitriylebyodkin.shoplist.presenters;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.activities.InfoActivity;
import ru.dmitriylebyodkin.shoplist.adapters.IItemAdapter;
import ru.dmitriylebyodkin.shoplist.data.Section;
import ru.dmitriylebyodkin.shoplist.models.ItemModel;
import ru.dmitriylebyodkin.shoplist.models.ListModel;
import ru.dmitriylebyodkin.shoplist.models.ProductModel;
import ru.dmitriylebyodkin.shoplist.room.data.Category;
import ru.dmitriylebyodkin.shoplist.room.data.IItem;
import ru.dmitriylebyodkin.shoplist.room.data.IList;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.views.InfoView;

@InjectViewState
public class InfoPresenter extends MvpPresenter<InfoView> {

    public void init(IListWithItems list) {
        getViewState().initItemAdapter();
        getViewState().initSectionAdapter();
        getViewState().sortItems(list.getList().getSortingType());
        updateSummary(list);
        getViewState().initList();
    }

    public void hideSearchIcon() {
        getViewState().hideSearchIcon();
    }

    public void showSearchIcon() {
        getViewState().showSearchIcon();
    }

    public void checkAll() {
        getViewState().checkAll();
    }

    public void resetAll() {
        getViewState().resetAll();
    }

    public void deleteChecked() {
        getViewState().deleteChecked();
    }

    /**
     * Обновляет количество и стоимость купленного и оставшегося
     * @param list
     */
    public void updateSummary(IListWithItems list) {
        List<IItem> itemList;

        float spentSum = 0;
        int spentCount = 0;
        float leftSum = 0;
        int leftCount = 0;

        if (list.getItems() == null) {
            itemList = list.getItems();
        } else {
            itemList = list.getItems();
        }

        for (IItem item: itemList) {
            if (item.isBought()) {
                spentSum += item.getCost();
                spentCount++;
            } else {
                leftSum += item.getCost();
                leftCount++;
            }
        }

        String finalSpentSum, finalLeftSum;

        if (spentSum - (int) spentSum == 0) {
            finalSpentSum = String.valueOf((int) spentSum);
        } else {
            finalSpentSum = String.valueOf(leftSum);
        }

        if (leftSum - (int) leftSum == 0) {
            finalLeftSum = String.valueOf((int) leftSum);
        } else {
            finalLeftSum = String.valueOf(leftSum);
        }

        getViewState().setSummarySpent(finalSpentSum, spentCount);
        getViewState().setSummaryLeft(finalLeftSum, leftCount);
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
        getViewState().addAdapterItem(item);
        getViewState().addAdapterProduct(product);
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

    public void update(Context context, IList list) {
        ListModel.update(context, list);
    }

    public void sortByAlphabet(List<IItem> itemList, List<Product> productList) {
        Collections.sort(itemList, (item1, item2) -> {
            String title1 = null, title2 = null;

            for (Product product: productList) {
                if (product.getId() == item1.getProductId()) {
                    title1 = product.getTitle();
                }

                if (product.getId() == item2.getProductId()) {
                    title2 = product.getTitle();
                }
            }

            if (title1 == null || title2 == null) {
                return 0;
            }

            return title1.compareTo(title2);
        });

        getViewState().setAdapterItems(itemList);
        getViewState().setItemAdapter();
    }

    public void sortByAdd(List<IItem> itemList) {
        Collections.sort(itemList, (item1, item2) -> Integer.compare(item2.getId(), item1.getId()));

        getViewState().setAdapterItems(itemList);
        getViewState().setItemAdapter();
    }

    public void sortByCategory(Context context, List<IItem> itemList, List<Product> productList, List<Category> categoryList) {
        Map<Integer, List<IItem>> map = new HashMap<>();
        int categoryId = 0;

        for (IItem item: itemList) {
            for (Product product: productList) {
                if (item.getProductId() == product.getId()) {
                    categoryId = product.getCategoryId();
                }
            }

            if (map.containsKey(categoryId)) {
                map.get(categoryId).add(item);
            } else {
                List<IItem> list = new ArrayList<>();
                list.add(item);

                map.put(categoryId, list);
            }
        }

        for (Map.Entry<Integer, List<IItem>> entry : map.entrySet()) {
            Collections.sort(entry.getValue(), (item1, item2) -> {
                String title1 = null, title2 = null;

                for (Product product: productList) {
                    if (product.getId() == item1.getProductId()) {
                        title1 = product.getTitle();
                    }

                    if (product.getId() == item2.getProductId()) {
                        title2 = product.getTitle();
                    }
                }

                if (title1 == null || title2 == null) {
                    return 0;
                }

                return title1.compareTo(title2);
            });
        }

        List<Section> sectionList = new ArrayList<>();
        Section section;

        for (Map.Entry<Integer, List<IItem>> entry : map.entrySet()) {
            section = new Section();

            for (Category category : categoryList) {
                if (category.getId() == entry.getKey()) {
                    section.setTitle(category.getTitle());
                }
            }

            section.setAdapter(new IItemAdapter(context, entry.getValue(), productList, false));
            sectionList.add(section);
        }

        getViewState().setSectionList(sectionList);
        getViewState().setSectionAdapter();
    }

    public void setAdapterItem(int position, IItem item) {
        getViewState().setAdapterItem(position, item);
    }

    public void setAdapterProduct(int position, Product product) {
        getViewState().setAdapterProduct(position, product);
    }

    public void setActivityTitle(Context context, String title) {
        if (title.trim().equalsIgnoreCase("")) {
            title = context.getResources().getString(R.string.list_activity_title);
        }

        getViewState().setActivityTitle(title);
    }

    public void setAdapterItems(List<IItem> items) {
        getViewState().setAdapterItems(items);
    }

    public void deleteItem(Context context, IItem item, int position) {
        int timestamp = (int) (System.currentTimeMillis()/1000L);

        ListModel.updateUpdatedAtById(context, item.getListId(), timestamp);
        ItemModel.delete(context, item);

        getViewState().deleteAdapterItem(item, position);
        getViewState().updateSummary();
        getViewState().setUpdatedTimestamp(timestamp);
    }
}
