package ru.dmitriylebyodkin.shoplist.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.activities.InfoActivity;
import ru.dmitriylebyodkin.shoplist.activities.MainActivity;
import ru.dmitriylebyodkin.shoplist.adapters.IListAdapter;
import ru.dmitriylebyodkin.shoplist.models.ListModel;
import ru.dmitriylebyodkin.shoplist.models.ProductModel;
import ru.dmitriylebyodkin.shoplist.presenters.ListsPresenter;
import ru.dmitriylebyodkin.shoplist.room.data.IList;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.views.ListsView;

public class ListsFragment extends MvpAppCompatFragment implements ListsView {

    @InjectPresenter
    ListsPresenter presenter;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private List<IListWithItems> listLists;
    private IListAdapter iListAdapter;
    private List<Product> productList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        productList = ProductModel.getAll(getActivity());
        presenter.init(getActivity());

        return view;
    }

    @OnClick(R.id.floatingActionButton)
    public void createList() {
        View view = getLayoutInflater().inflate(R.layout.dialog_create_list, null);
        final EditText etTitle = view.findViewById(R.id.etTitle);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY");

        String date = sdf.format(new Date(System.currentTimeMillis()));

        etTitle.setText(date);

        int timestampFrom, timestampTo;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        timestampFrom = (int) (calendar.getTimeInMillis()/1000L);

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        timestampTo = (int) (calendar.getTimeInMillis()/1000L);

        int count = ListModel.getCountByTimeRange(getActivity(), timestampFrom, timestampTo);

        if (count != 0) {
            etTitle.append(" " + String.valueOf(count+1));
        }

        etTitle.selectAll();

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.title)
                .setView(view)
                .setPositiveButton(R.string.next, (dialog, which) -> {
                    IList list = new IList();
                    list.setTitle(etTitle.getText().toString());

                    int id = presenter.createList(getActivity(), list);
                    list.setId(id);

                    IListWithItems iListWithItems = new IListWithItems();
                    iListWithItems.setList(list);
                    iListWithItems.setItems(new ArrayList<>());

                    Intent intent = new Intent(getActivity(), InfoActivity.class);
                    intent.putExtra("list", Parcels.wrap(iListWithItems));
                    intent.putExtra("new_list", true);
                    startActivityForResult(intent, MainActivity.LIST_ACTIVITY_CODE);
                })
                .setNeutralButton(R.string.cancel, null)
                .create();

        alertDialog.show();

        etTitle.requestFocus();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    @Override
    public void setList(List<IListWithItems> list) {
        listLists = list;
    }

    @Override
    public void setAdapter() {
        iListAdapter = new IListAdapter(getActivity(), listLists, productList);
    }

    @Override
    public void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(iListAdapter);
    }

    @Override
    public void updateAdapterItem(int position, IListWithItems iListWithItems) {
        iListAdapter.updateItem(position, iListWithItems);
    }

    @Override
    public void addAdapterItemToBegin(IListWithItems iListWithItems) {
        iListAdapter.addToBegin(iListWithItems);
    }

    @Override
    public void setAdapterProducts(List<Product> productList) {
        iListAdapter.setProducts(productList);
    }

    @Override
    public void removeAdapterItem(int position) {
        iListAdapter.removeItem(position);
        Toast.makeText(getActivity(), R.string.is_removed, Toast.LENGTH_LONG).show();
    }

    @Override
    public void smoothScrollToBegin() {
        recyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void showLayoutNotItems() {

    }

    @Override
    public void hideLayoutNotItems() {

    }
}
