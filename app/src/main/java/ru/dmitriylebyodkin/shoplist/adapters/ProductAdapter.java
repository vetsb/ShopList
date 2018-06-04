package ru.dmitriylebyodkin.shoplist.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.activities.AddProductActivity;
import ru.dmitriylebyodkin.shoplist.activities.MainActivity;
import ru.dmitriylebyodkin.shoplist.activities.ProductsActivity;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.views.MainView;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private static final String TAG = "myLogs";
    private Context context;
    private List<Product> productList;
    private boolean isProductsActivity;

    public ProductAdapter(Context context, List<Product> productList, boolean isProductsActivity) {
        this.context = context;
        this.productList = productList;
        this.isProductsActivity = isProductsActivity;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvTitle.setText(product.getTitle());
        holder.container.setOnClickListener(v -> startEditActivity(holder, product, position));
        holder.layoutEdit.setOnClickListener(v -> startEditActivity(holder, product, position));
        holder.layoutDelete.setOnClickListener(v -> {
            if (isProductsActivity) {
                ((ProductsActivity) context).showProductDeleteDialog(product, position);
            } else {
                ((MainView) context).showProductDeleteDialog(product, position);
            }
        });
    }

    private void startEditActivity(@NonNull ViewHolder holder, Product product, int position) {
        holder.swipeLayout.close();

        Intent intent = new Intent(context, AddProductActivity.class);
        intent.putExtra("product", Parcels.wrap(product));
        intent.putExtra("position", position);

        if (isProductsActivity) {
            ((ProductsActivity) context).startActivityForResult(intent, ProductsActivity.EDIT_PRODUCT_CODE);
        } else {
            ((MainActivity) context).startActivityForResult(intent, MainActivity.EDIT_PRODUCT_CODE);
        }
    }

    @Override
    public int getItemCount() {
        return productList == null ? 0 : productList.size();
    }

    public void update(Product product, int position) {
        productList.set(position, product);
        notifyItemChanged(position);
    }

    public void delete(int position) {
        productList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, getItemCount());
    }

    public void addToBegin(Product product) {
        productList.add(0, product);
        notifyItemInserted(0);
        notifyItemRangeChanged(0, getItemCount());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTitle)
        TextView tvTitle;

        @BindView(R.id.layoutEdit)
        LinearLayout layoutEdit;

        @BindView(R.id.layoutDelete)
        LinearLayout layoutDelete;

        @BindView(R.id.container)
        LinearLayout container;

        @BindView(R.id.swipeLayout)
        SwipeLayout swipeLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
