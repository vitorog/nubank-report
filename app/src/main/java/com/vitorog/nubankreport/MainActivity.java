package com.vitorog.nubankreport;

import android.accounts.Account;
import android.accounts.AccountManager;
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
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private NotificationReceiver receiver;

    // TODO: Create custom adapter type to unify these
    private List<NubankPurchase> entries = new ArrayList<>();
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

        // For testing purposes only
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

        Button exportButton = (Button) this.findViewById(R.id.exportButton);
        exportButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(!entries.isEmpty()) {
                    AccountManager accountManager = AccountManager.get(MainActivity.this);
                    // Non-depreacted method is API Level 23 :(
                    Intent intent = accountManager.newChooseAccountIntent(null, null, new String[]{"com.google"}, true, null,
                            null, null, null);
                    startActivityForResult(intent, Constants.ACCOUNT_PICKER_INTENT);
                }else{
                    Toast.makeText(MainActivity.this, "No entries to export.", Toast.LENGTH_SHORT).show();
                }
            }

        });


        databaseHelper = new NubankPurchasesDbHelper(getApplicationContext());
        readDatabaseEntries();

        if(!NubankListenerService.isStarted){
            Toast.makeText(MainActivity.this, "Notification permission required.", Toast.LENGTH_LONG).show();
            goToNotificationSettings();
        }else{
            Toast.makeText(MainActivity.this, "Notifications service running.", Toast.LENGTH_LONG).show();
        }
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
            goToNotificationSettings();
            return true;
        }

        if (id == R.id.action_clear_cache) {
            clearAll();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToNotificationSettings() {
        Intent intent = new Intent(Constants.ANDROID_NOTIFICATION_LISTENER_SETTINGS);
        startActivity(intent);
    }

    private void addNewPurchase(NubankPurchase purchase){
        if(!isDuplicated(purchase)){
            if(saveDatabaseEntry(purchase) != -1) {
                entries.add(purchase);
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
        entries.clear();
        while(c.moveToNext()){
            String formattedValue = c.getString(c.getColumnIndex(NubankPurchasesContract.PurchaseEntry.COLUMN_NAME_VALUE));
            String place = c.getString(c.getColumnIndex(NubankPurchasesContract.PurchaseEntry.COLUMN_NAME_PLACE));
            String date = c.getString(c.getColumnIndex(NubankPurchasesContract.PurchaseEntry.COLUMN_NAME_DATE));
            NubankPurchase purchase = new NubankPurchase(formattedValue, place, date);
            entries.add(purchase);
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
        entries.clear();
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

    private void exportReport(String accountName) {
        Log.i(TAG, "Account name: " + accountName + " chosen");
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{accountName});
        i.putExtra(Intent.EXTRA_SUBJECT, "Nubank Report " + new Date().toString());
        i.putExtra(Intent.EXTRA_TEXT, createReportFromData());
        try {
            startActivityForResult(Intent.createChooser(i, "Send mail..."), Constants.EMAIL_EXPORT_INTENT);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private String createReportFromData() {
        StringBuilder b = new StringBuilder();
        Double totalValue = 0.0;
        for(int i = 0; i < entries.size(); i++){
            NubankPurchase p = entries.get(i);
            b.append(p.getFormattedString() + "\n");
            totalValue += p.getValue();
        }
        b.append("\n");
        b.append("Total value of purchases: ");
        b.append(Double.toString(totalValue));
        b.append("\n");
        return b.toString();
    }

    /*After manually selecting every app related account, I got its Account type using the code below*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.ACCOUNT_PICKER_INTENT) {
            // Receiving a result from the AccountPicker
            if(resultCode == RESULT_OK){
                exportReport(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
            }
        }

        if (requestCode == Constants.EMAIL_EXPORT_INTENT) {
            Toast.makeText(MainActivity.this, "Email report sent.", Toast.LENGTH_SHORT).show();
        }
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
