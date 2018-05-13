package ru.dmitriylebyodkin.shoplist.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.activities.AddShopActivity;
import ru.dmitriylebyodkin.shoplist.activities.InfoActivity;
import ru.dmitriylebyodkin.shoplist.activities.MapActivity;
import ru.dmitriylebyodkin.shoplist.room.data.Shop;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "myLogs";
    private Context context;
    private List<Shop> shopList;
    private List<Shop> searchShopList;
    private int selectedPosition = -1;
    private int selectedId = 0;
    private ValueFilter valueFilter;

    public ShopAdapter(Context context, List<Shop> shopList) {
        this.context = context;

        if (shopList == null) {
            this.shopList = new ArrayList<>();
        } else {
            this.shopList = shopList;
        }

        this.searchShopList = this.shopList;
    }

    public void add(Shop shop) {
        shopList.add(shop);
        notifyDataSetChanged();
    }

    public void addAll(List<Shop> shops) {
        shopList.clear();
        shopList.addAll(shops);
        notifyDataSetChanged();
    }

    public void finishAdd() {
        Shop shop;

        for (int i = 0; i < shopList.size(); i++) {
            shop = shopList.get(i);

            if (shop.getId() == selectedId) {
                selectedPosition = i;
            }
        }
    }

    public void setSelectedId(int shopId) {
        selectedId = shopId;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.container)
        LinearLayout container;
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.tvAddress)
        TextView tvAddress;
        @BindView(R.id.btnGps)
        ImageButton btnGps;
        @BindView(R.id.viewLine)
        View viewLine;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shop_item, parent, false);
        return new ShopAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Shop shop = shopList.get(position);

        if (selectedPosition == position) {
            setColorSelected(holder);
        } else {
            setColorUnSelected(holder);
        }

        holder.tvTitle.setText(shop.getTitle());
        holder.tvAddress.setText(shop.getFormattedAddress());

        holder.container.setOnClickListener(v -> {
            if (selectedPosition == position) {
                ((AddShopActivity) context).setShop(null);

                selectedPosition = -1;
                notifyItemChanged(position);
            } else {
                ((AddShopActivity) context).setShop(shop);

                notifyItemChanged(selectedPosition);
                notifyItemChanged(position);

                selectedPosition = position;
            }
        });

        holder.btnGps.setOnClickListener(v -> {
            Intent intent = new Intent(context, MapActivity.class);
            intent.putExtra("shop", Parcels.wrap(shop));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (shopList == null) {
            return 0;
        }

        return shopList.size();
    }

    private void setColorSelected(@NonNull ViewHolder holder) {
        holder.tvTitle.setTextColor(Color.WHITE);
        holder.tvAddress.setTextColor(Color.WHITE);
        holder.btnGps.setImageDrawable(context.getResources().getDrawable(R.drawable.gps_white));
        holder.container.setBackgroundResource(R.drawable.item_background_selected);
        holder.viewLine.setBackgroundResource(R.color.colorPrimary);
    }

    private void setColorUnSelected(@NonNull ViewHolder holder) {
        holder.tvTitle.setTextColor(Color.BLACK);
        holder.tvAddress.setTextColor(Color.parseColor("#8F8F8F"));
        holder.btnGps.setImageDrawable(context.getResources().getDrawable(R.drawable.gps_gray));
        holder.container.setBackgroundResource(R.drawable.item_background);
        holder.viewLine.setBackgroundColor(Color.parseColor("#CDCDCD"));
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null) {
                List<Shop> filterList = new ArrayList<>();

                for (int i = 0; i < searchShopList.size(); i++) {
                    if ((searchShopList.get(i).getTitle().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {

                        filterList.add(searchShopList.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = shopList.size();
                results.values = shopList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            shopList = (ArrayList<Shop>) results.values;
            notifyDataSetChanged();
        }
    }
}
