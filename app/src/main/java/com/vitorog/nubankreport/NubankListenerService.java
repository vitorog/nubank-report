package com.vitorog.nubankreport;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * Created by vitor.gomes on 25/04/2016.
 */
public class NubankListenerService extends NotificationListenerService {

    private NubankListenerServiceReceiver serviceReceiver;

    @Override
    public void onCreate() {
        Log.i("Listener Service", "onCreate");
        serviceReceiver = new NubankListenerServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.vitorog.nubankreport.NUBANK_LISTENER");
        registerReceiver(serviceReceiver,filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serviceReceiver);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
      Log.i("NotificationPosted", "Posted");
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("NotificationRemoved", "Removed");
    }

    class NubankListenerServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Receiver", "TEST");
        }
    }
}
