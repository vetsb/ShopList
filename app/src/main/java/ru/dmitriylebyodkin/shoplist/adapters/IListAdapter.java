package ru.dmitriylebyodkin.shoplist.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dmitriylebyodkin.shoplist.App;
import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.activities.EditListActivity;
import ru.dmitriylebyodkin.shoplist.activities.InfoActivity;
import ru.dmitriylebyodkin.shoplist.activities.MainActivity;
import ru.dmitriylebyodkin.shoplist.models.ListModel;
import ru.dmitriylebyodkin.shoplist.room.data.IItem;
import ru.dmitriylebyodkin.shoplist.room.data.IList;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.views.CartView;
import ru.dmitriylebyodkin.shoplist.views.ListsView;
import ru.dmitriylebyodkin.shoplist.views.MainView;

public class IListAdapter extends RecyclerView.Adapter<IListAdapter.ViewHolder> {
    private static final String TAG = "myLogs";
    private Context context;
    private List<IListWithItems> data;
    private List<Product> productList;

    public IListAdapter(Context context, List<IListWithItems> data, List<Product> productList) {
        this.context = context;
        this.data = data;
        this.productList = productList;
    }

    public List<IListWithItems> getData() {
        return data;
    }

    public void setData(List<IListWithItems> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void updateItem(int position, IListWithItems iListWithItems) {
        data.get(position).setList(iListWithItems.getList());
        data.get(position).setItems(iListWithItems.getItems());

        notifyItemChanged(position);
    }

    public void addToBegin(IListWithItems iListWithItems) {
        data.add(0, iListWithItems);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount()-1);
    }

    public void setProducts(List<Product> products) {
        this.productList = products;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.swipeLayout)
        SwipeLayout swipeLayout;

        @BindView(R.id.controls)
        LinearLayout controls;

        @BindView(R.id.layoutEdit)
        LinearLayout layoutEdit;

        @BindView(R.id.layoutDelete)
        LinearLayout layoutDelete;

        @BindView(R.id.container)
        LinearLayout container;

        @BindView(R.id.tvTitle)
        TextView tvTitle;

        @BindView(R.id.tvItems)
        TextView tvItems;

        @BindView(R.id.tvUpdatedAt)
        TextView tvUpdatedAt;

        @BindView(R.id.tvDelete)
        TextView tvDelete;

        @BindView(R.id.tvRestore)
        TextView tvRestore;

        @BindView(R.id.layoutSettings)
        LinearLayout layoutSettings;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IListWithItems listWithItems = data.get(position);
        IList list = listWithItems.getList();
        List<IItem> itemList = listWithItems.getItems();

        if (listWithItems.getList().isDeleted()) {
            holder.swipeLayout.setSwipeEnabled(false);
            holder.layoutSettings.setVisibility(View.VISIBLE);
            holder.tvItems.setVisibility(View.GONE);
            holder.tvUpdatedAt.setVisibility(View.GONE);
        } else {
            holder.swipeLayout.setSwipeEnabled(true);
            holder.layoutSettings.setVisibility(View.GONE);
            holder.tvItems.setVisibility(View.VISIBLE);
            holder.tvUpdatedAt.setVisibility(View.VISIBLE);
        }

        holder.tvDelete.setOnClickListener(v -> {
            ListModel.delete(context, listWithItems.getList());
            data.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(0, getItemCount());
            Toast.makeText(context, R.string.is_removed, Toast.LENGTH_LONG).show();

            if (getItemCount() == 0) {
                ((MainView) context).showCartNoItems();
            }
        });

        holder.tvRestore.setOnClickListener(v -> {
            ListModel.restore(context, listWithItems.getList());
            data.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(0, getItemCount());
            Toast.makeText(context, R.string.is_restore, Toast.LENGTH_LONG).show();

            if (getItemCount() == 0) {
                ((MainView) context).showCartNoItems();
            }
        });

