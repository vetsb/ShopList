package ru.dmitriylebyodkin.shoplist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dmitriylebyodkin.shoplist.App;
import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.fragments.AddShopFragment;
import ru.dmitriylebyodkin.shoplist.fragments.ListFragment;
import ru.dmitriylebyodkin.shoplist.models.ListModel;
import ru.dmitriylebyodkin.shoplist.presenters.InfoPresenter;
import ru.dmitriylebyodkin.shoplist.room.data.IItem;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.room.data.Shop;
import ru.dmitriylebyodkin.shoplist.views.InfoView;

public class InfoActivity extends MvpAppCompatActivity implements InfoView {

    private static final String TAG = "myLogs";

    private static final int EDIT_LIST_CODE = 1;
    public static final int EDIT_ITEM_CODE = 2;
    public static final int MAP_CODE = 3;
    private static final int ADD_SHOP_CODE = 4;

    @InjectPresenter
    InfoPresenter presenter;

    @BindView(R.id.container)
    FrameLayout container;

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
    private ListFragment listFragment;
    private AddShopFragment addShopFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        intent = getIntent();
        list = Parcels.unwrap(intent.getParcelableExtra("list"));
        setTitle(list.getList().getTitle());

        listFragment = new ListFragment();
        listFragment.setList(list);

        addShopFragment = new AddShopFragment();
        addShopFragment.setList(list);

        updateSummary();

        fragmentManager = getSupportFragmentManager();

        bottomNavigation.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigation.setAutoHideEnabled(false);
        bottomNavigation
                .addItem(new BottomNavigationItem(R.drawable.list, R.string.list)
                        .setActiveColorResource(R.color.colorPrimary)
                        .setInActiveColorResource(R.color.navigation_gray))
                .addItem(new BottomNavigationItem(R.drawable.gps_blue, R.string.shop)
                        .setActiveColorResource(R.color.colorPrimary)
                        .setInActiveColorResource(R.color.navigation_gray))
                .addItem(new BottomNavigationItem(R.drawable.settings, R.string.settings)
                        .setActiveColorResource(R.color.colorPrimary)
                        .setInActiveColorResource(R.color.navigation_gray))
                .initialise();

        fragmentManager.beginTransaction()
                .replace(R.id.container, listFragment)
                .commit();

        bottomNavigation.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                Fragment fragment;
                String title;

                switch (position) {
                    default:
                    case 0:
                        fragment = listFragment;
                        title = list.getList().getTitle();
                        presenter.replaceFragment(fragment, title);
                        break;
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
//                if (position == 0) {
//                    View bottomDialogView = getLayoutInflater().inflate(R.layout.bottom_dialog, null, false);
//
//                    TextView tvSortByAlphabet = bottomDialogView.findViewById(R.id.tvSortByAlphabet);
//                    TextView tvSortByCategory = bottomDialogView.findViewById(R.id.tvSortByCategory);
//                    TextView tvSortAdd = bottomDialogView.findViewById(R.id.tvSortByAdd);
//
//                    switch (list.getList().getSortingType()) {
//                        case 0:
//                            tvSortByAlphabet.setTextColor(getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 1:
//                            tvSortByCategory.setTextColor(getResources().getColor(R.color.colorPrimary));
//                            break;
//                        case 2:
//                            tvSortAdd.setTextColor(getResources().getColor(R.color.colorPrimary));
//                            break;
//                    }
//
//                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(InfoActivity.this);
//                    bottomSheetDialog.setContentView(bottomDialogView);
//                    bottomSheetDialog.show();
//
//                    tvSortByAlphabet.setOnClickListener(v -> {
//                        setSort(v, 0);
//
//                        tvSortByCategory.setTextColor(Color.parseColor("#5F5F5F"));
//                        tvSortAdd.setTextColor(Color.parseColor("#5F5F5F"));
//
//                        bottomSheetDialog.dismiss();
//                    });
//
//                    tvSortByCategory.setOnClickListener(v -> {
//                        setSort(v, 1);
//
//                        tvSortByAlphabet.setTextColor(Color.parseColor("#5F5F5F"));
//                        tvSortAdd.setTextColor(Color.parseColor("#5F5F5F"));
//
//                        bottomSheetDialog.dismiss();
//                    });
//
//                    tvSortAdd.setOnClickListener(v -> {
//                        setSort(v, 2);
//
//                        tvSortByAlphabet.setTextColor(Color.parseColor("#5F5F5F"));
//                        tvSortByCategory.setTextColor(Color.parseColor("#5F5F5F"));
//
//                        bottomSheetDialog.dismiss();
//                    });
//                }
            }

            void setSort(View v, int sortingType) {
                listFragment.sortItems(sortingType);

                list.getList().setSortingType(sortingType);
                ListModel.update(InfoActivity.this, list.getList());

                ((TextView) v).setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        bottomNavigation.selectTab(0);
    }

    @Override
    public void onBackPressed() {
        list.setItems(listFragment.getItems());
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
    public void setActivityTitle(String title) {
        setTitle(title);
    }

    @Override
    public void checkAll() {
        listFragment.checkAll();
    }

    @Override
    public void resetAll() {
        listFragment.resetAll();
    }

    @Override
    public void deleteChecked() {
        listFragment.deleteChecked();
    }

    @Override
    public void replaceFragment(Fragment fragment, String title) {
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
        setTitle(title);
    }

    @Override
    public void updateList(IListWithItems iListWithItems) {
        listFragment.updateList(iListWithItems);
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
                presenter.updateList(this, list);
                intent.putExtra("list", Parcels.wrap(Parcels.unwrap(data.getParcelableExtra("list"))));
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

                listFragment.updateItem(position, item);

                if (productPosition != -1) {
                    listFragment.updateProduct(productPosition, product);
                }

                updateSummary();
            }
        }

        if (requestCode == ADD_SHOP_CODE && resultCode == App.RESULT_ADD) {
            IListWithItems newList = Parcels.unwrap(data.getParcelableExtra("list"));
            list.getList().setShopId(newList.getList().getShopId());
            intent.putExtra("list", Parcels.wrap(list));
        }
    }

    public void setShop(Shop shop) {
        addShopFragment.setShop(shop);
        list.getList().setShopId(shop.getId());

        ListModel.update(this, list.getList());
    }

    public void updateSummary() {
        List<IItem> itemList;

        float spentSum = 0;
        int spentCount = 0;
        float leftSum = 0;
        int leftCount = 0;

        if (listFragment.getItems() == null) {
            itemList = list.getItems();
        } else {
            itemList = listFragment.getItems();
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

        tvSpent.setText(finalSpentSum + " \u20BD");
        tvLeft.setText(finalLeftSum + " \u20BD");

        tvSpentCount.setText("("+String.valueOf(spentCount)+")");
        tvLeftCount.setText("("+String.valueOf(leftCount)+")");
    }
}
