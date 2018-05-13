package ru.dmitriylebyodkin.shoplist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.jakewharton.rxbinding2.widget.RxTextView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.models.ItemModel;
import ru.dmitriylebyodkin.shoplist.models.ProductModel;
import ru.dmitriylebyodkin.shoplist.presenters.EditListPresenter;
import ru.dmitriylebyodkin.shoplist.room.RoomDb;
import ru.dmitriylebyodkin.shoplist.room.data.Category;
import ru.dmitriylebyodkin.shoplist.room.data.IItem;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.views.EditListView;

public class EditItemActivity extends MvpAppCompatActivity implements EditListView {

    private static final String TAG = "myLogs";
    @InjectPresenter
    EditListPresenter presenter;

    @BindView(R.id.toolbar_1)
    Toolbar toolbar1;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.spinnerCategory)
    Spinner spinnerCategory;
    @BindView(R.id.btnMinus)
    ImageButton btnMinus;
    @BindView(R.id.btnPlus)
    ImageButton btnPlus;
    @BindView(R.id.etCount)
    EditText etCount;
    @BindView(R.id.spinnerUnit)
    Spinner spinnerUnit;
    @BindView(R.id.etCost)
    EditText etCost;
    @BindView(R.id.etNote)
    EditText etNote;

    private Intent intent;
    private IItem item;
    private Product product;
    private String title, note;
    private int count, categoryId, unit;
    private float cost;
    private boolean hasItemChanges, hasProductChanges = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        ButterKnife.bind(this);

        intent = getIntent();
        item = Parcels.unwrap(intent.getParcelableExtra("item"));
        product = Parcels.unwrap(intent.getParcelableExtra("product"));

        title = product.getTitle();
        count = item.getCount();
        cost = item.getCost();
        categoryId = product.getCategoryId();
        unit = product.getUnit();

        etTitle.setText(title);
        etTitle.setSelection(title.length());

        RxTextView.textChangeEvents(etTitle)
                .map(text -> text.text().toString().trim())
                .subscribe(text -> {
                    product.setTitle(text);
                });

        etTitle.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && etTitle.getText().toString().trim().equals("")) {
                etTitle.setText(title);
            }

            Log.d(TAG, "onFocusChange: " + hasFocus);
        });

        List<Category> categoryList = RoomDb.getInstance(this).getCategoryDao().getAll();
        List<String> categories = new ArrayList<>();
        int categoryIndex = 0;
        int index = 0;

        categories.add(getString(R.string.not_selected_w));

        for (Category category: categoryList) {
            categories.add(category.getTitle());

            if (category.getId() == product.getCategoryId()) {
                categoryIndex = index;
            }

            index++;
        }

        if (product.getCategoryId() != 0) {
            categoryIndex++;
        }

        spinnerCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories));
        spinnerCategory.setSelection(categoryIndex);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    product.setCategoryId(0);
                } else {
                    product.setCategoryId(categoryList.get(position-1).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etCount.setText(String.valueOf(item.getCount()));
        etCount.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && etCount.getText().toString().trim().equals("")) {
                etCount.setText(String.valueOf(1));
            }
        });
        etCount.setSelection(String.valueOf(item.getCount()).length());

        btnMinus.setOnClickListener(v -> {
            int count = Integer.parseInt(etCount.getText().toString());

            if (count > 1) {
                count--;
                etCount.setText(String.valueOf(count));
                etCount.setSelection(String.valueOf(count).length());
            }
        });

        btnPlus.setOnClickListener(v -> {
            int count = Integer.parseInt(etCount.getText().toString());
            count++;

            etCount.setText(String.valueOf(count));
            etCount.setSelection(String.valueOf(count).length());
        });

        etCost.setText(String.valueOf(item.getCost()));
        etCost.setSelection(String.valueOf(item.getCost()).length());

//        List<String> units = new ArrayList<>();
//
//        units.add(getString(R.string.not_selected_w));
//        units.addAll(Arrays.asList(Product.UNITS));

        spinnerUnit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Product.UNITS));
        spinnerUnit.setSelection(product.getUnit());
        spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                product.setUnit(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etNote.setText(item.getNote());

        setSupportActionBar(toolbar1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navSave:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        item.setCount(Integer.parseInt(etCount.getText().toString()));
        item.setCost(Float.parseFloat(etCost.getText().toString()));
        item.setNote(etNote.getText().toString());

        if (product.getTitle().equals("")) {
            product.setTitle(title);
        }

        if (item.getCount() != count || item.getCost() != cost || !item.getNote().equals(note)) {
            ItemModel.update(this, item);
//            RoomDb.getInstance(this).getIItemDao().update(item);
        }

        if (!product.getTitle().equals(title) || product.getCategoryId() != categoryId) {
            ProductModel.update(this, product);
//            RoomDb.getInstance(this).getProductDao().update(product);
        }

        intent.putExtra("item", Parcels.wrap(item));
        intent.putExtra("product", Parcels.wrap(product));

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
