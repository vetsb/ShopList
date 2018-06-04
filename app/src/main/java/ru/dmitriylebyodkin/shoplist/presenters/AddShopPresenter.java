package ru.dmitriylebyodkin.shoplist.presenters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.SmartLocation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.dmitriylebyodkin.shoplist.App;
import ru.dmitriylebyodkin.shoplist.models.ItemModel;
import ru.dmitriylebyodkin.shoplist.models.ListModel;
import ru.dmitriylebyodkin.shoplist.models.PlaceSearchModel;
import ru.dmitriylebyodkin.shoplist.models.ShopModel;
import ru.dmitriylebyodkin.shoplist.room.data.IItem;
import ru.dmitriylebyodkin.shoplist.room.data.IList;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.PlaceSearch;
import ru.dmitriylebyodkin.shoplist.room.data.Shop;
import ru.dmitriylebyodkin.shoplist.services.Data.GoogleLocation;
import ru.dmitriylebyodkin.shoplist.services.Data.GooglePlaceResult;
import ru.dmitriylebyodkin.shoplist.services.Data.GooglePlaces;
import ru.dmitriylebyodkin.shoplist.services.GoogleRetrofit;
import ru.dmitriylebyodkin.shoplist.views.AddShopView;

@InjectViewState
public class AddShopPresenter extends MvpPresenter<AddShopView> {
    String pageToken = "";

    public void init(Context context, int shopId, List<IItem> itemList) {
        getViewState().initList();

        /* сделать сортировку по рейтингу */

        if (shopId == 0) {
            SmartLocation.with(context).location().oneFix().start(location -> {
                double lat1 = location.getLatitude();
                double lng1 = location.getLongitude();

                List<PlaceSearch> placeSearches = PlaceSearchModel.getAll(context);
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
                    GoogleRetrofit.getGoogleService().getPlaces(
                            String.valueOf(lat1) + "," + String.valueOf(lng1),
                            "500",
                            "store",
                            "ru",
                            GoogleRetrofit.API_KEY,
                            pageToken
                    ).enqueue(new Callback<GooglePlaces>() {
                        @Override
                        public void onResponse(@NonNull Call<GooglePlaces> call, @NonNull Response<GooglePlaces> response) {
                            GooglePlaces places = response.body();

                            if (places != null) {
                                pageToken = places.getNextPageToken();
                            }

                            if (places == null || places.getResults() == null || places.getResults().size() == 0) {
                                Toast.makeText(context, "Не найдено", Toast.LENGTH_LONG).show();
                            } else {
                                List<Shop> shopList = new ArrayList<>();
                                Shop shop;
                                int timestamp = (int) (System.currentTimeMillis()/1000L);

                                PlaceSearch placeSearch = new PlaceSearch();
                                placeSearch.setAmount(places.getResults().size());
                                placeSearch.setLatitude(lat1);
                                placeSearch.setLongitude(lng1);
                                placeSearch.setId((int) PlaceSearchModel.insert(context, placeSearch)[0]);
                                placeSearch.setCreatedAt(timestamp);

                                getViewState().hideProgressBar();

                                for (GooglePlaceResult result: places.getResults()) {
                                    GoogleLocation resultLocation = result.getGeometry().getLocation();

                                    shop = new Shop();
                                    shop.setTitle(result.getName());
                                    shop.setLatitude(resultLocation.getLat());
                                    shop.setLongitude(resultLocation.getLng());
                                    shop.setGoogleId(result.getId());
                                    shop.setFormattedAddress(result.getVicinity());
                                    shop.setSearchId(placeSearch.getId());
                                    shop.setId((int) ShopModel.insert(context, shop)[0]);
                                    shop.setCreatedAt(timestamp);

                                    shopList.add(shop);
                                }

                                getViewState().addAllToAdapter(shopList);
                                getViewState().finishAdd();
                            }
                        }

                        @Override
                        public void onFailure(Call<GooglePlaces> call, Throwable t) {

                        }
                    });
                } else {
                    /**
                     * Финальный список
                     */

                    List<Shop> shopList = ShopModel.getBySearchId(context, placeSearchId);
//                    List<Integer> costs = new ArrayList<>();
//                    List<Integer> productIds = new ArrayList<>();
//
//                    for (IItem item: itemList) {
//                        productIds.add(item.getProductId());
//                    }
//
//                    float cost, minCost;
//
//                    for (Shop shop: shopList) {
//                        List<IListWithItems> list = ListModel.getWithItemsByShopId(context, shop.getId());
//                        cost = 0;
//
//                        for (IListWithItems iListWithItems: list) {
//                            minCost = 0;
//
//                            for (IItem item: iListWithItems.getItems()) {
//                                if (productIds.contains(item.getProductId()) && (item.getCost() < minCost || minCost == 0)) {
//                                    minCost = item.getCost();
//                                }
//                            }
//                        }
////                        for (IItem item: itemList) {
////                            List<IItem> items = ItemModel.getByShopAndProductId(shop.getId(), item.getProductId());
////                        }
//                    }

                    getViewState().addAllToAdapter(shopList);
                    getViewState().finishAdd();
                    getViewState().hideProgressBar();
                }
            });
        } else {
            Shop shop = ShopModel.getById(context, shopId);
            List<Shop> shopList = ShopModel.getBySearchId(context, shop.getSearchId());

            getViewState().setSelectedShop(shop);
            getViewState().setInitialShop(shop);
            getViewState().addAllToAdapter(shopList);
            getViewState().finishAdd();
            getViewState().hideProgressBar();
        }
    }

    public void showLocationDialog() {
        getViewState().showLocationDialog();
    }

    public void update(Context context, IList list) {
        ListModel.update(context, list);
    }
}
