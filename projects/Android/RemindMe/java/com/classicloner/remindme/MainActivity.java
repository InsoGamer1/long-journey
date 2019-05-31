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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.rtugeek.android.colorseekbar.ColorSeekBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static android.support.v4.app.NotificationCompat.DEFAULT_ALL;
import static android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY;

public class MainActivity extends AppCompatActivity {

    private NotificationManager notificationManager;
    private final int SELECT_PHOTO = 1;

    WallpaperManager wallpaperManager ;
    DisplayMetrics displayMetrics ;
    private enum OPTIONS{
        SEND,
        WALLPAPER,
        LOCKSCREEN
    }
    private String appName = "RemindMe";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        ColorSeekBar colorSeekBar = findViewById(R.id.colorSlider);
        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                EditText editText = findViewById(R.id.rET);
                editText.setTextColor(color);
                //colorSeekBar.getAlphaValue();
            }
        });

        ColorSeekBar colorBgSeekBar = findViewById(R.id.colorBackgroundSlider);

        colorBgSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                EditText editText = findViewById(R.id.rET);
                editText.setBackgroundColor(color);
                //colorSeekBar.getAlphaValue();
            }
        });

        Button rmvButton = findViewById(R.id.removeImage);
        rmvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView imageview = findViewById(R.id.imageView);
                imageview.setImageDrawable(null);

                ColorSeekBar colorBgSeekBar = findViewById(R.id.colorBackgroundSlider);
                Button rmvButton = findViewById(R.id.removeImage);
                colorBgSeekBar.setVisibility(View.VISIBLE);
                rmvButton.setVisibility(View.GONE);
            }
        });
        askForPermission();
    }

    public void toolAction(View view) {
        EditText editText = findViewById(R.id.rET);
        hideKeyBoard(editText);
        editText.clearFocus();
        FrameLayout frameLayout = findViewById(R.id.container);
        switch (view.getId()) {
            case R.id.fabHome:
                if ( editText.getText().length()==0) {
                    Snackbar.make(view, "Message can not be empty" , Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    return;
                }
                if ( HandleImageAction(frameLayout, OPTIONS.WALLPAPER) ){
                    Snackbar.make(view, "Wallpaper is set!" , Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }else{
                    Snackbar.make(view, "Error : unable to set wallpaper" , Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
                break;

            case R.id.fabLock:
                if ( editText.getText().length()==0) {
                    Snackbar.make(view, "Message can not be empty" , Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    return;
                }
                if ( HandleImageAction(frameLayout, OPTIONS.LOCKSCREEN) ){
                    Snackbar.make(view, "Lock Wallpaper is set!" , Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }else{
                    Snackbar.make(view, "Error : unable to set lock wallpaper" , Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
                break;
            case R.id.fabNotification:

                if ( editText.getText().length()==0) {
                    Snackbar.make(view, "Message can not be empty" , Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    return;
                }
                createNotification( editText.getText().toString(),"...", getApplicationContext());
                Snackbar.make(view, "Notification is set", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            case R.id.PickImage:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                break;
            case R.id.action_share:
                if ( editText.getText().length()==0) {
                    Toast.makeText(this.getApplicationContext(),"Empty message is no allowed",Toast.LENGTH_SHORT).show();
                    return;
                }
                HandleImageAction(frameLayout, OPTIONS.SEND);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (requestCode == SELECT_PHOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = imageReturnedIntent.getData();
                    assert imageUri != null;
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    ColorSeekBar colorBgSeekBar = findViewById(R.id.colorBackgroundSlider);
                    Button rmvButton = findViewById(R.id.removeImage);
                    ImageView imageview = findViewById(R.id.imageView);
                    imageview.setImageBitmap(selectedImage);
                    colorBgSeekBar.setVisibility(View.GONE);
                    rmvButton.setVisibility(View.VISIBLE);
                    EditText editText = findViewById(R.id.rET);
                    editText.setBackgroundColor(Color.TRANSPARENT);

//                  Drawable selectedImageDrawable = new BitmapDrawable(getResources(), selectedImage);
//                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                      editText.setBackground(selectedImageDrawable);
//                  }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
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
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_share) {
//            return true;
//        }
//
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        Log.d("onNewIntent", "NewIntent");
        this.setIntent(intent);
    }

    public void createNotification(String aMessage, String body, Context context){
        int NOTIFY_ID = new Random().nextInt();
        String CHANNEL_ID = "REMINDER_CHANNEL_ID"; // default_channel_id
        String GROUP_KEY = "com.classicloner.NOTIF_GROUP";
        String title = appName+"Channel"; // Default Channel

        Intent intent2;
        PendingIntent pi2;


        if (notificationManager == null) {
            notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (mChannel == null) {
                mChannel = new NotificationChannel(CHANNEL_ID, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        intent2 = new Intent(this, NotificationListener.class);

        intent2.replaceExtras(new Bundle());

        String notificationTag = appName+"Notification";
        intent2.putExtra("NOTIFICATION_TAG" , notificationTag);
        intent2.putExtra("INTENT_EXTRA_NOTIFICATION_ID" , NOTIFY_ID);
        intent2.setAction(String.valueOf(NOTIFY_ID));
        pi2 = PendingIntent.getBroadcast(this, 0, intent2,  PendingIntent.FLAG_UPDATE_CURRENT );

        if ( aMessage.length() > 24) body = aMessage;
        else body = "";

        Bundle extra = new Bundle();
        extra.putInt("THISS" , NOTIFY_ID);
        Notification newMessageNotification1 =
                new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_popup_reminder)
                        .setContentTitle(aMessage)
                        //.setContentText("Body:"+aMessage)
                        .setGroup(GROUP_KEY)
                        .addAction(android.R.drawable.ic_dialog_info ,"Done" , pi2)
                        .setOngoing(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                        .setAutoCancel(true)
                        .setExtras(extra)
                        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setDefaults(DEFAULT_ALL)
                        .build();


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFY_ID, newMessageNotification1);

    }

    public String saveBitmapToImage(Bitmap bitmap){
        try {
            // Save the bitmap object to file
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String currentDateTimeString = df.format(c);
            String filename = "IMG_"+currentDateTimeString+".png";
            String folderPath = Environment.getExternalStorageDirectory()+"/"+appName;
            createFolderInExternal(folderPath);
            String completeFilename = folderPath+"/"+filename;

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(completeFilename));
            return completeFilename;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Bitmap convertTextViewToImage(FrameLayout sourceView , boolean isSave) {
        hideKeyBoard(sourceView);
        sourceView.clearFocus();
        sourceView.setDrawingCacheEnabled(true); // Enable drawing cache before calling the getDrawingCache() method
        // Get bitmap object from the View
        Bitmap bitmap;

        sourceView.buildDrawingCache();
        //bitmap = sourceView.getDrawingCache();
        bitmap = Bitmap.createBitmap(sourceView.getDrawingCache());
        sourceView.destroyDrawingCache();

        if (isSave)
            saveBitmapToImage(bitmap);
        return bitmap;
    }

    // Wallpaper and stuff

    public boolean HandleImageAction(FrameLayout frameLayout, OPTIONS options){

        if (options==OPTIONS.SEND) {
            Bitmap bitmap1 = convertTextViewToImage(frameLayout,false);
            if ( bitmap1==null) {
                return false;
            }
            String imagePath = saveBitmapToImage(bitmap1);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse((imagePath)));
            String SHARE_PHOTO = "Share image";
            startActivity(Intent.createChooser(shareIntent, SHARE_PHOTO));
            return true;
        }
        Bitmap bitmap1 = convertTextViewToImage(frameLayout,true);
        if ( bitmap1==null) {
            return false;
        }

        int[] resolution ;
        resolution = GetScreenWidthHeight();
        int width = resolution[0];
        int height = resolution[1];

        Bitmap bitmap2  = SetBitmapSize(bitmap1,width,height);
        if (bitmap2==null){
            return false;
        }
        wallpaperManager = WallpaperManager.getInstance(MainActivity.this);
        try {
            if (options==OPTIONS.LOCKSCREEN) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    wallpaperManager.setBitmap(bitmap2, null, true, WallpaperManager.FLAG_LOCK);
                }else{
                    startActivity(new Intent(Intent.ACTION_SET_WALLPAPER));
                }
            }else{
                wallpaperManager.setBitmap(bitmap2);
            }

            wallpaperManager.suggestDesiredDimensions(width, height);
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
        //**************** Start: Request Permission Handling ********************************/
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
        //******************* Done: Request Permission Handling ********************************/
    }

    public void createFolderInExternal(String foldername){
        File tempFolder = new File(foldername);
        if ( !tempFolder.exists() ) {
            tempFolder.mkdirs();
        }
    }

    public void hideKeyBoard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), HIDE_IMPLICIT_ONLY );
        }
    }
}
