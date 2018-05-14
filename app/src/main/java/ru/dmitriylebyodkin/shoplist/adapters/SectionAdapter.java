package ru.dmitriylebyodkin.shoplist.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.data.Section;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.ViewHolder> {

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
        if (sectionList == null) {
            return 0;
        }

        return sectionList.size();
    }
}
