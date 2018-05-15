package ru.dmitriylebyodkin.shoplist.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.parceler.Parcels;

import ru.dmitriylebyodkin.shoplist.R;
import ru.dmitriylebyodkin.shoplist.activities.InfoActivity;
import ru.dmitriylebyodkin.shoplist.room.data.IListWithItems;

public class TimeNotification extends BroadcastReceiver {
    private static final String TAG = "myLogs";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive extras: " + intent.getExtras());

        for (String key: intent.getExtras().keySet()) {
            Log.d(TAG, "onReceive " + key + ": " + intent.getExtras().get(key));
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "shoplist_channel";

        Intent infoIntent = new Intent(context, InfoActivity.class);
        infoIntent.putExtras(intent);
        infoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, infoIntent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "ShopList notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        IListWithItems listWithItems = Parcels.unwrap(intent.getParcelableExtra("list"));

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker(context.getString(R.string.notification_ticker))
                .setSmallIcon(R.drawable.check)
                .setContentTitle("Список покупок");

        Log.d(TAG, "onReceive: " + listWithItems);

        if (listWithItems == null) {
            notificationBuilder.setContentText("Купить список");
        } else {
            notificationBuilder
                    .setContentText("Купить список \""+listWithItems.getList().getTitle()+"\"")
                    .setContentIntent(pendingIntent);
        }

        notificationManager.notify(1, notificationBuilder.build());
    }
}
