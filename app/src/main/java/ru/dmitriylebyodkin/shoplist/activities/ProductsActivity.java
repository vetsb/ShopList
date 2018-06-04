package ru.dmitriylebyodkin.shoplist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.parceler.Parcels;

import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.fragments.ProductsFragment;
import ru.dmitriylebyodkin.shoplist.room.data.Category;
import ru.dmitriylebyodkin.shoplist.room.data.Product;

public class ProductsActivity extends AppCompatActivity {

    public static final int EDIT_PRODUCT_CODE = 1;
    private static final String TAG = "myLogs";
    private ProductsFragment productsFragment;
    private FragmentManager fragmentManager;
    private Intent intent;
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        intent = getIntent();
        category = Parcels.unwrap(intent.getParcelableExtra("category"));

        productsFragment = new ProductsFragment();
        productsFragment.setCategoryId(category.getId());
        productsFragment.setProductsActivity(true);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frameLayout, productsFragment)
                .commit();

        setTitle(category.getTitle());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void showProductDeleteDialog(Product product, int position) {
        productsFragment.showDeleteDialog(product, position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_PRODUCT_CODE) {
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra("position", -1);
                Product product = Parcels.unwrap(data.getParcelableExtra("product"));

                if (product.getCategoryId() == category.getId()) {
                    if (position != -1) {
                        productsFragment.update(product, position);
                    }
                } else {
                    productsFragment.delete(position);
                }
            }
        }

        if (requestCode == MainActivity.ADD_PRODUCT_CODE) {
            if (resultCode == RESULT_OK) {
                Product product = Parcels.unwrap(data.getParcelableExtra("product"));
                productsFragment.insert(product);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
