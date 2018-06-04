package ru.dmitriylebyodkin.shoplist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import ru.dmitriylebyodkin.shoplist.models.UnitModel;
import ru.dmitriylebyodkin.shoplist.presenters.EditProductPresenter;
import ru.dmitriylebyodkin.shoplist.room.data.Category;
import ru.dmitriylebyodkin.shoplist.room.data.Product;
import ru.dmitriylebyodkin.shoplist.room.data.Unit;
import ru.dmitriylebyodkin.shoplist.views.EditItemView;

public class AddProductActivity extends MvpAppCompatActivity implements EditItemView {

    private static final String TAG = "myLogs";
    @InjectPresenter
    EditProductPresenter presenter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.outerLayoutCount)
    LinearLayout outerLayoutCount;
    @BindView(R.id.spinnerCategory)
    Spinner spinnerCategory;
    @BindView(R.id.spinnerUnit)
    Spinner spinnerUnit;
    @BindView(R.id.etCost)
    EditText etCost;
    @BindView(R.id.etNote)
    EditText etNote;

    private Intent intent;
    private Product product;
    private int categoryIndex;
    private List<String> categories;
    private List<Category> categoryList;
    private String title;
    private int categoryId, unit;
    private boolean isEdit = false, hasChanges = false;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        ButterKnife.bind(this);

        intent = getIntent();
        product = Parcels.unwrap(intent.getParcelableExtra("product"));

        if (product == null) {
            product = new Product();
        } else {
            isEdit = true;
        }

        if (product.getCategoryId() == 0) {
            product.setCategoryId(intent.getIntExtra("category_id", product.getCategoryId()));
        }

        if (isEdit) {
            title = product.getTitle() == null ? "" : product.getTitle();
            categoryId = product.getCategoryId();
            unit = product.getUnit();
            etTitle.setText(title);
            etTitle.setSelection(title.length());
        }

        RxTextView.textChangeEvents(etTitle)
                .map(text -> text.text().toString())
                .subscribe(text -> {
                    if (menu != null) {
                        if (text.trim().length() > 0) {
                            menu.getItem(0).setVisible(true);
                        } else {
                            menu.getItem(0).setVisible(false);
                        }
                    }
                });

        outerLayoutCount.setVisibility(View.GONE);
        etCost.setVisibility(View.GONE);
        etNote.setVisibility(View.GONE);

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

        List<String> units = new ArrayList<>();
        List<Unit> unitList = UnitModel.getAll(this);
        int selectionId = 0;
        int i = 0;

        for (Unit unit: unitList) {
            units.add(unit.getTitle());

            if (unit.getId() == product.getUnit()) {
                selectionId = i;
            }

            i++;
        }

        ArrayAdapter unitAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item, units);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        spinnerUnit.setAdapter(unitAdapter);
        spinnerUnit.setSelection(selectionId);
        spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                product.setUnit(unitList.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        presenter.init(this, product);
    }

    @Override
    public void setCategoryIndex(int categoryIndex) {
        this.categoryIndex = categoryIndex;
        spinnerCategory.setSelection(categoryIndex);
    }

    @Override
    public void setCategories(List<String> categories) {
        this.categories = categories;

        ArrayAdapter categoryAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        spinnerCategory.setAdapter(categoryAdapter);
    }

    @Override
    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_list_menu, menu);
        this.menu = menu;

        if (!isEdit) {
            menu.getItem(0).setTitle(R.string.add);
        }

        if (etTitle.getText().toString().trim().length() > 0) {
            menu.getItem(0).setVisible(true);
        } else {
            menu.getItem(0).setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navSave:
                if (isEdit) {
                    if (product.getTitle().equals("")) {
                        product.setTitle(title);
                    }
                }

                product.setTitle(etTitle.getText().toString().trim());

                if (isEdit) {
                    presenter.update(this, product);
                } else {
                    int id = (int) presenter.insert(this, product)[0];
                    product.setId(id);
                }

                intent.putExtra("product", Parcels.wrap(product));

                setResult(RESULT_OK, intent);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isEdit) {
            if (etTitle.getText().toString().trim().length() == 0) {
                product.setTitle(title);

                etTitle.setText(title);
                etTitle.setSelection(title.length());
            }

            product.setTitle(etTitle.getText().toString().trim());
            intent.putExtra("product", Parcels.wrap(product));

            if (!title.equals(product.getTitle()) || categoryId != product.getCategoryId() || unit != product.getUnit()) {
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.saving_changes)
                        .setMessage(R.string.do_you_want_save_changes)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            presenter.update(this, product);
                            setResult(RESULT_OK, intent);
                            finish();
                        })
                        .setNegativeButton(R.string.no, (dialog, which) -> {
                            setResult(RESULT_CANCELED, intent);
                            finish();
                        })
                        .setNeutralButton(R.string.cancel, null)
                        .create();

                alertDialog.show();
            } else {
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        } else {
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
