package com.classicloner.remindme;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.Random;

import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;

public class MainActivity extends AppCompatActivity {

    private NotificationManagerCompat notifManager;
    private NotificationManager notifMan;
    private String notifTag = "RemindMeNotification";
    String GROUP_KEY = "RemindMeNotificationGroup";
    int SUMMARY_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabHome = (FloatingActionButton) findViewById(R.id.fabHome);
        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Set it Desktop wallpaper", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton fabLock = (FloatingActionButton) findViewById(R.id.fabLock);
        fabLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Set as Lockscreen wallpaper", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FloatingActionButton fabNotification = (FloatingActionButton) findViewById(R.id.fabNotification);
        fabNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Notification is set", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                EditText editText = findViewById(R.id.rET);
                try {
                    createNotification( editText.getText().toString(),"...", getApplicationContext());
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }

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

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        Log.d("onNewIntent", "NewIntent");
        this.setIntent(intent);
    }

    public void createNotification(String aMessage, String body, Context context) throws PendingIntent.CanceledException {
        int NOTIFY_ID = new Random().nextInt();
        String CHANNEL_ID = "REMINDER_CHANNEL_ID"; // default_channel_id
        int SUMMARY_ID = 0;
        String GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL";
        String title = "My title"; // Default Channel

        Intent intent2;
        PendingIntent pi2;


        if (notifMan == null) {
            notifMan = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifMan.getNotificationChannel(CHANNEL_ID);
            if (mChannel == null) {
                mChannel = new NotificationChannel(CHANNEL_ID, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifMan.createNotificationChannel(mChannel);
            }
        }

        intent2 = new Intent(this, NotificationListener.class);

        intent2.replaceExtras(new Bundle());

        intent2.putExtra("NOTIFICATION_TAG" , notifTag);
        intent2.putExtra("INTENT_EXTRA_NOTIFICATION_ID" , NOTIFY_ID);
        pi2 = PendingIntent.getBroadcast(this, 0, intent2,  PendingIntent.FLAG_UPDATE_CURRENT);


        Notification newMessageNotification1 =
                new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_popup_reminder)
                        .setContentTitle(aMessage)
                        .setContentText(aMessage)
                        .setGroup(GROUP_KEY_WORK_EMAIL)
//                        .addAction( R.drawable.ic_launcher_foreground,"Done" , pi2)
                        //.setOngoing(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(aMessage))
                        .build();




//        Notification newMessageNotification2 =
//                new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
//                        .setSmallIcon(R.drawable.ic_launcher_foreground)
//                        .setContentTitle(aMessage)
//                        .setContentText("Please join us to celebrate the...")
//                        .setGroup(GROUP_KEY_WORK_EMAIL)
//                        .addAction( R.drawable.ic_launcher_foreground,"Done" , pi2)
//                        //.setOngoing(true)
//                        .build();


        Notification summaryNotification =
                new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                        .setContentTitle(aMessage)
                        //set content text to support devices running API level < 24
                        .setContentText("...")
                        .setSmallIcon(android.R.drawable.ic_popup_reminder)
                        //build summary info into InboxStyle template
                        .setStyle(new NotificationCompat.InboxStyle()
                                .addLine("Alex Faarborg  Check this out")
                                .addLine("Jeff Chang    Launch Party")
                                .setBigContentTitle("2 new messages")
                                .setSummaryText("..."))
                        //specify which group this notification belongs to
                        .setGroup(GROUP_KEY_WORK_EMAIL)
                        //set this notification as the summary for the group
                        .setGroupSummary(true)

                        .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(NOTIFY_ID, newMessageNotification1);
        NOTIFY_ID = new Random().nextInt();

//        notificationManager.notify(NOTIFY_ID, newMessageNotification2);
        notificationManager.notify(SUMMARY_ID, summaryNotification);
    }
}
