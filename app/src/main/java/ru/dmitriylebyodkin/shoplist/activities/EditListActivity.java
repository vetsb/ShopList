package ru.dmitriylebyodkin.shoplist.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dmitriylebyodkin.shoplist.App;
import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.models.ListModel;
import ru.dmitriylebyodkin.shoplist.models.ShopModel;
import ru.dmitriylebyodkin.shoplist.notifications.TimeNotification;
import ru.dmitriylebyodkin.shoplist.presenters.EditListPresenter;
import ru.dmitriylebyodkin.shoplist.room.data.IList;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;
import ru.dmitriylebyodkin.shoplist.room.data.Shop;
import ru.dmitriylebyodkin.shoplist.views.EditListView;

public class EditListActivity extends MvpAppCompatActivity implements EditListView {

    private static final int ADD_SHOP_CODE = 1;
    private static final int TIME_NOTIFICATION_CODE = 2;
    private static final String TAG = "myLogs";

    @InjectPresenter
    EditListPresenter presenter;

    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.tvShop)
    TextView tvShop;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.tvDelete)
    TextView tvDelete;

    private Intent intent;
    private IListWithItems iListWithItems;
    private boolean hasChanges = false;
    private IList list;
    private String title;
    private int timestampNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        ButterKnife.bind(this);

        intent = getIntent();
        iListWithItems = Parcels.unwrap(intent.getParcelableExtra("list"));
        list = iListWithItems.getList();

        title = list.getTitle();
        timestampNotification = list.getTimestampNotification();

        etTitle.setText(list.getTitle());
        etTitle.setSelection(list.getTitle().length());
        etTitle.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && etTitle.getText().toString().trim().equals("")) {
                etTitle.setText(title);
            }
        });

        Shop shop = ShopModel.getById(this, list.getShopId());

        if (shop == null) {
            tvShop.setText(R.string.not_selected_m);
        } else {
            tvShop.setText(shop.getTitle());
        }

        tvShop.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(etTitle.getWindowToken(), 0);

            etTitle.clearFocus();

            Intent shopIntent = new Intent(this, AddShopActivity.class);
            shopIntent.putExtras(intent);
            startActivityForResult(shopIntent, ADD_SHOP_CODE);
        });

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.YYYY");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

        if (list.getTimestampNotification() == 0) {
            calendar.setTimeInMillis(System.currentTimeMillis());

            tvDate.setText(R.string.date);
            tvTime.setText(R.string.time);
        } else {
            calendar.setTimeInMillis(list.getTimestampNotification()*1000L);

            tvDate.setText(sdfDate.format(new Date(calendar.getTimeInMillis())));
            tvTime.setText(sdfTime.format(new Date(calendar.getTimeInMillis())));
        }

        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        tvDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                tvDate.setText(sdfDate.format(new Date(calendar.getTimeInMillis())));
                list.setTimestampNotification((int) (calendar.getTimeInMillis()/1000L));
                hasChanges = true;
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        });

        tvTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                tvTime.setText(sdfTime.format(new Date(calendar.getTimeInMillis())));
                list.setTimestampNotification((int) (calendar.getTimeInMillis()/1000L));
                hasChanges = true;
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

            timePickerDialog.show();
        });

        btnSave.setOnClickListener(v -> {
            if (etTitle.getText().toString().trim().equals("")) {
                etTitle.setText(title);
                list.setTitle(title);
            }

            list.setTitle(etTitle.getText().toString().trim());
            iListWithItems.setList(list);

            Intent alarmIntent = new Intent(this, TimeNotification.class);
//                    .setAction("action 1")
//                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            alarmIntent.putExtra("title", iListWithItems.getList().getTitle());
            alarmIntent.putExtra("list", Parcels.wrap(iListWithItems));


            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, TIME_NOTIFICATION_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT); // FLAG_CANCEL_CURRENT, FLAG_UPDATE_CURRENT

            if (list.getTimestampNotification() != 0 && list.getTimestampNotification() != timestampNotification) {
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, list.getTimestampNotification()*1000L, pendingIntent);
            }

            list.setUpdatedAt((int) (System.currentTimeMillis()/1000L));
            ListModel.update(this, list);

            intent.putExtra("list", Parcels.wrap(iListWithItems));

            setResult(RESULT_OK, intent);
            finish();
        });

        tvDelete.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.deleting)
                    .setMessage(R.string.deleting_message)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        ListModel.deleteTemporarily(this, list);
                        setResult(App.RESULT_DELETE, intent);
                        finish();
                    })
                    .setNegativeButton(R.string.no, null)
                    .create();

            alertDialog.show();
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (etTitle.getText().toString().trim().equals("")) {
            etTitle.setText(title);
            list.setTitle(title);
        }

        list.setTitle(etTitle.getText().toString().trim());
        iListWithItems.setList(list);

        if (!title.equals(list.getTitle())) {
            hasChanges = true;
        }

        if (hasChanges) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.saving_changes)
                    .setMessage("Вы хотите сохранить изменения?")
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        list.setUpdatedAt((int) (System.currentTimeMillis()/1000L));
                        ListModel.update(this, list);

                        intent.putExtra("list", Parcels.wrap(iListWithItems));

                        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent alarmIntent = new Intent(this, TimeNotification.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        alarmIntent.putExtra("list", Parcels.wrap(iListWithItems));

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT); // FLAG_CANCEL_CURRENT
                        am.cancel(pendingIntent);

                        if (list.getTimestampNotification() != 0 && list.getTimestampNotification() != timestampNotification) {
                            Log.d(TAG, "onBackPressed system: " + System.currentTimeMillis()/1000L);
                            Log.d(TAG, "onBackPressed list: " + list.getTimestampNotification());

                            am.set(AlarmManager.RTC_WAKEUP, list.getTimestampNotification()*1000L, pendingIntent);
                        }

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
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_SHOP_CODE) {
            if (resultCode == App.RESULT_ADD) {
                IListWithItems newList = Parcels.unwrap(data.getParcelableExtra("list"));
                Shop shop = Parcels.unwrap(data.getParcelableExtra("shop"));

                list.setShopId(newList.getList().getShopId());
                intent.putExtra("list", Parcels.wrap(newList));

                hasChanges = true;

                if (list.getShopId() == 0) {
                    tvShop.setText(R.string.not_selected_m);
                } else {
                    tvShop.setText(shop.getTitle());
                }
            }
        }
    }
}
