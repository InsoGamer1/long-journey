package com.classicloner.remindme;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


public class NotificationListener  extends BroadcastReceiver {
    private NotificationManager notifManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if ( extras!=null) {
            String tag = extras.getString("NOTIFICATION_TAG");
            int notifId = extras.getInt("INTENT_EXTRA_NOTIFICATION_ID");
            Log.d("NotificationListenerS", tag);
            Log.d("NotificationListenerI", String.valueOf(notifId));

            if (notifManager == null) {
                notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            }
            notifManager.cancel(notifId);

        }
    }

}