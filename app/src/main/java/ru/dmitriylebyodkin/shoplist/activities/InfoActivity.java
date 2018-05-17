package ru.dmitriylebyodkin.shoplist.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.jakewharton.rxbinding2.widget.RxTextView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.dmitriylebyodkin.shoplist.App;
import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.adapters.IItemAdapter;
import ru.dmitriylebyodkin.shoplist.adapters.SectionAdapter;
import ru.dmitriylebyodkin.shoplist.data.Section;
import ru.dmitriylebyodkin.shoplist.models.CategoryModel;
import ru.dmitriylebyodkin.shoplist.models.ItemModel;
import ru.dmitriylebyodkin.shoplist.models.ListModel;
import ru.dmitriylebyodkin.shoplist.models.ProductModel;
import ru.dmitriylebyodkin.shoplist.presenters.InfoPresenter;
import ru.dmitriylebyodkin.shoplist.room.data.Category;
import ru.dmitriylebyodkin.shoplist.room.data.IItem;
import ru.dmitriylebyodkin.shoplist.room.data.IList;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.room.data.Shop;
import ru.dmitriylebyodkin.shoplist.views.InfoView;

public class InfoActivity extends MvpAppCompatActivity implements InfoView {

    private static final String TAG = "myLogs";

    private static final int EDIT_LIST_CODE = 1;
    public static final int EDIT_ITEM_CODE = 2;
    private static final int ADD_SHOP_CODE = 3;

    @InjectPresenter
    InfoPresenter presenter;

    @BindView(R.id.etSearch)
    AutoCompleteTextView etSearch;
    @BindView(R.id.ivSearch)
    ImageView ivSearch;
    @BindView(R.id.ivClear)
    ImageView ivClear;
    @BindView(R.id.ivAdd)
    ImageView ivAdd;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.layoutSummary)
    FrameLayout layoutSummary;
    @BindView(R.id.tvSpent)
    TextView tvSpent;
    @BindView(R.id.tvLeft)
    TextView tvLeft;
    @BindView(R.id.tvSpentCount)
    TextView tvSpentCount;
    @BindView(R.id.tvLeftCount)
    TextView tvLeftCount;

    @BindView(R.id.bottomNavigation)
    BottomNavigationBar bottomNavigation;

    private Intent intent;
    private IListWithItems list;
    private IItemAdapter adapter;
    private SectionAdapter sectionAdapter;
    private List<Integer> productIds;
    private List<Product> productList = new ArrayList<>();
    private List<String> products;
    private ArrayAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        intent = getIntent();
        list = Parcels.unwrap(intent.getParcelableExtra("list"));
        setTitle(list.getList().getTitle());



        productIds = new ArrayList<>();
        for (IItem item: list.getItems()) {
            productIds.add(item.getProductId());
        }

