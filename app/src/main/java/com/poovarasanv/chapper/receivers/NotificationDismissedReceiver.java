package com.poovarasanv.chapper.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.poovarasanv.chapper.singleton.Prefs;

/**
 * Created by poovarasanv on 21/7/16.
 */
public class NotificationDismissedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getExtras().getInt("notificationId");


        Prefs.with(context).write("notificationtext", "");

    }
}