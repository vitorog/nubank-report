package com.vitorog.nubankreport;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by vitor.gomes on 25/04/2016.
 */
public class NubankListenerService extends NotificationListenerService {

    private NubankListenerServiceReceiver serviceReceiver;

    private final static String TAG = "NubankListenerService";

    @Override
    public void onCreate() {
        Log.i(TAG, "Started");
        serviceReceiver = new NubankListenerServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.NUBANK_NOTIFICATION_LISTENER_INTENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(serviceReceiver, filter);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Destroyed");
        super.onDestroy();
        unregisterReceiver(serviceReceiver);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String notificationTitle = sbn.getNotification().extras.getString(Constants.ANDROID_NOTIFICATION_TITLE);
        // Check if it's a Nubank notification
        if(notificationTitle.contains(Constants.NUBANK_NOTIFICATION_TAG)) {
            Intent msg = new Intent(Constants.NUBANK_NOTIFICATION_LISTENER_INTENT);
            msg.putExtra(Constants.TITLE_KEY, notificationTitle);
            msg.putExtra(Constants.PACKAGE_KEY, sbn.getPackageName());
            msg.putExtra(Constants.TICKER_KEY, sbn.getNotification().tickerText);
            msg.putExtra(Constants.POST_TIME_KEY, sbn.getPostTime());
            msg.putExtra(Constants.ID_KEY, Integer.toString(sbn.getId()));

            msg.putExtra(Constants.TEXT_KEY, sbn.getNotification().extras.getString(Constants.ANDROID_NOTIFICATION_TEXT));
            LocalBroadcastManager.getInstance(this).sendBroadcast(msg);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //Do nothing
    }

    class NubankListenerServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Broadcast to main activity
            LocalBroadcastManager.getInstance(NubankListenerService.this).sendBroadcast(intent.setAction(Constants.NUBANK_PURCHASE_LISTENER_INTENT));
        }
    }
}
