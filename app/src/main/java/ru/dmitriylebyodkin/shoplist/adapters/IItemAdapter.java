package ru.dmitriylebyodkin.shoplist.adapters;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.activities.EditItemActivity;
import ru.dmitriylebyodkin.shoplist.activities.InfoActivity;
import ru.dmitriylebyodkin.shoplist.models.ItemModel;
import ru.dmitriylebyodkin.shoplist.models.ListModel;
import ru.dmitriylebyodkin.shoplist.room.data.IItem;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.views.InfoView;

public class IItemAdapter extends RecyclerView.Adapter<IItemAdapter.ViewHolder> {
    private static final String TAG = "myLogs";
    private Context context;
    private List<IItem> listItems;
    private List<Product> listProducts;
    private int positionOpened = -1;
    private int leftItems;
    private boolean isCompleted;
    private InputMethodManager inputMethodManager;

    public IItemAdapter(Context context, List<IItem> listItems, List<Product> listProducts, boolean isCompleted) {
        this.context = context;
        this.listItems = listItems;
        this.listProducts = listProducts;
        this.isCompleted = isCompleted;

        for (IItem item: listItems) {
            if (!item.isBought()) {
                leftItems += 1;
            }
        }
    }

    public List<Product> getProducts() {
        return this.listProducts;
    }

    public void setProducts(List<Product> products) {
        this.listProducts = products;
        notifyDataSetChanged();
    }

    public List<IItem> getItems() {
        return listItems;
    }

    public void setItems(List<IItem> listItems) {
        this.listItems = listItems;
        notifyDataSetChanged();
    }

    public void addItem(IItem item) {
        listItems.add(0, item);
        notifyItemInserted(0);
        notifyItemRangeChanged(0, getItemCount()+1);
    }

    public void checkAll() {
        for (int i = 0; i < listItems.size(); i++) {
            listItems.get(i).setBought(true);
            ItemModel.update(context, listItems.get(i));
        }

        notifyDataSetChanged();
    }

    public void resetAll() {
        for (int i = 0; i < listItems.size(); i++) {
            listItems.get(i).setBought(false);
            ItemModel.update(context, listItems.get(i));
        }

        notifyDataSetChanged();
    }

    public void deleteChecked() {
        List<IItem> removeItems = new ArrayList<>();
        IItem item;

        for (int i = 0; i < listItems.size(); i++) {
            item = listItems.get(i);

            if (item.isBought()) {
                ItemModel.delete(context, item);

                removeItems.add(item);
                notifyItemRemoved(i);
                notifyItemRangeChanged(0, getItemCount());
            }
        }

        listItems.removeAll(removeItems);
    }

    public void addProduct(Product product) {
        listProducts.add(product);
        notifyDataSetChanged();
    }

    public void setItem(int position, IItem item) {
        listItems.set(position, item);
        notifyItemChanged(position);
    }

