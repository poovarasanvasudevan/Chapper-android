package com.poovarasanv.chapper.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;
import com.poovarasanv.chapper.singleton.ChapperSingleton;

/**
 * Created by poovarasanv on 14/7/16.
 */
public class NetworkConnectionReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkStateReceiver";
    Socket socket;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d(TAG, "Network connectivity change");

        if (socket == null)
            socket = ChapperSingleton.getSocket(context);

        if (intent.getExtras() != null) {
            final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

            if (ni != null && ni.isConnectedOrConnecting()) {
                if (socket.connected() == false) {
                    socket.connect();

                    Toast.makeText(context,"Network Available...",Toast.LENGTH_LONG).show();
                }
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                if (socket.connected() == true) {
                    socket.disconnect();
                    Toast.makeText(context,"No Network...",Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}