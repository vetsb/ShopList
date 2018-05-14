//package ru.dmitriylebyodkin.shoplist.fragments;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.arellomobile.mvp.MvpAppCompatFragment;
//import com.arellomobile.mvp.presenter.InjectPresenter;
//import com.jakewharton.rxbinding2.widget.RxTextView;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import ru.dmitriylebyodkin.shoplist.App;
//import ru.dmitriylebyodkin.shoplist.R;
//import ru.dmitriylebyodkin.shoplist.activities.InfoActivity;
//import ru.dmitriylebyodkin.shoplist.adapters.IItemAdapter;
//import ru.dmitriylebyodkin.shoplist.adapters.SectionAdapter;
//import ru.dmitriylebyodkin.shoplist.data.Section;
//import ru.dmitriylebyodkin.shoplist.models.CategoryModel;
//import ru.dmitriylebyodkin.shoplist.models.ProductModel;
//import ru.dmitriylebyodkin.shoplist.presenters.ListPresenter;
//import ru.dmitriylebyodkin.shoplist.room.data.Category;
//import ru.dmitriylebyodkin.shoplist.room.data.IItem;
//import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
//import ru.dmitriylebyodkin.shoplist.room.data.Product;
//import ru.dmitriylebyodkin.shoplist.views.InfoView;
//import ru.dmitriylebyodkin.shoplist.views.ListView;
//
//public class ListFragment extends MvpAppCompatFragment implements ListView {
//
//    @InjectPresenter
//    ListPresenter presenter;
//
//    @BindView(R.id.etSearch)
//    AutoCompleteTextView etSearch;
//    @BindView(R.id.ivSearch)
//    ImageView ivSearch;
//    @BindView(R.id.ivClear)
//    ImageView ivClear;
//    @BindView(R.id.ivAdd)
//    ImageView ivAdd;
//
//    @BindView(R.id.recyclerView)
//    RecyclerView recyclerView;
//
//    private IListWithItems list;
//    private IItemAdapter adapter;
//    private SectionAdapter sectionAdapter;
//    private List<Section> sectionList;
//    private List<Integer> productIds;
//    private List<Product> productList;
//    private List<String> products;
//    private ArrayAdapter searchAdapter;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View fragmentView = inflater.inflate(R.layout.fragment_list, container, false);
//        ButterKnife.bind(this, fragmentView);
//
//        productIds = new ArrayList<>();
//        for (IItem item: list.getItems()) {
//            productIds.add(item.getProductId());
//        }
//
//        productList = ProductModel.getAll(getActivity());
//        products = new ArrayList<>();
//
//        for (Iterator<Product> iterator = productList.iterator(); iterator.hasNext(); ) {
//            Product product = iterator.next();
//
//            if (productIds.indexOf(product.getId()) == -1) {
//                products.add(product.getTitle());
//            } else {
//                iterator.remove();
//            }
//        }
//
//        searchAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, products);
//
//        etSearch.setAdapter(searchAdapter);
//        etSearch.setOnItemClickListener((parent, view, position, id) -> {
//            int index = products.indexOf(etSearch.getText().toString());
//            presenter.addItem(getActivity(), list.getList(), productList.get(index));
//
//            recyclerView.scrollToPosition(0);
//        });
//        etSearch.setOnEditorActionListener((v, actionId, event) -> {
//            if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && etSearch.getText().toString().trim().length() > 0) {
//                int index = App.indexOfIgnoreCase(products, etSearch.getText().toString());
//
//                if (index == -1) {
//                    presenter.addItem(getActivity(), list.getList(), etSearch.getText().toString());
//                } else {
//                    presenter.addItem(getActivity(), list.getList(), productList.get(index));
//                }
//
//                recyclerView.scrollToPosition(0);
//
//                return true;
//            }
//
//            return false;
//        });
//
//        RxTextView.textChangeEvents(etSearch)
//                .map(text -> text.text().toString())
//                .subscribe(text -> {
//                    if (text.length() > 0) {
//                        presenter.hideSearchIcon();
//                    } else {
//                        presenter.showSearchIcon();
//                    }
//                });
//
//        presenter.init();
//
//        return fragmentView;
//    }
//
//    @OnClick(R.id.ivClear)
//    public void clearSearch() {
//        etSearch.dismissDropDown();
//        etSearch.setText("");
//    }
//
//    @Override
//    public void addProduct(Product product) {
//        productIds.add(product.getId());
//        int index = products.indexOf(product.getTitle());
//
//        if (index != -1) {
//            products.remove(index);
//            productList.remove(index);
//
//            searchAdapter.clear();
//            searchAdapter.addAll(products);
//        }
//
//        adapter.addProduct(product);
//    }
//
//    @Override
//    public void showCost() {
//
//    }
//
//    @Override
//    public void checkAll() {
//        adapter.checkAll();
//    }
//
//    @Override
//    public void resetAll() {
//        adapter.resetAll();
//    }
//
//    @Override
//    public void deleteChecked() {
//        adapter.deleteChecked();
//    }
//
//    @OnClick(R.id.ivAdd)
//    public void addSearchItem() {
//        int index = products.indexOf(etSearch.getText().toString());
//
//        if (index == -1) {
//            presenter.addItem(getActivity(), list.getList(), etSearch.getText().toString());
//        } else {
//            presenter.addItem(getActivity(), list.getList(), productList.get(index));
//        }
//
//        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
//
//        etSearch.clearFocus();
//    }
//
//    @Override
//    public void initAdapter() {
//        productList = ProductModel.getAll(getActivity());
//        groupListByCategory(list.getItems(), productList, CategoryModel.getAll(getActivity()));
//        sectionAdapter = new SectionAdapter(getActivity(), sectionList);
//
//        adapter = new IItemAdapter(getActivity(), list.getItems(), productList, list.getList().isCompleted());
//        sortItems(list.getList().getSortingType()); // list.getList().getSortingType()
//    }
//
//    @Override
//    public void updateList(IListWithItems iListWithItems) {
//        this.list = iListWithItems;
//    }
//
//    public void groupListByCategory(List<IItem> itemList, List<Product> productList, List<Category> categoryList) {
//        Map<Integer, List<IItem>> map = new HashMap<>();
//        int categoryId = 0;
//
//        for (IItem item: itemList) {
//            for (Product product: productList) {
//                if (item.getProductId() == product.getId()) {
//                    categoryId = product.getCategoryId();
//                }
//            }
//
//            if (categoryId != 0) {
//                if (map.containsKey(categoryId)) {
//                    map.get(categoryId).add(item);
//                } else {
//                    List<IItem> list = new ArrayList<>();
//                    list.add(item);
//
//                    map.put(categoryId, list);
//                }
//            }
//        }
//
//        List<Section> sectionList = new ArrayList<>();
//        Section section;
//
//        for (Map.Entry<Integer, List<IItem>> entry : map.entrySet()) {
//            section = new Section();
//
//            for (Category category : categoryList) {
//                if (category.getId() == entry.getKey()) {
//                    section.setTitle(category.getTitle());
//                }
//            }
//
//            section.setAdapter(new IItemAdapter(getActivity(), entry.getValue(), productList, false));
//            sectionList.add(section);
//        }
//
//        this.sectionList = sectionList;
//    }
//
//    @Override
//    public void initList() {
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.setHasFixedSize(true);
//
//        if (adapter != null) {
//            recyclerView.setAdapter(adapter);
//        }
//    }
//
//    @Override
//    public void addItem(IItem item) {
//        adapter.addItem(item);
//        ((InfoActivity) getActivity()).updateSummary();
//    }
//
//    @Override
//    public void showSearchIcon() {
//        ivClear.setVisibility(View.GONE);
//        ivAdd.setVisibility(View.GONE);
//        ivSearch.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void hideSearchIcon() {
//        ivClear.setVisibility(View.VISIBLE);
//        ivAdd.setVisibility(View.VISIBLE);
//        ivSearch.setVisibility(View.GONE);
//    }
//
//    public List<IItem> getItems() {
//        return adapter == null ? null : adapter.getItems();
//    }
//
//    public void updateItem(int position, IItem item) {
//        adapter.updateItem(position, item);
//    }
//
//    public void updateProduct(int productPosition, Product product) {
//        adapter.updateProduct(productPosition, product);
//    }
//
//    public void setList(IListWithItems iListWithItems) {
//        this.list = iListWithItems;
//    }
//
//    public void sortByAlphabet() {
//        List<IItem> itemList = adapter.getItems();
//        List<Product> productList = adapter.getProducts();
//
//        Collections.sort(itemList, (item1, item2) -> {
//            String title1 = null, title2 = null;
//
//            for (Product product: productList) {
//                if (product.getId() == item1.getProductId()) {
//                    title1 = product.getTitle();
//                }
//
//                if (product.getId() == item2.getProductId()) {
//                    title2 = product.getTitle();
//                }
//            }
//
//            if (title1 == null || title2 == null) {
//                return 0;
//            }
//
//            return title1.compareTo(title2);
//        });
//
//        adapter.setItems(itemList);
//
//        if (list.getList().getSortingType() == 1) {
//            recyclerView.setAdapter(adapter);
//        }
//    }
//
//    public void sortByCategory() {
//        groupListByCategory(list.getItems(), ProductModel.getAll(getActivity()), CategoryModel.getAll(getActivity()));
//        sectionAdapter = new SectionAdapter(getActivity(), sectionList);
//
//        recyclerView.setAdapter(sectionAdapter);
//    }
//
//    public void sortByAdd() {
//        List<IItem> itemList = adapter.getItems();
//
//        Collections.sort(itemList, (item1, item2) -> Integer.compare(item2.getId(), item1.getId()));
//
//        adapter.setItems(itemList);
//
//        if (list.getList().getSortingType() == 1) {
//            recyclerView.setAdapter(adapter);
//        }
//    }
//
//    public void sortItems(int sortingType) {
//        switch (sortingType) {
//            case 0:
//                sortByAlphabet();
//                break;
//            case 1:
//                sortByCategory();
//                break;
//            case 2:
//                sortByAdd();
//                break;
//        }
//
//        list.getList().setSortingType(sortingType);
//    }
//}
