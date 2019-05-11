package com.classicloner.remindme;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;

public class MainActivity extends AppCompatActivity {

    private NotificationManagerCompat notifManager;
    private NotificationManager notifMan;
    private String notifTag = "RemindMeNotification";
    String GROUP_KEY = "RemindMeNotificationGroup";
    int SUMMARY_ID = 0;

    WallpaperManager wallpaperManager ;
    DisplayMetrics displayMetrics ;

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
                EditText editText = findViewById(R.id.rET);
                if ( editText.getText().length()==0) {
                    Snackbar.make(view, "Message can not be empty" , Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    return;
                }
                if ( SetAsWallpaper(editText, false) ){
                    Snackbar.make(view, "Wallpaper is set!" , Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }else{
                    Snackbar.make(view, "Error : unable to set wallpaper" , Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }

            }
        });

        FloatingActionButton fabLock = (FloatingActionButton) findViewById(R.id.fabLock);
        fabLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.rET);
                if ( editText.getText().length()==0) {
                    Snackbar.make(view, "Message can not be empty" , Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    return;
                }
                if ( SetAsWallpaper(editText, true) ){
                    Snackbar.make(view, "Lock Wallpaper is set!" , Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }else{
                    Snackbar.make(view, "Error : unable to set lock wallpaper" , Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });

        FloatingActionButton fabNotification = (FloatingActionButton) findViewById(R.id.fabNotification);
        fabNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.rET);
                if ( editText.getText().length()==0) {
                    Snackbar.make(view, "Message can not be empty" , Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    return;
                }
                createNotification1( editText.getText().toString(),"...", getApplicationContext());
                Snackbar.make(view, "Notification is set", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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

    public void createNotification1(String aMessage, String body, Context context){
        int NOTIFY_ID = new Random().nextInt();
        String CHANNEL_ID = "REMINDER_CHANNEL_ID"; // default_channel_id
        String GROUP_KEY = "com.classicloner.NOTIF_GROUP";
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
        intent2.setAction(String.valueOf(NOTIFY_ID));
        pi2 = PendingIntent.getBroadcast(this, 0, intent2,  PendingIntent.FLAG_UPDATE_CURRENT);

        Bundle extra = new Bundle();
        extra.putInt("THISS" , NOTIFY_ID);
        Notification newMessageNotification1 =
                new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_popup_reminder)
                        .setContentTitle(aMessage)
                        .setContentText(aMessage)
                        .setGroup(GROUP_KEY)
                        .addAction( R.drawable.ic_launcher_foreground,"Done" , pi2)
                        .setOngoing(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(aMessage))
                        .setAutoCancel(true)
                        .setExtras(extra)
//|                        .setContentIntent(pi2)
                        .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFY_ID, newMessageNotification1);

    }

    public Bitmap convertTextViewToImage(TextView tv) {
        askForPermission();
        tv.clearFocus();
        tv.setDrawingCacheEnabled(true); // Enable drawing cache before calling the getDrawingCache() method
        // Get bitmap object from the TextView
        Bitmap tvImage= Bitmap.createBitmap(tv.getDrawingCache());
        try {
            // Save the bitmap object to file
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String currentDateTimeString = df.format(c);
            String filename = "IMG_"+currentDateTimeString+".png";
            String folderPath = Environment.getExternalStorageDirectory()+"/RemindMe";
            createFolderInExternal(folderPath);
            String completeFilename = folderPath+"/"+filename;
            tvImage.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(completeFilename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return tvImage;
    }

    // Wallpaper and stuff

    public boolean SetAsWallpaper(TextView editText, boolean isLockSreen){
        Bitmap bitmap1 = convertTextViewToImage(editText);
        if ( bitmap1==null) {
            return false;
        }
        int[] resolution ;
        resolution = GetScreenWidthHeight();
        int width = resolution[0];
        int height = resolution[1];
        Log.d( "Resolution" , "width : " + width + "  , height : "  + height );

        Bitmap bitmap2  = SetBitmapSize(bitmap1,width,height);
        if (bitmap2==null){
            return false;
        }
        wallpaperManager = WallpaperManager.getInstance(MainActivity.this);
        try {
            if (isLockSreen) {
                Log.d("WALLPAPER" , "LOCK");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    wallpaperManager.setBitmap(bitmap2, null, true, WallpaperManager.FLAG_LOCK);
                }else{
                    startActivity(new Intent(Intent.ACTION_SET_WALLPAPER));
                }
            }else{
                Log.d("WALLPAPER" , "HOME");
                wallpaperManager.setBitmap(bitmap2);
            }

            wallpaperManager.suggestDesiredDimensions(width, width);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public int[] GetScreenWidthHeight(){
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels,displayMetrics.heightPixels};
    }

    public Bitmap SetBitmapSize(Bitmap bitmap,int width,int height){
        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

    public  void askForPermission(){
        /******************* Start: Request Permission Handling ********************************/
        // Check for the permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // get the permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                String explanationStr = "This app need WRITE permisson to store the data in your phone";
                new android.app.AlertDialog.Builder(this).setTitle("Request").setMessage(explanationStr).setPositiveButton(android.R.string.ok, null).setCancelable(false).create().show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        } else {
            Log.d("WRITE_PERM", "Permission is already granted");
        }
        /******************* Done: Request Permission Handling ********************************/
    }

    public void createFolderInExternal(String foldername){
        File tempFolder = new File(foldername);
        if ( !tempFolder.exists() ) {
            tempFolder.mkdirs();
        }
    }
}
