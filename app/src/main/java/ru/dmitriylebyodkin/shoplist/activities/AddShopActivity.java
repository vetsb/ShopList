package ru.dmitriylebyodkin.shoplist.activities;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.nlopez.smartlocation.SmartLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.dmitriylebyodkin.shoplist.App;
import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.adapters.ShopAdapter;
import ru.dmitriylebyodkin.shoplist.models.ListModel;
import ru.dmitriylebyodkin.shoplist.models.PlaceSearchModel;
import ru.dmitriylebyodkin.shoplist.models.ShopModel;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.PlaceSearch;
import ru.dmitriylebyodkin.shoplist.room.data.Shop;
import ru.dmitriylebyodkin.shoplist.services.Data.GoogleLocation;
import ru.dmitriylebyodkin.shoplist.services.Data.GoogleNPlaceResult;
import ru.dmitriylebyodkin.shoplist.services.Data.GoogleNPlaces;
import ru.dmitriylebyodkin.shoplist.services.GoogleRetrofit;

public class AddShopActivity extends AppCompatActivity {

    public static final int MAP_CODE = 1;
    private static final String TAG = "myLogs";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private Shop selectedShop, initialShop;
    private ShopAdapter shopAdapter;
    private Intent intent;
    private static double lat1 = 0, lng1 = 0;
    private String pageToken;
    private int shopId;
    private IListWithItems iListWithItems;
    private boolean isFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop);

        ButterKnife.bind(this);

        intent = getIntent();
        iListWithItems = Parcels.unwrap(intent.getParcelableExtra("list"));
        shopId = iListWithItems.getList().getShopId();

        if (shopId != 0 || isLocationServicesEnabled()) {
            nextStep();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Включить геолокацию?")
                    .setMessage("Включите геолокацию для нахождения магазинов поблизости.")
                    .setPositiveButton(R.string.yes, (dialog, which) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton(R.string.no, (dialog, which) -> {
                        dialog.dismiss();
                        setResult(RESULT_CANCELED, intent);
                        finish();
                    })
                    .create();

            alertDialog.setCanceledOnTouchOutside(true);
            alertDialog.setOnCancelListener(dialog -> {
                dialog.dismiss();
                setResult(RESULT_CANCELED, intent);
                finish();
            });
            alertDialog.show();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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

        if (!isFound && isLocationServicesEnabled()) {
            nextStep();
            isFound = true;
        }
    }

    public void nextStep() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        /* сделать сортировку по рейтингу */

        shopAdapter = new ShopAdapter(this, null);
        shopAdapter.setSelectedId(shopId);
        recyclerView.setAdapter(shopAdapter);

        if (shopId == 0) {
            SmartLocation.with(this).location().oneFix().start(location -> {
                lat1 = location.getLatitude();
                lng1 = location.getLongitude();

                List<PlaceSearch> placeSearches = PlaceSearchModel.getAll(this);
                int placeSearchId = 0;
                int distance;

                for (PlaceSearch placeSearch: placeSearches) {
                    if (((int) System.currentTimeMillis()/1000L) - placeSearch.getCreatedAt() < 60*60*24*7) {
                        distance = App.calculateDistance(lat1, lng1, placeSearch.getLatitude(), placeSearch.getLongitude());

                        if (distance < 500) {
                            placeSearchId = placeSearch.getId();
                        }
                    }
                }

                /**
                 * Если нет Id поиска, то есть магазины не искались в текущем месте ни разу, то найти и добавить в базу, потом вывести.
                 * Иначе найти в базе магазины и вывести.
                 */
                if (placeSearchId == 0) {
                    GoogleRetrofit.getGoogleService().getNPlaces(
                            String.valueOf(lat1) + "," + String.valueOf(lng1),
                            "500",
                            "store",
                            "ru",
                            GoogleRetrofit.API_KEY,
                            pageToken
                    ).enqueue(new Callback<GoogleNPlaces>() {
                        @Override
                        public void onResponse(@NonNull Call<GoogleNPlaces> call, @NonNull Response<GoogleNPlaces> response) {
                            GoogleNPlaces places = response.body();

                            if (places != null) {
                                pageToken = places.getNextPageToken();
                            }

                            if (places == null || places.getResults() == null || places.getResults().size() == 0) {
                                Toast.makeText(getApplicationContext(), "Не найдено", Toast.LENGTH_LONG).show();
                            } else {
                                Shop shop;
                                int timestamp = (int) (System.currentTimeMillis()/1000L);

                                PlaceSearch placeSearch = new PlaceSearch();
                                placeSearch.setAmount(places.getResults().size());
                                placeSearch.setLatitude(lat1);
                                placeSearch.setLongitude(lng1);
                                placeSearch.setId((int) PlaceSearchModel.insert(getApplicationContext(), placeSearch)[0]);
                                placeSearch.setCreatedAt(timestamp);

                                progressBar.setVisibility(View.GONE);

                                for (GoogleNPlaceResult result: places.getResults()) {
                                    GoogleLocation resultLocation = result.getGeometry().getLocation();

                                    shop = new Shop();
                                    shop.setTitle(result.getName());
                                    shop.setLatitude(resultLocation.getLat());
                                    shop.setLongitude(resultLocation.getLng());
                                    shop.setGoogleId(result.getId());
                                    shop.setFormattedAddress(result.getVicinity());
                                    shop.setSearchId(placeSearch.getId());
                                    shop.setId((int) ShopModel.insert(getApplicationContext(), shop)[0]);
                                    shop.setCreatedAt(timestamp);

                                    shopAdapter.add(shop);
                                }

                                shopAdapter.finishAdd();
                            }
                        }

                        @Override
                        public void onFailure(Call<GoogleNPlaces> call, Throwable t) {

                        }
                    });
                } else {
                    /**
                     * Финальный список
                     */
                    shopAdapter.addAll(ShopModel.getBySearchId(this, placeSearchId));
                    shopAdapter.finishAdd();

                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            Shop shop = ShopModel.getById(this, shopId);
            List<Shop> shopList = ShopModel.getBySearchId(this, shop.getSearchId());

            selectedShop = shop;
            initialShop = shop;

            shopAdapter.addAll(shopList);
            shopAdapter.finishAdd();

            progressBar.setVisibility(View.GONE);
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

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.navSearch:
//
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

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

                ListModel.update(this, iListWithItems.getList());

                intent.putExtra("shop", Parcels.wrap(selectedShop));
                intent.putExtra("list", Parcels.wrap(iListWithItems));
                setResult(App.RESULT_ADD, intent);
            }
        } else {
            if (initialShop == selectedShop) {
                setResult(RESULT_CANCELED);
            } else {
                iListWithItems.getList().setShopId(selectedShop.getId());

                ListModel.update(this, iListWithItems.getList());

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
