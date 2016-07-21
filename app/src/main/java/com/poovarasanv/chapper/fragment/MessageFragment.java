package com.poovarasanv.chapper.fragment;

import android.database.DatabaseUtils;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.poovarasanv.chapper.R;
import com.poovarasanv.chapper.databinding.FragmentMessageBinding;
import com.poovarasanv.chapper.singleton.ChapperSingleton;

import de.greenrobot.event.EventBus;


/**
 * Created by poovarasanv on 15/7/16.
 */
public class MessageFragment extends Fragment {


    FragmentMessageBinding fragmentMessageBinding;
    Socket socket;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentMessageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, container, false);
        socket = ChapperSingleton.getSocket(getContext());
        socket.on("videodata", onVideoListener);

        EventBus.getDefault().register(this);

        return fragmentMessageBinding.getRoot();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private Emitter.Listener onVideoListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    byte[] videoByte = (byte[]) args[0];
                    fragmentMessageBinding.receiver.setPreviewData(videoByte);
                }
            });

        }
    };
    public void onEvent(byte[] event) {

    }

}
