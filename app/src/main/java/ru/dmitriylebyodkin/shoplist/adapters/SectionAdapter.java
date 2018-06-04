package ru.dmitriylebyodkin.shoplist.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.data.Section;
import ru.dmitriylebyodkin.shoplist.models.ProductModel;
import ru.dmitriylebyodkin.shoplist.room.data.IItem;
import ru.dmitriylebyodkin.shoplist.room.data.Product;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.ViewHolder> {

    private static final String TAG = "myLogs";
    private Context context;
    private List<Section> sectionList;

    public SectionAdapter(Context context, List<Section> sectionList) {
        this.context = context;
        this.sectionList = sectionList;
    }

    public void setList(List<Section> list) {
        this.sectionList = list;
        notifyDataSetChanged();
    }

    public void checkAll() {
        for (Section section: sectionList) {
            section.getAdapter().checkAll();
        }
    }

    public void resetAll() {
        for (Section section: sectionList) {
            section.getAdapter().resetAll();
        }
    }

    public void deleteChecked() {
        int i = 0;

        for (Iterator<Section> iterator = sectionList.iterator(); iterator.hasNext(); ) {
            Section section = iterator.next();

            section.getAdapter().deleteChecked();

            notifyItemChanged(i);

            if (section.getAdapter().getItemCount() == 0) {
                iterator.remove();
                notifyItemRemoved(i);
                notifyItemRangeChanged(0, getItemCount());
            }

            i++;
        }
    }

    public void addItem(IItem item) {
        int i = 0;

        for (Section section: sectionList) {
            Product product1 = ProductModel.getById(context, item.getProductId());
            Product product2 = ProductModel.getById(context, section.getAdapter().getItems().get(0).getProductId());

            /*
            Ид категории товара, который хотим добавить
             */
            int categoryId1 = product1.getCategoryId();

            /*
            Ид категории, которая в текущем Section
             */
            int categoryId2 = product2.getCategoryId();

            if (categoryId1 == categoryId2) {
                section.getAdapter().addItem(item);

                notifyItemChanged(i);
            }

            i++;
        }
    }

    public void update() {
        notifyDataSetChanged();

        for (Iterator<Section> iterator = sectionList.iterator(); iterator.hasNext(); ) {
            Section section = iterator.next();

            if (section.getAdapter().getItemCount() == 0) {
                iterator.remove();
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.recyclerView)
        RecyclerView recyclerView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.section_item, parent, false);
        return new SectionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Section section = sectionList.get(position);

        if (section.getTitle() == null || section.getTitle().trim().equalsIgnoreCase("")) {
            holder.tvTitle.setText(R.string.without_category);
        } else {
            holder.tvTitle.setText(section.getTitle());
        }

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setAdapter(section.getAdapter());
    }

    @Override
    public int getItemCount() {
        return sectionList == null ? 0 : sectionList.size();
    }
}
