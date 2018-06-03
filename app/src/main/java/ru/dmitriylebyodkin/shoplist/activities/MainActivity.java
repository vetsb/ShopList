package ru.dmitriylebyodkin.shoplist.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

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
import ru.dmitriylebyodkin.shoplist.fragments.CartFragment;
import ru.dmitriylebyodkin.shoplist.fragments.CategoriesFragment;
import ru.dmitriylebyodkin.shoplist.fragments.ListsFragment;
import ru.dmitriylebyodkin.shoplist.fragments.ProductsFragment;
import ru.dmitriylebyodkin.shoplist.models.ProductModel;
import ru.dmitriylebyodkin.shoplist.presenters.MainPresenter;
import ru.dmitriylebyodkin.shoplist.room.data.Category;
import ru.dmitriylebyodkin.shoplist.room.data.IList;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.views.MainView;

public class MainActivity extends MvpAppCompatActivity implements MainView {

    private static final String TAG = "myLogs";
    public static final int LIST_ACTIVITY_CODE = 1;
    public static final int EDIT_LIST_ACTIVITY_CODE = 2;
    public static final int EDIT_PRODUCT_CODE = 3;
    public static final int ADD_PRODUCT_CODE = 4;
    public static final int LIST_PRODUCTS_CODE = 5;

    @InjectPresenter
    MainPresenter presenter;

    @BindView(R.id.bottomNavigation)
    BottomNavigationBar bottomNavigation;

    private ListsFragment listsFragment;
    private ProductsFragment productsFragment;
    private CategoriesFragment categoriesFragment;
    private CartFragment cartFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        App.initApp(this);

        bottomNavigation.setMode(BottomNavigationBar.MODE_FIXED_NO_TITLE);
        bottomNavigation.setAutoHideEnabled(false);
        bottomNavigation.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        bottomNavigation
                .addItem(new BottomNavigationItem(R.drawable.list, ""))
                .addItem(new BottomNavigationItem(R.drawable.cart, ""))
                .addItem(new BottomNavigationItem(R.drawable.pattern, ""))
                .addItem(new BottomNavigationItem(R.drawable.delete_blue, ""))
                .addItem(new BottomNavigationItem(R.drawable.settings, ""))
                .initialise();

        List<Product> productList = ProductModel.getAll(this);

        listsFragment = new ListsFragment();
        listsFragment.setProducts(productList);

        productsFragment = new ProductsFragment();

        cartFragment = new CartFragment();
        cartFragment.setProducts(productList);


        categoriesFragment = new CategoriesFragment();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frameLayout, listsFragment)
                .commit();

        bottomNavigation.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                String title = getTitle().toString();

                switch (position) {
                    case 0:
                        title = getString(R.string.lists);
                        fragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, listsFragment)
                                .commit();
                        break;
                    case 1:
                        title = getString(R.string.products);
                        fragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, productsFragment)
                                .commit();
                        break;
                    case 2:
                        title = getString(R.string.categories);
                        fragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, categoriesFragment)
                                .commit();
                        break;
                    case 3:
                        title = getString(R.string.trash);
                        fragmentManager.beginTransaction()
                                .replace(R.id.frameLayout, cartFragment)
                                .commit();
                        break;
                    case 4:
                        break;
                }

                presenter.setActivityTitle(title);
            }

            @Override
            public void onTabUnselected(int position) {
            }

            @Override
            public void onTabReselected(int position) {

            }
        });

//        IListWithItems iListWithItems = ListModel.getWithItems(this).get(0);
//
//        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent alarmIntent = new Intent(this, TimeNotification.class);
//        alarmIntent.putExtra("list", Parcels.wrap(iListWithItems));
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//        am.cancel(pendingIntent);
//        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+15000, pendingIntent);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LIST_ACTIVITY_CODE) {
            int position = data.getIntExtra("position", -1);

            if (resultCode == RESULT_OK) {
                IListWithItems iListWithItems = Parcels.unwrap(data.getParcelableExtra("list"));

                if (position == -1) {
                    /**
                     * Если нет ни одного элемента, создать список и положить туда первый элемент.
                     * Иначе добавить в адаптер
                     */
//                    presenter.addAdapterItemToBegin(iListWithItems);
//                    presenter.smoothScrollToBegin();
                } else {
                    presenter.updateAdapterItem(position, iListWithItems);
                    presenter.setProducts(ProductModel.getAll(this));
                }
            }

            if (resultCode == App.RESULT_DELETE) {
                if (position != -1) {
                    presenter.removeAdapterItem(position);
                }
            }
        }

        if (requestCode == EDIT_LIST_ACTIVITY_CODE) {
            int position = data.getIntExtra("position", -1);

            if (position != -1) {
                if (resultCode == RESULT_OK) {
                    IListWithItems list = Parcels.unwrap(data.getParcelableExtra("list"));
                    listsFragment.updateAdapterItem(position, list);
                }

                if (resultCode == App.RESULT_DELETE) {
                    listsFragment.removeAdapterItem(position);
                }
            }
        }

        if (requestCode == EDIT_PRODUCT_CODE) {
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra("position", -1);
                Product product = Parcels.unwrap(data.getParcelableExtra("product"));

                if (position != -1) {
                    productsFragment.update(product, position);
                }
            }
        }

        if (requestCode == ADD_PRODUCT_CODE) {
            if (resultCode == RESULT_OK) {
                Product product = Parcels.unwrap(data.getParcelableExtra("product"));
                productsFragment.insert(product);
            }
        }

        if (requestCode == LIST_PRODUCTS_CODE) {
            if (resultCode == RESULT_OK) {
                productsFragment.updateList();
            }
        }
    }

    @Override
    public void setActivityTitle(String title) {
        setTitle(title);
    }

    @Override
    public void smoothScrollToBegin() {
        listsFragment.smoothScrollToBegin();
    }

    @Override
    public void setAdapter() {
        listsFragment.setAdapter();
    }

    @Override
    public void addAdapterItemToBegin(IListWithItems iListWithItems) {
        listsFragment.addAdapterItemToBegin(iListWithItems);
    }

    @Override
    public void updateAdapterItem(int position, IListWithItems iListWithItems) {
        listsFragment.updateAdapterItem(position, iListWithItems);
    }

    @Override
    public void removeAdapterItem(int position) {
        listsFragment.removeAdapterItem(position);

        if (listsFragment.getItemCount() == 0) {
            listsFragment.showNoItems();
        }

        Toast.makeText(this, R.string.is_removed, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setAdapterProducts(List<Product> productList) {
        listsFragment.setAdapterProducts(productList);
    }

    @Override
    public void showListNoItems() {
        listsFragment.showNoItems();
    }

    @Override
    public void showCartNoItems() {
        cartFragment.showNoItems();
    }

    @Override
    public void showCategoryEditDialog(Category category, int position) {
        categoriesFragment.showEditDialog(category, position);
    }

    @Override
    public void showCategoryDeleteDialog(Category category, int position) {
        categoriesFragment.showDeleteDialog(category, position);
    }

    @Override
    public void showListDeleteDialog(IList list, int position) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.deleting)
                .setMessage(R.string.deleting_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> presenter.remove(getApplicationContext(), list, position))
                .setNegativeButton(R.string.no, null)
                .create();

        alertDialog.show();
    }

    @Override
    public void showProductDeleteDialog(Product product, int position) {
        productsFragment.showDeleteDialog(product, position);
    }
}