    public void setProduct(int position, Product product) {
        listProducts.set(position, product);
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        listItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, getItemCount());
    }

    public void deleteItem(IItem item) {
        int index = -1;

        for (int i = 0; i < listItems.size(); i++) {
            IItem iItem = listItems.get(i);

            if (iItem.getId() == item.getId()) {
                index = i;
            }
        }

        if (index != -1) {
            listItems.remove(index);
            notifyItemRemoved(index);
            notifyItemRangeChanged(0, getItemCount());
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.swipeLayout)
        SwipeLayout swipeLayout;

        @BindView(R.id.ivCheck)
        ImageView ivCheck;

        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.tvCount)
        TextView tvCount;
        @BindView(R.id.tvCost)
        TextView tvCost;

        @BindView(R.id.etCost)
        EditText etCost;
        @BindView(R.id.etCount)
        EditText etCount;

        @BindView(R.id.controls)
        LinearLayout controls;
        @BindView(R.id.container)
        LinearLayout container;
        @BindView(R.id.layoutEdit)
        LinearLayout layoutEdit;
        @BindView(R.id.layoutDelete)
        LinearLayout layoutDelete;
        @BindView(R.id.layoutCount)
        LinearLayout layoutCount;

        @BindView(R.id.btnMinus)
        ImageButton btnMinus;
        @BindView(R.id.btnPlus)
        ImageButton btnPlus;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_item, parent, false);
        return new IItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IItem item = listItems.get(position);

        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.controls);

        if (item.isBought()) {
            holder.ivCheck.setImageDrawable(context.getResources().getDrawable(R.drawable.check_checked));
        } else {
            holder.ivCheck.setImageDrawable(context.getResources().getDrawable(R.drawable.check_unchecked));
        }

        Product product = null;
        int indexProduct = 0, i = 0;

        for (Product prod: listProducts) {
            if (prod.getId() == item.getProductId()) {
                product = prod;
                indexProduct = i;
            }
            i++;
        }

        if (product != null) {
            holder.tvTitle.setText(product.getTitle());
            holder.tvCount.setText(String.valueOf(item.getCount())+ " " + product.getUnitTitle());
        }

        String cost = String.valueOf(item.getCost());

        if (item.getCost() == 0) {
            cost = String.valueOf(0);
        }

        /**
         * Если дробная часть равна 0
         */
        if (item.getCost() - (int) item.getCost() == 0) {
            cost = String.valueOf((int) item.getCost());
        }

        holder.tvCost.setText(cost + " \u20BD");
        holder.etCost.setText(cost);
        holder.etCost.setSelection(0, cost.length());

        holder.etCost.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == KeyEvent.KEYCODE_ENDCALL) {
                float newCost = Float.parseFloat(holder.etCost.getText().toString());

                listItems.get(position).setCost(newCost);
                ItemModel.update(context, listItems.get(position));

                if (getItemCount() != position+1) {
                    positionOpened = position+1;
                    notifyItemChanged(positionOpened);
                } else {
                    positionOpened = -1;
                }

                notifyItemChanged(position);
                ((InfoView) context).updateSummary();

                return true;
            }

            return false;
        });

        holder.etCount.setText(String.valueOf(item.getCount()));
        holder.etCount.setSelection(String.valueOf(item.getCount()).length());

        Product finalProduct = product;
        int finalIndexProduct = indexProduct;
        holder.layoutEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditItemActivity.class);
            intent.putExtra("item", Parcels.wrap(item));
            intent.putExtra("position", position);
            intent.putExtra("product", Parcels.wrap(finalProduct));
            intent.putExtra("product_position", finalIndexProduct);
            ((InfoActivity) context).startActivityForResult(intent, InfoActivity.EDIT_ITEM_CODE);
        });

        holder.layoutDelete.setOnClickListener(v -> {
            listItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount()-1);
            Toast.makeText(context, "Удалено", Toast.LENGTH_LONG).show();

            ((InfoView) context).deleteItem(item, position);
        });

        if (positionOpened == position) {
            holder.etCost.setVisibility(View.VISIBLE);
            holder.etCost.requestFocus();

            inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

            if (inputMethodManager != null) {
                inputMethodManager.showSoftInput(((InfoActivity) context).getCurrentFocus(), 0);
            }

            holder.tvCost.setVisibility(View.GONE);
        } else {
            holder.etCost.setVisibility(View.GONE);
            holder.tvCost.setVisibility(View.VISIBLE);
        }

        holder.container.setOnLongClickListener(v -> {
            notifyItemChanged(positionOpened);
            positionOpened = position;
            notifyItemChanged(positionOpened);

            return true;
        });

        holder.container.setOnClickListener(view -> {
            if (item.isBought()) {
                leftItems = leftItems + 1;
            } else {
                leftItems = leftItems - 1;
            }

            listItems.get(position).setBought(!item.isBought());

            notifyItemChanged(positionOpened);
            notifyItemChanged(position);

            ItemModel.update(context, item);

            ((InfoActivity) context).updateSummary();
        });

    }

    public int getPositionOpened() {
        return positionOpened;
    }

    public void setPositionOpened(int positionOpened) {
        notifyItemChanged(this.positionOpened);
        this.positionOpened = positionOpened;
    }

    @Override
    public int getItemCount() {
        if (listItems == null) {
            return 0;
        }

        return listItems.size();
    }
}
