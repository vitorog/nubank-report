package com.vitorog.nubankreport;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by vitor.gomes on 25/04/2016.
 */
public class NubankListenerService extends NotificationListenerService {

    private NubankListenerServiceReceiver serviceReceiver;
    private static final String nubankListenerIntent = "com.vitorog.nubankreport.NUBANK_LISTENER";

    @Override
    public void onCreate() {
        Log.i("Listener Service", "onCreate");
        serviceReceiver = new NubankListenerServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(nubankListenerIntent);
        LocalBroadcastManager.getInstance(this).registerReceiver(serviceReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serviceReceiver);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i("NubankListenerService", "onNotificationPosted: " + sbn.getId() + " - " + sbn.getNotification().tickerText + " - " + sbn.getPackageName());
        Intent msg = new Intent(nubankListenerIntent);
        msg.putExtra("package", sbn.getPackageName());
        msg.putExtra("ticker", sbn.getNotification().tickerText);
        msg.putExtra("posttime", Long.toString(sbn.getPostTime()));
        msg.putExtra("id", Integer.toString(sbn.getId()));
        LocalBroadcastManager.getInstance(this).sendBroadcast(msg);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("NotificationRemoved", "Removed");
    }

    class NubankListenerServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i("Receiver", "TEST");
            Intent msg = new Intent(MainActivity.notificationListenerIntent);
            Bundle extras = intent.getExtras();
            msg.putExtra("package", extras.getString("package"));
            msg.putExtra("ticker", extras.getString("ticker"));
            msg.putExtra("posttime", extras.getString("posttime"));
            msg.putExtra("id", extras.getString("id"));
            LocalBroadcastManager.getInstance(NubankListenerService.this).sendBroadcast(msg);
        }
    }
}
