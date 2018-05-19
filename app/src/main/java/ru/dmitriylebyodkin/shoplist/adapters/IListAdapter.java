package ru.dmitriylebyodkin.shoplist.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.activities.InfoActivity;
import ru.dmitriylebyodkin.shoplist.activities.MainActivity;
import ru.dmitriylebyodkin.shoplist.room.RoomDb;
import ru.dmitriylebyodkin.shoplist.room.dao.ProductDao;
import ru.dmitriylebyodkin.shoplist.room.data.IItem;
import ru.dmitriylebyodkin.shoplist.room.data.IList;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.Product;

public class IListAdapter extends RecyclerView.Adapter<IListAdapter.ViewHolder> {
    private static final String TAG = "myLogs";
    private Context context;
    private List<IListWithItems> data;
    private List<Product> productList;

    public IListAdapter(Context context, List<IListWithItems> data, List<Product> productList) {
        this.context = context;
        this.data = data;
        this.productList = productList;

//        if (productList == null) {
//            this.productList = new ArrayList<>();
//        } else {
//            this.productList = productList;
//        }
//
//        Product product;
//        List<Integer> productIds = new ArrayList<>();
//
//        for (IListWithItems list: data) {
//            for (IItem item: list.getItems()) {
//                if (productIds.indexOf(item.getProductId()) == -1) {
//                    productIds.add(item.getProductId());
//                }
//            }
//        }
//
//        for (Iterator<Product> iterator = productList.iterator(); iterator.hasNext();) {
//            product = iterator.next();
//
//            if (productIds.indexOf(product.getId()) == -1) {
//                iterator.remove();
//            }
//        }
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
//        notifyItemInserted(0);
//        notifyItemRangeChanged(0, getItemCount());
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
        @BindView(R.id.container)
        LinearLayout container;

        @BindView(R.id.tvTitle)
        TextView tvTitle;

        @BindView(R.id.tvItems)
        TextView tvItems;

        @BindView(R.id.tvUpdatedAt)
        TextView tvUpdatedAt;

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

        holder.container.setOnClickListener(v -> {
            Intent intent = new Intent(context, InfoActivity.class);
            intent.putExtra("list", Parcels.wrap(listWithItems));
//            intent.putExtra("products", Parcels.wrap(productList));
            intent.putExtra("position", position);
            ((MainActivity) context).startActivityForResult(intent, MainActivity.LIST_ACTIVITY_CODE);
        });

        /**
         * Название
         */
        holder.tvTitle.setText(list.getTitle());

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
        String updatedText = "изменен ";

        if (timestampUpdated == 0) {
            updatedText = "создан ";
            timestampUpdated = list.getCreatedAt();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestampUpdated*1000L);

        int hours, minutes;
        hours = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        int timestampUpdatedDay = (int) (calendar.getTimeInMillis()/1000L);
        int timestampToday;

        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        timestampToday = (int) (calendar.getTimeInMillis()/1000L);

        if (timestampUpdatedDay == timestampToday) {
            updatedText += "сегодня ";
        } else if (timestampToday - timestampUpdatedDay == 60*60*24) {
            updatedText += "вчера ";
        } else if (timestampToday - timestampUpdatedDay == 60*60*24*2) {
            updatedText += "позавчера ";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

            updatedText += sdf.format(new Date(timestampUpdatedDay*1000L));
        }

        updatedText += String.valueOf(hours) + ":" + String.valueOf(minutes);

        holder.tvUpdatedAt.setText(updatedText);
    }

    @Override
    public int getItemCount() {
        if (data == null) {
            return 0;
        }

        return data.size();
    }
}