//        productList = Parcels.unwrap(intent.getParcelableExtra("products"));
        productList = ProductModel.getAll(this);
        products = new ArrayList<>();

        if (productList != null) {
            for (Iterator<Product> iterator = productList.iterator(); iterator.hasNext(); ) {
                Product product = iterator.next();

                if (productIds.indexOf(product.getId()) == -1) {
                    products.add(product.getTitle());
                } else {
                    iterator.remove();
                }
            }
        }

        searchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, products);

        etSearch.setAdapter(searchAdapter);
        etSearch.setOnItemClickListener((parent, view, position, id) -> {
            int index = products.indexOf(etSearch.getText().toString());
            presenter.addItem(this, list.getList(), productList.get(index));

            recyclerView.scrollToPosition(0);
        });
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && etSearch.getText().toString().trim().length() > 0) {
                int index = App.indexOfIgnoreCase(products, etSearch.getText().toString());

                if (index == -1) {
                    presenter.addItem(this, list.getList(), etSearch.getText().toString());
                } else {
                    presenter.addItem(this, list.getList(), productList.get(index));
                }

                recyclerView.scrollToPosition(0);
                sortItems(list.getList().getSortingType());

                return true;
            }

            return false;
        });

        RxTextView.textChangeEvents(etSearch)
                .map(text -> text.text().toString())
                .subscribe(text -> {
                    if (text.length() > 0) {
                        presenter.hideSearchIcon();
                    } else {
                        presenter.showSearchIcon();
                    }
                });

        bottomNavigation.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigation.setAutoHideEnabled(false);
        bottomNavigation
                .addItem(new BottomNavigationItem(R.drawable.list, R.string.sorting)
                        .setActiveColorResource(R.color.colorPrimary)
                        .setInActiveColorResource(R.color.navigation_gray))
                .addItem(new BottomNavigationItem(R.drawable.gps_blue, R.string.shop)
                        .setActiveColorResource(R.color.colorPrimary)
                        .setInActiveColorResource(R.color.navigation_gray))
                .addItem(new BottomNavigationItem(R.drawable.settings, R.string.settings)
                        .setActiveColorResource(R.color.colorPrimary)
                        .setInActiveColorResource(R.color.navigation_gray))
                .initialise();

        bottomNavigation.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                switch (position) {
                    case 1:
                        Intent addShopIntent = new Intent(getApplicationContext(), AddShopActivity.class);
                        addShopIntent.putExtras(intent);
                        startActivityForResult(addShopIntent, ADD_SHOP_CODE);
                        break;
                    case 2:
                        Intent settingsIntent = new Intent(getApplicationContext(), EditListActivity.class);
                        settingsIntent.putExtras(intent);
                        startActivityForResult(settingsIntent, EDIT_LIST_CODE);
                        break;
                }

            }

            @Override
            public void onTabUnselected(int position) {
            }

            @Override
            public void onTabReselected(int position) {
                if (position == 0) {
                    View bottomDialogView = getLayoutInflater().inflate(R.layout.bottom_dialog, null, false);

                    TextView tvSortByAlphabet = bottomDialogView.findViewById(R.id.tvSortByAlphabet);
                    TextView tvSortByCategory = bottomDialogView.findViewById(R.id.tvSortByCategory);
                    TextView tvSortAdd = bottomDialogView.findViewById(R.id.tvSortByAdd);

                    switch (list.getList().getSortingType()) {
                        case 0:
                            tvSortByAlphabet.setTextColor(getResources().getColor(R.color.colorPrimary));
                            break;
                        case 1:
                            tvSortByCategory.setTextColor(getResources().getColor(R.color.colorPrimary));
                            break;
                        case 2:
                            tvSortAdd.setTextColor(getResources().getColor(R.color.colorPrimary));
                            break;
                    }

                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(InfoActivity.this);
                    bottomSheetDialog.setContentView(bottomDialogView);
                    bottomSheetDialog.show();

                    tvSortByAlphabet.setOnClickListener(v -> {
                        setSort(v, 0);

                        tvSortByCategory.setTextColor(Color.parseColor("#5F5F5F"));
                        tvSortAdd.setTextColor(Color.parseColor("#5F5F5F"));

                        bottomSheetDialog.dismiss();
                    });

                    tvSortByCategory.setOnClickListener(v -> {
                        setSort(v, 1);

                        tvSortByAlphabet.setTextColor(Color.parseColor("#5F5F5F"));
                        tvSortAdd.setTextColor(Color.parseColor("#5F5F5F"));

                        bottomSheetDialog.dismiss();
                    });

                    tvSortAdd.setOnClickListener(v -> {
                        setSort(v, 2);

                        tvSortByAlphabet.setTextColor(Color.parseColor("#5F5F5F"));
                        tvSortByCategory.setTextColor(Color.parseColor("#5F5F5F"));

                        bottomSheetDialog.dismiss();
                    });
                }
            }

            void setSort(View v, int sortingType) {
                sortItems(sortingType);

                list.getList().setSortingType(sortingType);
                ListModel.update(InfoActivity.this, list.getList());

                ((TextView) v).setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        presenter.init(list);
    }

    @Override
    public void onResume() {
        super.onResume();

        bottomNavigation.selectTab(0);
    }

    @Override
    public void onBackPressed() {
        list.setItems(adapter.getItems());
        intent.putExtra("list", Parcels.wrap(list));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void initItemAdapter() {
        adapter = new IItemAdapter(this, list.getItems(), ProductModel.getAll(this), list.getList().isCompleted());
    }

    @Override
    public void initSectionAdapter() {
        sectionAdapter = new SectionAdapter(this, new ArrayList<>());
    }

    @Override
    public void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

//        if (adapter != null) {
//            recyclerView.setAdapter(adapter);
//        }
    }

    @Override
    public void addAdapterItem(IItem item) {
        adapter.addItem(item);
        presenter.updateSummary(list);
    }

    @Override
    public void setAdapterItem(int position, IItem item) {
        adapter.setItem(position, item);
    }

    @Override
    public void addAdapterProduct(Product product) {
        productIds.add(product.getId());
        int index = products.indexOf(product.getTitle());

        if (index != -1) {
            products.remove(index);
            productList.remove(index);

            searchAdapter.clear();
            searchAdapter.addAll(products);
        }

        adapter.addProduct(product);
    }

    @Override
    public void showSearchIcon() {
        ivClear.setVisibility(View.GONE);
        ivAdd.setVisibility(View.GONE);
        ivSearch.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSearchIcon() {
        ivClear.setVisibility(View.VISIBLE);
        ivAdd.setVisibility(View.VISIBLE);
        ivSearch.setVisibility(View.GONE);
    }

    @Override
    @OnClick(R.id.ivClear)
    public void clearSearch() {
        etSearch.dismissDropDown();
        etSearch.setText("");
    }

    @Override
    public void checkAll() {
        adapter.checkAll();
    }

    @Override
    public void resetAll() {
        adapter.resetAll();
    }

    @Override
    public void deleteChecked() {
        adapter.deleteChecked();
    }

    @OnClick(R.id.ivAdd)
    public void addSearchItem() {
        int index = products.indexOf(etSearch.getText().toString());

        if (index == -1) {
            presenter.addItem(this, list.getList(), etSearch.getText().toString());
        } else {
            presenter.addItem(this, list.getList(), productList.get(index));
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);

        etSearch.clearFocus();
    }

    @Override
    public void updateList(IListWithItems iListWithItems) {
        this.list = iListWithItems;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navEdit:
                Intent editIntent = new Intent(this, EditListActivity.class);
                editIntent.putExtras(intent);
                startActivityForResult(editIntent, EDIT_LIST_CODE);
                break;
            case R.id.navCheckAll:
                presenter.checkAll();
                break;
            case R.id.navResetAll:
                presenter.resetAll();
                break;
            case R.id.navDeleteChecked:
                presenter.deleteChecked();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_LIST_CODE) {
            if (resultCode == RESULT_OK) {
                list = Parcels.unwrap(data.getParcelableExtra("list"));
                presenter.setActivityTitle(this, list.getList().getTitle());

                intent.putExtra("list", Parcels.wrap(list));
            }

            if (resultCode == App.RESULT_DELETE) {
                setResult(App.RESULT_DELETE, intent);
                finish();
            }
        }

        if (requestCode == EDIT_ITEM_CODE && resultCode == RESULT_OK) {
            int position = data.getIntExtra("position", -1);

            if (position != -1) {
                IItem item = Parcels.unwrap(data.getParcelableExtra("item"));

                Product product = Parcels.unwrap(data.getParcelableExtra("product"));
                int productPosition = data.getIntExtra("product_position", -1);

                presenter.setAdapterItem(position, item);

                if (productPosition != -1) {
                    presenter.setAdapterProduct(productPosition, product);
                }

                int updatedTimestamp = data.getIntExtra("updated_timestamp", 0);

                if (updatedTimestamp != 0) {
                    list.getList().setUpdatedAt(updatedTimestamp);
                    intent.putExtra("list", Parcels.wrap(list));
                }

                sortItems(list.getList().getSortingType());

                presenter.updateSummary(list);
            }
        }

        if (requestCode == ADD_SHOP_CODE && resultCode == App.RESULT_ADD) {
            IListWithItems newList = Parcels.unwrap(data.getParcelableExtra("list"));
            int shopId = newList.getList().getShopId();

            /**
             * Если выбран новый магазин, то пройтись по всем элементам, которые покупались в том же магазине.
             * Определить самую часто встречаемую стоимость и установить ее тем элементам текущего списка, у которых нет цены и которые еще не куплены.
             */
            if (shopId != 0 && shopId != list.getList().getShopId()) {
                List<IList> listList = ListModel.getByShopId(this, shopId);
                IItem iItem;
                HashMap<Float, Integer> hash;

                float maxCost; // самая часто встречаемая цена
                int maxValue; // максимальное количество

                for (IItem item: list.getItems()) {
                    if (item.getCost() == 0 && !item.isBought()) {
                        hash = new HashMap<>();

                        for (IList iList: listList) {
                            iItem = ItemModel.getByListAndProductIds(this, iList.getId(), item.getProductId());

                            if (iItem != null && iItem.getCost() != 0) {
                                if (hash.get(iItem.getCost()) == null) {
                                    hash.put(iItem.getCost(), 1);
                                } else {
                                    hash.put(iItem.getCost(), hash.get(iItem.getCost())+1);
                                }
                            }
                        }

                        maxCost = 0;
                        maxValue = 0;

                        if (hash.size() == 0) {
                            maxCost = 0;
                        } else if (hash.size() == 1) {
                            maxCost = item.getCost();
                        } else {
                            for (Map.Entry<Float, Integer> entry : hash.entrySet()) {
                                if (entry.getValue() > maxValue) {
                                    maxCost = entry.getKey();
                                    maxValue = entry.getValue();
                                }
                            }
                        }

                        item.setCost(maxCost);
                        ItemModel.update(this, item);
                    }
                }

                list.getList().setShopId(shopId);
            }

            presenter.setAdapterItems(list.getItems());
            intent.putExtra("list", Parcels.wrap(list));
        }
    }

    @Override
    public void setShop(Shop shop) {
        list.getList().setShopId(shop.getId());
        presenter.update(this, list.getList());
    }

    @Override
    public void setUpdatedTimestamp(int timestamp) {
        list.getList().setUpdatedAt(timestamp);
        intent.putExtra("list", Parcels.wrap(list));
    }

    @Override
    public void setActivityTitle(String title) {
        setTitle(title);
    }

    @Override
    public void setSummarySpent(String finalSpentSum, int spentCount) {
        tvSpent.setText(finalSpentSum + " \u20BD");
        tvSpentCount.setText("("+String.valueOf(spentCount)+")");
    }

    @Override
    public void setSummaryLeft(String finalLeftSum, int leftCount) {
        tvLeft.setText(finalLeftSum + " \u20BD");
        tvLeftCount.setText("("+String.valueOf(leftCount)+")");
    }

    @Override
    public void updateSummary() {
        presenter.updateSummary(list);
    }

    @Override
    public void setAdapterItems(List<IItem> itemList) {
        adapter.setItems(itemList);
    }

    @Override
    public void setItemAdapter() {
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setSectionAdapter() {
        recyclerView.setAdapter(sectionAdapter);
    }

    @Override
    public void setSectionList(List<Section> sectionList) {
        sectionAdapter.setList(sectionList);
    }

    @Override
    public void setAdapterProduct(int position, Product product) {
        adapter.setProduct(position, product);
    }

    @Override
    public void sortItems(int sortingType) {
        switch (sortingType) {
            case 0:
                presenter.sortByAlphabet(list.getItems(), ProductModel.getAll(this));
                break;
            case 1:
                presenter.sortByCategory(this, list.getItems(), ProductModel.getAll(this), CategoryModel.getAll(this));
                break;
            case 2:
                presenter.sortByAdd(list.getItems());
                break;
        }

        list.getList().setSortingType(sortingType);
    }
}
