package com.vitorog.nubankreport;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private NotificationReceiver receiver;

    private ArrayList<String> purchasesList = new ArrayList();
    private ArrayAdapter<String> purchasesAdapter;
    private NubankPurchasesDbHelper databaseHelper;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.NUBANK_PURCHASE_LISTENER_INTENT);
        receiver = new NotificationReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        purchasesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, purchasesList);
        ListView list = (ListView)this.findViewById(R.id.notificationsListView);
        list.setAdapter(purchasesAdapter);


        Button createNotificationButton = (Button) this.findViewById(R.id.createNotificationButton);
        createNotificationButton.setVisibility(View.INVISIBLE);
        createNotificationButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this);
                builder.setContentTitle(Constants.NUBANK_NOTIFICATION_TITLE);
                Random r = new Random();
                double rangeMin = 1.0;
                double rangeMax = 1000.0;
                double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
                String valueStr = String.format("%.2f", randomValue);
                valueStr.replace(Constants.NUBANK_CURRENCY_DOT_CHAR, Constants.NUBANK_CURRENCY_COMMA_CHAR);
                String text = Constants.NUBANK_BRAZILIAN_CURRENCY_SYMBOL + valueStr  + Constants.NUBANK_NOTIFICATION_TEXT_EXAMPLE;
                builder.setContentText(text);
                builder.setSmallIcon(R.mipmap.ic_launcher);
                manager.notify((int)System.currentTimeMillis(), builder.build());
            }
        });

        databaseHelper = new NubankPurchasesDbHelper(getApplicationContext());
        readDatabaseEntries();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Destroyed");
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(Constants.ANDROID_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_clear_cache) {
            clearAll();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addNewPurchase(NubankPurchase purchase){
        if(!isDuplicated(purchase)){
            if(saveDatabaseEntry(purchase) != -1) {
                purchasesList.add(purchase.getDisplayString());
                purchasesAdapter.notifyDataSetChanged();
            }else{
                Log.w(TAG, "Error saving entry to database");
            }
        }else{
            Log.w(TAG, "Duplicated notification.");
        }
    }

    private boolean isDuplicated(NubankPurchase purchase) {
        // TODO: Implement a better duplicate check
        return purchasesList.contains(purchase.getDisplayString());
    }

    private void readDatabaseEntries() {
        Log.i(TAG, "Reading database entries");
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
            purchasesList.add(purchase.getDisplayString());
            purchasesAdapter.notifyDataSetChanged();
        }
        db.close();
    }

    private long saveDatabaseEntry(NubankPurchase purchase){
        Log.i(TAG, "Inserting new entry on database");
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(NubankPurchasesContract.PurchaseEntry.COLUMN_NAME_PLACE, purchase.getPlace());
        values.put(NubankPurchasesContract.PurchaseEntry.COLUMN_NAME_VALUE, purchase.getFormattedValueStr());
        values.put(NubankPurchasesContract.PurchaseEntry.COLUMN_NAME_DATE, purchase.getDate());

        long newRowId = db.insert(NubankPurchasesContract.PurchaseEntry.TABLE_NAME, null, values);
        db.close();
        return newRowId;
    }


    private void clearAll() {
        Log.i(TAG, "Clear all");
        purchasesList.clear();
        purchasesAdapter.notifyDataSetChanged();
        clearDatabase();
    }

    private void clearDatabase() {
        Log.i(TAG, "Clearing database");
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        db.delete(NubankPurchasesContract.PurchaseEntry.TABLE_NAME, null, null);
        db.close();
    }

    class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            NubankPurchase purchase = new NubankPurchase(intent);
            if(purchase.isValid()) {
                addNewPurchase(purchase);
            }
        }
    }
}
