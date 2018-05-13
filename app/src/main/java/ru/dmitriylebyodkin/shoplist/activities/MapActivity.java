package ru.dmitriylebyodkin.shoplist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.parceler.Parcels;

import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.room.data.Shop;

public class MapActivity extends AppCompatActivity {

    private Intent intent;
    private Shop shop;
    private SupportMapFragment supportMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        intent = getIntent();
        shop = Parcels.unwrap(intent.getParcelableExtra("shop"));

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(googleMap -> {
            LatLng latLng = new LatLng(shop.getLatitude(), shop.getLongitude());

            googleMap.addMarker(new MarkerOptions().position(latLng).title(shop.getTitle()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        });

        setTitle(shop.getTitle());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
