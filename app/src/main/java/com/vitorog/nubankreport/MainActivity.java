package com.vitorog.nubankreport;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

public class MainActivity extends AppCompatActivity {

    private NotificationReceiver receiver;
    final static String notificationListenerIntent = "com.vitorog.nubankreport.NOTIFICATION_LISTENER";

    private ArrayList<String> notificationsList = new ArrayList<String>();
    private ArrayAdapter<String> notificationsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("MainActivity", "onCreate");
        IntentFilter filter = new IntentFilter();
        filter.addAction(notificationListenerIntent);
        receiver = new NotificationReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        notificationsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,  notificationsList);
        ListView list = (ListView)this.findViewById(R.id.notificationsListView);
        list.setAdapter(notificationsAdapter);

        Button accessButton = (Button)this.findViewById(R.id.accessButton);
        accessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intent);
            }
        });

        Button createNotificationButton = (Button) this.findViewById(R.id.createNotificationButton);
        createNotificationButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.i("Create notification","HERE");
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this);
                builder.setContentTitle("Test Notification");
                builder.setContentText("Test Notification Text");
                builder.setAutoCancel(true);
                builder.setSmallIcon(R.mipmap.ic_launcher);
                manager.notify((int)System.currentTimeMillis(), builder.build());
            }
        });

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Notification received", "MainActivity HERE");
            Bundle extras = intent.getExtras();
            notificationsList.add(extras.getString("package") + " - " + extras.getString("id") + " - " + extras.getString("posttime") + " - " + extras.getString("ticker"));
            notificationsAdapter.notifyDataSetChanged();
        }
    }
}
