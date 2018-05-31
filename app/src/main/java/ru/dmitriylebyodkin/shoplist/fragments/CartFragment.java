package ru.dmitriylebyodkin.shoplist.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.adapters.IListAdapter;
import ru.dmitriylebyodkin.shoplist.presenters.CartPresenter;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.views.CartView;

public class CartFragment extends MvpAppCompatFragment implements CartView {

    @InjectPresenter
    CartPresenter presenter;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tvNoItems)
    TextView tvNoItems;

    private List<IListWithItems> listLists;
    private List<Product> productList;
    private IListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        ButterKnife.bind(this, view);

        presenter.init(getActivity());

        return view;
    }

    @Override
    public void setLists(List<IListWithItems> listLists) {
        this.listLists = listLists;
    }

    @Override
    public void setAdapter() {
        adapter = new IListAdapter(getActivity(), listLists, productList);
    }

    @Override
    public void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showNoItems() {
        tvNoItems.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void hideNoItems() {
        tvNoItems.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void setProducts(List<Product> productList) {
        this.productList = productList;
    }
}