        holder.layoutEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditListActivity.class);
            intent.putExtra("list", Parcels.wrap(listWithItems));
            intent.putExtra("position", position);
            ((MainActivity) context).startActivityForResult(intent, MainActivity.EDIT_LIST_ACTIVITY_CODE);
            holder.swipeLayout.close();

            if (getItemCount() == 0) {
                ((MainView) context).showListNoItems();
            }
        });

        holder.layoutDelete.setOnClickListener(v -> {
            listWithItems.getList().setDeleted(true);

            ((MainView) context).showListDeleteDialog(listWithItems.getList(), position);
        });

        if (listWithItems.getList().isDeleted()) {
            holder.container.setBackgroundColor(context.getResources().getColor(R.color.whitePrimary));
        } else {
            holder.container.setOnClickListener(v -> {
                Intent intent = new Intent(context, InfoActivity.class);
                intent.putExtra("list", Parcels.wrap(listWithItems));
                intent.putExtra("position", position);
                ((MainActivity) context).startActivityForResult(intent, MainActivity.LIST_ACTIVITY_CODE);
                holder.swipeLayout.close();
            });
        }

        /**
         * Название
         */
        holder.tvTitle.setText(list.getTitle());

        if (!listWithItems.getList().isDeleted()) {
            /**
             * Товары
             */
            if (itemList == null || itemList.size() == 0) {
                holder.tvItems.setText(R.string.no_products);
            } else {
                StringBuilder items = new StringBuilder();

                for (int i = 0; i < itemList.size(); i++) {
                    IItem item = itemList.get(i);

                    for (Product product : productList) {
                        if (product.getId() == item.getProductId()) {
                            items.append(product.getTitle());
                        }
                    }

                    if (i != itemList.size()-1) {
                        items.append(", ");
                    }
                }
//            StringBuilder items = new StringBuilder();
//
//            int size = itemList.size();
//
//            if (itemList.size() == 11) {
//                size = 11;
//            } else if (itemList.size() > 11) {
//                size = 10;
//            }
//
//            for (int i = 0; i < size; i++) {
//                IItem item = itemList.get(i);
//
//                for (Product product: productList) {
//                    if (product.getId() == item.getProductId()) {
//                        items.append(product.getTitle());
//                    }
//                }
//
//                if (i != size-1) {
//                    items.append("\n");
//                }
//            }
//
//            if (itemList.size() > 11) {
//                items.append("\n").append("+ ещё ").append(itemList.size() - size);
//            }

                holder.tvItems.setText(items);
            }


            /**
             * Дата изменения
             */
            int timestampUpdated = list.getUpdatedAt();
            int timestampCurrent = (int) (System.currentTimeMillis()/1000L);
            String updatedText = "изменен ";

            if (timestampUpdated == 0) {
                updatedText = "создан ";
                timestampUpdated = list.getCreatedAt();
            }

            SimpleDateFormat sdf;
//
//            if (timestampCurrent - timestampUpdated < 60*60*24) {
//                updatedText += "сегодня";
//            } else if (timestampCurrent - timestampUpdated >= 60*60*24 && timestampCurrent - timestampUpdated < 60*60*24*2) {
//                updatedText += "вчера";
//            } else {
//                sdf = new SimpleDateFormat("dd MMMM YYYY", App.getRussianLocale());
//                updatedText += sdf.format(new Date(timestampCurrent*1000L));
//            }
//
//            sdf = new SimpleDateFormat("HH:mm");
//            updatedText += " в " + sdf.format(new Date(timestampUpdated*1000L));
//
//            holder.tvUpdatedAt.setText(updatedText);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestampUpdated*1000L);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.AM_PM, Calendar.AM);

            int timestampUpdatedDay = (int) (calendar.getTimeInMillis()/1000L);


            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.AM_PM, Calendar.AM);

            int timestampToday = (int) (calendar.getTimeInMillis()/1000L);

            sdf = new SimpleDateFormat("dd MMMM YYYY HH:mm:ss", App.getRussianLocale());

            if (timestampUpdatedDay == timestampToday) {
                updatedText += "сегодня";
            } else if (timestampToday - timestampUpdatedDay == 60*60*24) {
                updatedText += "вчера";
            } else if (timestampToday - timestampUpdatedDay == 60*60*24*2) {
                updatedText += "позавчера";
            } else {
                sdf = new SimpleDateFormat("dd MMMM YYYY", App.getRussianLocale());
                updatedText += sdf.format(new Date(timestampUpdatedDay*1000L)) + " ";
            }

            sdf = new SimpleDateFormat("HH:mm");
            updatedText += " в " + sdf.format(new Date(timestampUpdated*1000L));

            holder.tvUpdatedAt.setText(updatedText);
        }
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }

        return data.size();
    }
}
