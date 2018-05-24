package ru.dmitriylebyodkin.shoplist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dmitriylebyodkin.shoplist.App;
import ru.dmitriylebyodkin.shoplist.fragments.ListsFragment;
import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.fragments.PatternsFragment;
import ru.dmitriylebyodkin.shoplist.models.ProductModel;
import ru.dmitriylebyodkin.shoplist.presenters.MainPresenter;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.views.MainView;

public class MainActivity extends MvpAppCompatActivity implements MainView {

    private static final String TAG = "myLogs";
    public static final int LIST_ACTIVITY_CODE = 1;
    public static final int EDIT_LIST_ACTIVITY_CODE = 2;

    @InjectPresenter
    MainPresenter presenter;

//    @BindView(R.id.bottomNavigation)
//    BottomNavigationBar bottomNavigation;

    private ListsFragment listsFragment;
    private PatternsFragment patternsFragment;
    private FragmentManager fragmentManager;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        App.initApp(this);

//        bottomNavigation.setMode(BottomNavigationBar.MODE_FIXED);
//        bottomNavigation.setAutoHideEnabled(false);
//        bottomNavigation
//                .addItem(new BottomNavigationItem(R.drawable.list, R.string.lists)
//                        .setActiveColorResource(R.color.colorPrimary)
//                        .setInActiveColorResource(R.color.navigation_gray))
//                .addItem(new BottomNavigationItem(R.drawable.pattern, R.string.patterns)
//                        .setActiveColorResource(R.color.colorPrimary)
//                        .setInActiveColorResource(R.color.navigation_gray))
//                .initialise();

        listsFragment = new ListsFragment();
        patternsFragment = new PatternsFragment();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frameLayout, listsFragment)
                .commit();

//        bottomNavigation.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(int position) {
//                switch (position) {
//                    case 0:
//                        title = getString(R.string.lists);
//                        fragmentManager.beginTransaction()
//                                .replace(R.id.frameLayout, listsFragment)
//                                .commit();
//                        break;
//                    case 1:
//                        title = getString(R.string.patterns);
//                        fragmentManager.beginTransaction()
//                                .replace(R.id.frameLayout, patternsFragment)
//                                .commit();
//                        break;
//                }
//
//                presenter.setActivityTitle(title);
//            }
//
//            @Override
//            public void onTabUnselected(int position) {
//
//            }
//
//            @Override
//            public void onTabReselected(int position) {
//
//            }
//        });

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
    }

    @Override
    public void setAdapterProducts(List<Product> productList) {
        listsFragment.setAdapterProducts(productList);
    }
}
