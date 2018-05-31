package ru.dmitriylebyodkin.shoplist.activities;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dmitriylebyodkin.shoplist.App;
import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.adapters.ShopAdapter;
import ru.dmitriylebyodkin.shoplist.models.ListModel;
import ru.dmitriylebyodkin.shoplist.presenters.AddShopPresenter;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.Shop;
import ru.dmitriylebyodkin.shoplist.views.AddShopView;

public class AddShopActivity extends MvpAppCompatActivity implements AddShopView {

    @InjectPresenter
    AddShopPresenter presenter;

    private static final String TAG = "myLogs";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Shop selectedShop, initialShop;
    private ShopAdapter shopAdapter;
    private Intent intent;
    private int shopId;
    private IListWithItems iListWithItems;
    private boolean isFound;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop);

        ButterKnife.bind(this);

        intent = getIntent();
        iListWithItems = Parcels.unwrap(intent.getParcelableExtra("list"));
        shopId = iListWithItems.getList().getShopId();

        alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.location_dialog_title)
                .setMessage(R.string.location_dialog_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton(R.string.no, (dialog, which) -> {
                    dialog.dismiss();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                })
                .create();

        if (shopId != 0 || isLocationServicesEnabled()) {
            presenter.init(this, shopId);
        } else {
            presenter.showLocationDialog();
        }

        shopAdapter = new ShopAdapter(this, null);
        shopAdapter.setSelectedId(shopId);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(shopAdapter);
    }

    @Override
    public void showLocationDialog() {
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnCancelListener(dialog -> {
            dialog.dismiss();
            setResult(RESULT_CANCELED, intent);
            finish();
        });
        alertDialog.show();
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void addAllToAdapter(List<Shop> shopList) {
        shopAdapter.addAll(shopList);
    }

    @Override
    public void finishAdd() {
        shopAdapter.finishAdd();
        isFound = true;
    }

    @Override
    public void setSelectedShop(Shop shop) {
        selectedShop = shop;
    }

    @Override
    public void setInitialShop(Shop shop) {
        initialShop = shop;
    }

    public boolean isLocationServicesEnabled() {
        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean mIsGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean mIsNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        return mIsGPSEnabled || mIsNetworkEnabled;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isLocationServicesEnabled()) {
            if (!isFound) {
                presenter.init(this, shopId);
            }

            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        } else {
            presenter.showLocationDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_shop_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.navSearch);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setFocusable(false);
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                shopAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                shopAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (selectedShop == null) {
            if (iListWithItems.getList().getShopId() == 0) {
                setResult(RESULT_CANCELED);
            } else {
                iListWithItems.getList().setShopId(0);
                iListWithItems.getList().setUpdatedAt((int) (System.currentTimeMillis()/1000L));

                presenter.update(this, iListWithItems.getList());

                intent.putExtra("shop", Parcels.wrap(selectedShop));
                intent.putExtra("list", Parcels.wrap(iListWithItems));
                setResult(App.RESULT_ADD, intent);
            }
        } else {
            if (initialShop == selectedShop) {
                setResult(RESULT_CANCELED);
            } else {
                iListWithItems.getList().setShopId(selectedShop.getId());
                iListWithItems.getList().setUpdatedAt((int) (System.currentTimeMillis()/1000L));

                presenter.update(this, iListWithItems.getList());

                intent.putExtra("shop", Parcels.wrap(selectedShop));
                intent.putExtra("list", Parcels.wrap(iListWithItems));
                setResult(App.RESULT_ADD, intent);
            }
        }

        finish();
    }

    public void setShop(Shop shop) {
        selectedShop = shop;
    }
}
