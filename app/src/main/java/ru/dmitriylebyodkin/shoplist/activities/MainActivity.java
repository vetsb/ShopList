package ru.dmitriylebyodkin.shoplist.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.dmitriylebyodkin.shoplist.App;
import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.adapters.IListAdapter;
import ru.dmitriylebyodkin.shoplist.models.ListModel;
import ru.dmitriylebyodkin.shoplist.models.ProductModel;
import ru.dmitriylebyodkin.shoplist.presenters.MainPresenter;
import ru.dmitriylebyodkin.shoplist.room.data.IList;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.views.MainView;

public class MainActivity extends MvpAppCompatActivity implements MainView {

    private static final String TAG = "myLogs";
    public static final int LIST_ACTIVITY_CODE = 1;

    @InjectPresenter
    MainPresenter presenter;

    @BindView(R.id.scrollView)
    DiscreteScrollView scrollView;

    private List<IListWithItems> listLists;
    private IListAdapter iListAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        App.initApp(this);

        productList = ProductModel.getAll(this);

        presenter.init(this);
    }

    @OnClick(R.id.btnNewList)
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

        int count = ListModel.getCountByTimeRange(this, timestampFrom, timestampTo);

        if (count != 0) {
            etTitle.append(" " + String.valueOf(count+1));
        }

        etTitle.selectAll();

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.title)
                .setView(view)
                .setPositiveButton(R.string.next, (dialog, which) -> {
                    IList list = new IList();
                    list.setTitle(etTitle.getText().toString());

                    int id = presenter.createList(getApplicationContext(), list);
                    list.setId(id);

                    IListWithItems iListWithItems = new IListWithItems();
                    iListWithItems.setList(list);
                    iListWithItems.setItems(new ArrayList<>());

                    Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                    intent.putExtra("list", Parcels.wrap(iListWithItems));
                    intent.putExtra("new_list", true);
                    startActivityForResult(intent, LIST_ACTIVITY_CODE);
                })
                .setNeutralButton(R.string.cancel, null)
                .create();

        alertDialog.show();

        etTitle.requestFocus();

        InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
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
        iListAdapter = new IListAdapter(this, listLists, productList);
    }

    @Override
    public void initList() {
//        scrollView.setSlideOnFling(true);
//        scrollView.setOffscreenItems(1);
        scrollView.setItemTransitionTimeMillis(100);
        scrollView.setAdapter(iListAdapter);
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
        Toast.makeText(this, R.string.is_removed, Toast.LENGTH_LONG).show();
    }

    @Override
    public void smoothScrollToBegin() {
        scrollView.scrollTo(0, 0);
    }

    @Override
    public void showLayoutNotItems() {

    }

    @Override
    public void hideLayoutNotItems() {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LIST_ACTIVITY_CODE) {
            int position = data.getIntExtra("position", -1);

            if (resultCode == RESULT_OK) {
                IListWithItems iListWithItems = Parcels.unwrap(data.getParcelableExtra("list"));

                if (position == -1) {
                    if (data.getBooleanExtra("new_list", false)) {
                        /**
                         * Если нет ни одного элемента, создать список и положить туда первый элемент.
                         * Иначе добавить в адаптер
                         */
                        if (listLists == null || listLists.size() == 0) {
                            listLists = new ArrayList<>();
                            listLists.add(iListWithItems);

                            iListAdapter = new IListAdapter(this, listLists, ProductModel.getAll(this));
                            presenter.initList();
                        } else {
                            presenter.addAdapterItemToBegin(iListWithItems);
                            presenter.smoothScrollToBegin();
                        }
                    }
                } else {
                    presenter.updateAdapterItem(position, iListWithItems);
                    presenter.setProducts(ProductModel.getAll(this));
                }
            }

            if (resultCode == App.RESULT_DELETE) {
                if (position != -1) {
                    presenter.removeAdapterItem(position);
                }
            }
        }
    }
}
