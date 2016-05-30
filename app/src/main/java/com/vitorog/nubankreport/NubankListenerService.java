package com.vitorog.nubankreport;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    public static boolean isStarted = false;

    @Override
    public void onCreate() {
        Log.i(TAG, "Started");
        serviceReceiver = new NubankListenerServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.NUBANK_NOTIFICATION_LISTENER_INTENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(serviceReceiver, filter);
        isStarted = true;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Destroyed");
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(serviceReceiver);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // This handles big style notifications
        String notificationTitle = sbn.getNotification().extras.getCharSequence(Constants.ANDROID_NOTIFICATION_TITLE).toString();
        // Check if it's a Nubank notification
        if(notificationTitle != null && notificationTitle.contains(Constants.NUBANK_NOTIFICATION_TAG)) {
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

    private boolean checkDuplicateDatabaseEntry(NubankPurchase otherPurchase) {
        Log.i(TAG, "Checking for duplicate entries...");
        NubankPurchasesDbHelper databaseHelper = new NubankPurchasesDbHelper(this);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] projection =  {
                NubankPurchasesContract.PurchaseEntry.COLUMN_NAME_PLACE,
                NubankPurchasesContract.PurchaseEntry.COLUMN_NAME_VALUE,
                NubankPurchasesContract.PurchaseEntry.COLUMN_NAME_DATE
        };

        String sortOrder = NubankPurchasesContract.PurchaseEntry.COLUMN_NAME_DATE + " DESC";
        Cursor c = db.query(NubankPurchasesContract.PurchaseEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);
        while(c.moveToNext()){
            String formattedValue = c.getString(c.getColumnIndex(NubankPurchasesContract.PurchaseEntry.COLUMN_NAME_VALUE));
            String place = c.getString(c.getColumnIndex(NubankPurchasesContract.PurchaseEntry.COLUMN_NAME_PLACE));
            String date = c.getString(c.getColumnIndex(NubankPurchasesContract.PurchaseEntry.COLUMN_NAME_DATE));
            NubankPurchase purchase = new NubankPurchase(formattedValue, place, date);
            if(purchase.getTimeStamp().equals(otherPurchase.getTimeStamp())){
                return true;
            }
        }
        db.close();
        return false;
    }

    private long saveDatabaseEntry(NubankPurchase purchase){
        Log.i(TAG, "Inserting new entry on database");
        NubankPurchasesDbHelper databaseHelper = new NubankPurchasesDbHelper(this);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(NubankPurchasesContract.PurchaseEntry.COLUMN_NAME_PLACE, purchase.getPlace());
        values.put(NubankPurchasesContract.PurchaseEntry.COLUMN_NAME_VALUE, purchase.getFormattedValueStr());
        values.put(NubankPurchasesContract.PurchaseEntry.COLUMN_NAME_DATE, purchase.getDate());

        long newRowId = db.insert(NubankPurchasesContract.PurchaseEntry.TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }



    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //Do nothing
    }

    class NubankListenerServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            NubankPurchase purchase = new NubankPurchase(intent);
            if(purchase.isValid()){
                if(!checkDuplicateDatabaseEntry(purchase)) {
                    if(saveDatabaseEntry(purchase) != -1) {
                        // Broadcast to main activity
                        LocalBroadcastManager.getInstance(NubankListenerService.this).sendBroadcast(intent.setAction(Constants.NUBANK_REPORT_MAIN_ACTIVITY_INTENT));
                    }else{
                        Log.w(TAG, "Error saving entry to database");
                    }
                }else {
                    Log.w(TAG, "Duplicated notification.");
                }
            }

        }
    }
}
