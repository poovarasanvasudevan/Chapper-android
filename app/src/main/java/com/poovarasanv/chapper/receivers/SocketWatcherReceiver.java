package com.poovarasanv.chapper.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.poovarasanv.chapper.service.SocketWatcherService;
import com.poovarasanv.chapper.singleton.ChapperSingleton;

/**
 * Created by poovarasanv on 14/7/16.
 */
public class SocketWatcherReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ChapperSingleton.isMyServiceRunning(SocketWatcherService.class, context) == false) {
            Intent i = new Intent(context,SocketWatcherService.class);
            context.startService(i);
        }
    }
}
