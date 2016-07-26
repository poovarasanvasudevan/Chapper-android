package com.poovarasanv.chapper.fragment;

import android.content.Intent;
import android.database.DatabaseUtils;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.minimize.android.rxrecycleradapter.RxDataSource;
import com.poovarasanv.chapper.MainActivity;
import com.poovarasanv.chapper.R;
import com.poovarasanv.chapper.activity.MessageActivity;
import com.poovarasanv.chapper.databinding.ContactItemBinding;
import com.poovarasanv.chapper.databinding.FragmentMessageBinding;
import com.poovarasanv.chapper.models.Contact;
import com.poovarasanv.chapper.models.MessageContact;
import com.poovarasanv.chapper.singleton.ChapperSingleton;
import com.poovarasanv.chapper.util.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by poovarasanv on 15/7/16.
 */
public class MessageFragment extends Fragment {


    FragmentMessageBinding fragmentMessageBinding;
    Socket socket;
    Subscription subscription;
    RxDataSource<MessageContact> rxDataSource;
    List<MessageContact> messageContacts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentMessageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, container, false);
        socket = ChapperSingleton.getSocket(getContext());

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        fragmentMessageBinding.messageUser.setLayoutManager(linearLayoutManager);


        subscription = Observable.just(ChapperSingleton.getAllMessageUser())
                .observeOn(Schedulers.io())
                .subscribe(contacts -> {
                    messageContacts = new ArrayList<MessageContact>(contacts);
                    rxDataSource = new RxDataSource<>(contacts);
                    rxDataSource
                            .<ContactItemBinding>bindRecyclerView(fragmentMessageBinding.messageUser, R.layout.contact_item)
                            .subscribe(viewHolder -> {
                                ContactItemBinding b = viewHolder.getViewDataBinding();
                                b.inivite.setVisibility(View.GONE);
                                if (viewHolder.getItem().getImage() == null) {
                                    b.contactImage.setImageDrawable(getResources().getDrawable(R.drawable.default_image));
                                } else {
                                    b.contactImage.setImageURI(Uri.parse(viewHolder.getItem().getImage()));
                                }
                                b.contactName.setText(viewHolder.getItem().getName());
                                b.contactNumber.setText(viewHolder.getItem().getMessage());
                            });
                });


        fragmentMessageBinding.messageUser.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        ActivityOptionsCompat options =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        getActivity(), view, "DetailActivity:image");
                        Intent intent = new Intent(getActivity(), MessageActivity.class);
                        intent.putExtra("phonenumber", messageContacts.get(position).getNumber());
                        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());

                    }
                })
        );

        return fragmentMessageBinding.getRoot();
    }

    @Override
    public void onDestroy() {
        subscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
