package ru.dmitriylebyodkin.shoplist.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.activities.AddProductActivity;
import ru.dmitriylebyodkin.shoplist.activities.MainActivity;
import ru.dmitriylebyodkin.shoplist.adapters.ProductAdapter;
import ru.dmitriylebyodkin.shoplist.presenters.ProductsPresenter;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.views.ProductsView;

public class ProductsFragment extends MvpAppCompatFragment implements ProductsView {

    private static final String TAG = "myLogs";
    @InjectPresenter
    ProductsPresenter presenter;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tvNoItems)
    TextView tvNoItems;

    private List<Product> productList;
    private ProductAdapter adapter;
    private int categoryId = 0;
    public boolean isProductsActivity = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lists, container, false);

        ButterKnife.bind(this, view);

        tvNoItems.setText(R.string.no_products);
        presenter.init(getActivity(), categoryId);

        return view;
    }

    @OnClick(R.id.floatingActionButton)
    public void createProduct(View v) {
        Intent intent = new Intent(getActivity(), AddProductActivity.class);
        intent.putExtra("category_id", categoryId);
        getActivity().startActivityForResult(intent, MainActivity.ADD_PRODUCT_CODE);
    }

    @Override
    public void setList(List<Product> productList) {
        this.productList = productList;
    }

    @Override
    public void setAdapter() {
        adapter = new ProductAdapter(getActivity(), productList, isProductsActivity);
    }

    @Override
    public void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void insert(Product product) {
        adapter.addToBegin(product);

        if (adapter.getItemCount() == 1) {
            presenter.hideNoItems();
        }
    }

    @Override
    public void update(Product product, int position) {
        adapter.update(product, position);
    }

    @Override
    public void delete(int position) {
        adapter.delete(position);

        if (adapter.getItemCount() == 0) {
            presenter.showNoItems();
        }

        Toast.makeText(getActivity(), R.string.is_removed, Toast.LENGTH_LONG).show();
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

    @Override
    public void showDeleteDialog(Product product, int position) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.deleting)
                .setMessage(R.string.deleting_message_product)
                .setPositiveButton(R.string.yes, (dialog, which) -> presenter.remove(getContext(), product, position))
                .setNegativeButton(R.string.no, null)
                .create();

        alertDialog.show();
    }

    @Override
    public void updateList() {
        presenter.init(getActivity(), categoryId);
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setProductsActivity(boolean isProductsActivity) {
        this.isProductsActivity = isProductsActivity;
    }
}
