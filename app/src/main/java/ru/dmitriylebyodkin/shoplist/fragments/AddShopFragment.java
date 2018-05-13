package ru.dmitriylebyodkin.shoplist.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;

import org.parceler.Parcels;

import java.util.ArrayList;
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
import ru.dmitriylebyodkin.shoplist.models.PlaceSearchModel;
import ru.dmitriylebyodkin.shoplist.models.ShopModel;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.PlaceSearch;
import ru.dmitriylebyodkin.shoplist.room.data.Shop;
import ru.dmitriylebyodkin.shoplist.services.Data.GoogleLocation;
import ru.dmitriylebyodkin.shoplist.services.Data.GoogleNPlaceResult;
import ru.dmitriylebyodkin.shoplist.services.Data.GoogleNPlaces;
import ru.dmitriylebyodkin.shoplist.services.Data.GooglePlaceResult;
import ru.dmitriylebyodkin.shoplist.services.GoogleRetrofit;

public class AddShopFragment extends MvpAppCompatFragment {
    public static final int MAP_CODE = 1;
    private static final String TAG = "myLogs";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    private List<GooglePlaceResult> resultList;
    private GooglePlaceResult selectedResult;
    private Shop selectedShop;
    private Intent intent;
    private static double lat1 = 0;
    private static double lng1 = 0;
    private String pageToken;
    private int shopId;
    private IListWithItems iListWithItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_shop, container, false);
        ButterKnife.bind(this, view);

        resultList = new ArrayList<>();
        shopId = iListWithItems.getList().getShopId();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        /* сделать сортировку по рейтингу */

        SmartLocation.with(getActivity()).location().oneFix().start(location -> {
            lat1 = location.getLatitude();
            lng1 = location.getLongitude();

            List<PlaceSearch> placeSearches = PlaceSearchModel.getAll(getActivity());
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

            ShopAdapter shopAdapter = new ShopAdapter(getActivity(), null);
            shopAdapter.setSelectedId(shopId);
            recyclerView.setAdapter(shopAdapter);

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
                            Toast.makeText(getActivity(), "Не найдено", Toast.LENGTH_LONG).show();
                        } else {
                            Shop shop;
                            int timestamp = (int) (System.currentTimeMillis()/1000L);

                            PlaceSearch placeSearch = new PlaceSearch();
                            placeSearch.setAmount(places.getResults().size());
                            placeSearch.setLatitude(lat1);
                            placeSearch.setLongitude(lng1);
                            placeSearch.setId((int) PlaceSearchModel.insert(getActivity(), placeSearch)[0]);
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
                                shop.setId((int) ShopModel.insert(getActivity(), shop)[0]);
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
                shopAdapter.addAll(ShopModel.getBySearchId(getActivity(), placeSearchId));
                shopAdapter.finishAdd();
                progressBar.setVisibility(View.GONE);
            }
//            if (shopList == null) {
//               GoogleNPlaces googleResults = findPlaces(null);
//
//               for (Iterator<GoogleNPlaceResult> iterator = googleResults.getResults().iterator(); iterator.hasNext(); ) {
//                   GoogleNPlaceResult result = iterator.next();
//               }
//            } else {
//                for (Iterator<Shop> iterator = shopList.iterator(); iterator.hasNext(); ) {
//                    Shop shop = iterator.next();
//                    int distance = App.calculateDistance(lat1, shop.getLatitude(), lng2, shop.getLongitude());
//
//                    Log.d(TAG, "onCreate: " + distance);
//
//                    if (distance > 500) {
//                        iterator.remove();
//                    }
//                }
//            }
        });

        return view;
    }

    public void setList(IListWithItems iListWithItems) {
        this.iListWithItems = iListWithItems;
    }

    public void setShop(Shop shop) {
        this.selectedShop = shop;
    }
}
