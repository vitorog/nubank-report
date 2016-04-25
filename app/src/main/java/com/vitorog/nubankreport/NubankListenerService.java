package com.vitorog.nubankreport;

import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/**
 * Created by vitor.gomes on 25/04/2016.
 */
public class NubankListenerService extends NotificationListenerService {

    private NubankListenerServiceReceiver serviceReceiver;

    @Override
    public void onCreate() {
        serviceReceiver = new NubankListenerServiceReceiver();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }
}
