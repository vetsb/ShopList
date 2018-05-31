package ru.dmitriylebyodkin.shoplist.activities;

import android.content.Intent;
import android.os.Bundle;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.presenters.EditItemPresenter;
import ru.dmitriylebyodkin.shoplist.room.data.Category;
import ru.dmitriylebyodkin.shoplist.room.data.IItem;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.views.EditItemView;

public class EditItemActivity extends MvpAppCompatActivity implements EditItemView {

    private static final String TAG = "myLogs";

    @InjectPresenter
    EditItemPresenter presenter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
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
    private int count, categoryId, unit, categoryIndex = 0;
    private float cost;
    private List<Category> categoryList = new ArrayList<>();
    private List<String> categories = new ArrayList<>();

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
        note = item.getNote();

        categoryId = product.getCategoryId();
        unit = product.getUnit();

        presenter.init(this, product);

        etTitle.setText(title);
        etTitle.setSelection(title.length());

        RxTextView.textChangeEvents(etTitle)
                .map(text -> text.text().toString().trim())
                .subscribe(text -> product.setTitle(text));

        etTitle.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && etTitle.getText().toString().trim().equals("")) {
                etTitle.setText(title);
            }
        });

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    product.setCategoryId(0);
                } else {
                    product.setCategoryId(categoryList.get(position-1).getId());
                }

                Log.d(TAG, "onItemSelected: " + product.getCategoryId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (count == 0) {
            etCount.setText(String.valueOf(item.getCount()));
            etCount.setSelection(String.valueOf(item.getCount()).length());
        } else {
            etCount.setText(String.valueOf(count));
            etCount.setSelection(String.valueOf(count).length());
        }
        etCount.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && etCount.getText().toString().trim().equals("")) {
                etCount.setText(String.valueOf(1));
            }
        });

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

        setSupportActionBar(toolbar);
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
            presenter.updateUpdatedAtList(this, item.getListId());
            presenter.updateItem(this, item);
        }

        if (!product.getTitle().equals(title) || product.getCategoryId() != categoryId || product.getUnit() != unit) {
            presenter.updateUpdatedAtList(this, item.getListId());
            presenter.updateProduct(this, product);
        }

        intent.putExtra("item", Parcels.wrap(item));
        intent.putExtra("product", Parcels.wrap(product));
        intent.putExtra("updated_timestamp", (int) (System.currentTimeMillis()/1000L));

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void setCategoryIndex(int categoryIndex) {
        this.categoryIndex = categoryIndex;
        spinnerCategory.setSelection(categoryIndex);
    }

    @Override
    public void setCategories(List<String> categories) {
        this.categories = categories;
        spinnerCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories));
    }

    @Override
    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }
}
