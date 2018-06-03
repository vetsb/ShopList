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

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.activities.MainActivity;
import ru.dmitriylebyodkin.shoplist.activities.ProductsActivity;
import ru.dmitriylebyodkin.shoplist.room.data.Category;
import ru.dmitriylebyodkin.shoplist.views.MainView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private Context context;
    private List<Category> categoryList;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryList.get(position);

        holder.tvTitle.setText(category.getTitle());
        holder.container.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductsActivity.class);
            intent.putExtra("category", Parcels.wrap(category));
            ((MainActivity) context).startActivityForResult(intent, MainActivity.LIST_PRODUCTS_CODE);
        });
        holder.layoutEdit.setOnClickListener(v -> ((MainView) context).showCategoryEditDialog(category, position));
        holder.layoutDelete.setOnClickListener(v -> ((MainView) context).showCategoryDeleteDialog(category, position));
    }

    @Override
    public int getItemCount() {
        return (categoryList == null) ? 0 : categoryList.size();
    }

    public void set(int position, Category category) {
        categoryList.set(position, category);
        notifyItemChanged(position);
    }

    public void remove(int position) {
        categoryList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, getItemCount());
    }

    public void add(Category category) {
        categoryList.add(category);
        notifyItemInserted(getItemCount()-1);
        notifyItemRangeChanged(0, getItemCount());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.container)
        LinearLayout container;

        @BindView(R.id.tvTitle)
        TextView tvTitle;

        @BindView(R.id.layoutEdit)
        LinearLayout layoutEdit;

        @BindView(R.id.layoutDelete)
        LinearLayout layoutDelete;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
