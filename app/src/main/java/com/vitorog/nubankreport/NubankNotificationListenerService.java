package com.vitorog.nubankreport;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/**
 * Created by vitor.gomes on 25/04/2016.
 */
public class NubankNotificationListenerService extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }
}
