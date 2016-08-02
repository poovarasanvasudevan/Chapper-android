package com.poovarasanv.chapper.app;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.github.nkzawa.socketio.client.Socket;
import com.poovarasanv.chapper.service.SocketWatcherService;
import com.poovarasanv.chapper.singleton.ChapperSingleton;
import com.rohitarya.picasso.facedetection.transformation.core.PicassoFaceDetector;

/**
 * Created by poovarasanv on 14/7/16.
 */
public class Chapper extends Application {


    Socket socket;


    @Override
    public void onCreate() {
        super.onCreate();


        PicassoFaceDetector.initialize(this);


        ChapperSingleton.init(getApplicationContext());

        ChapperSingleton.initAndroid();

        //ChapperSingleton.setOnline();
        if (ChapperSingleton.isMyServiceRunning(SocketWatcherService.class, getApplicationContext()) == false) {
            startService(new Intent(getApplicationContext(), SocketWatcherService.class));

            //socket = ChapperSingleton.getSocket(getApplicationContext());

        }


        Log.d("fghgf", "hfghgf");
    }


    @Override
    public void onTerminate() {
        ChapperSingleton.setOffline();
        super.onTerminate();
    }
}
