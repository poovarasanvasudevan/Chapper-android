package com.poovarasanv.chapper.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.poovarasanv.chapper.pojo.Message;
import com.poovarasanv.chapper.singleton.ChapperSingleton;
import com.poovarasanv.chapper.singleton.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by poovarasanv on 14/7/16.
 */
public class SocketWatcherService extends Service {

    Socket socket;
    static final int NOTIFICATION_ID = 9789;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        socket = ChapperSingleton.getSocket(getApplicationContext());
        socket.on("requestphonenumber", requestPhoneNumberListener);
        socket.on("newMessage", onMessageListener);
        socket.on("allUsers", onAllUserListener);

        Log.i("Socket", "Socket Connected...");

        return START_STICKY;
    }

    private Emitter.Listener requestPhoneNumberListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    if (ChapperSingleton.isLoggedIn()) {
                        socket.emit("phonenumber", ChapperSingleton.getNumber());
                    }
                }
            });

        }
    };

    private Emitter.Listener onAllUserListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    try {
                        ChapperSingleton.saveActiveUsers(new JSONArray(args[0].toString()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "Contact Refreshed", Toast.LENGTH_SHORT).show();
                }
            });

        }
    };


    private Emitter.Listener onMessageListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), args[0].toString(), Toast.LENGTH_LONG).show();
                    try {
                        JSONObject jsonObject = new JSONObject(args[0].toString());

                        ChapperSingleton.saveIncommingMessage(jsonObject);
                        String from = jsonObject.optString("from");

                        Prefs.with(getApplicationContext())
                                .write("notificationtext", Prefs.with(getApplicationContext()).read("notificationtext", "") + from + ":" + jsonObject.optString("content") + "\n");


                        if (Prefs.with(getApplicationContext()).readBoolean("offline") == true) {
                            Notification notification = ChapperSingleton.getNotification(from + " Sent a Message", Prefs.with(getApplicationContext()).read("notificationtext"));
                            NotificationManager notificationManager = ChapperSingleton.getNotificationManager();
                            notificationManager.notify(NOTIFICATION_ID, notification);
                        }
                        socket.emit("messageAck", jsonObject.optInt("messageId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (socket.connected()) {
            socket.disconnect();
            socket = null;

            socket = ChapperSingleton.getSocket(getApplicationContext());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
