package com.poovarasanv.chapper.fragment;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minimize.android.rxrecycleradapter.RxDataSource;
import com.poovarasanv.chapper.R;
import com.poovarasanv.chapper.databinding.ContactItemBinding;
import com.poovarasanv.chapper.databinding.FragmentContactsBinding;
import com.poovarasanv.chapper.intf.EndlessRecyclerViewScrollListener;
import com.poovarasanv.chapper.models.Contact;
import com.poovarasanv.chapper.singleton.ChapperSingleton;

import java.util.List;


/**
 * Created by poovarasanv on 15/7/16.
 */
public class ContactsFragment extends Fragment {

    FragmentContactsBinding fragmentContactsBinding;
    RxDataSource<Contact> rxDataSource;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentContactsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_contacts, container, false);
        fragmentContactsBinding.setProgressVisible(View.VISIBLE);
        fragmentContactsBinding.setScrollVisible(View.GONE);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        fragmentContactsBinding.allContacts.setLayoutManager(linearLayoutManager);
        //   fragmentContactsBinding.allContacts.addItemDecoration(new ItemDecoration(getActivity()));

        new ContactFetcher().execute();

        fragmentContactsBinding.allContacts.setOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                fragmentContactsBinding.setProgressVisible(View.VISIBLE);
                List<Contact> existing = rxDataSource.getRxAdapter().getDataSet();
                List<Contact> newContact = ChapperSingleton.getAllContacts();
                existing.addAll(newContact);

                rxDataSource
                        .updateDataSet(existing) //base items should remain the same
                        .updateAdapter();
                fragmentContactsBinding.setProgressVisible(View.GONE);
            }
        });

        return fragmentContactsBinding.getRoot();
    }


    class ContactFetcher extends AsyncTask<Void, Void, List<Contact>> {

        @Override
        protected void onPreExecute() {
            fragmentContactsBinding.setProgressVisible(View.VISIBLE);
            super.onPreExecute();
        }


        @Override
        protected List<Contact> doInBackground(Void... voids) {
            return ChapperSingleton.getAllContacts();
        }

        @Override
        protected void onPostExecute(List<Contact> contacts) {


            rxDataSource = new RxDataSource<>(contacts);
            rxDataSource
                    .<ContactItemBinding>bindRecyclerView(fragmentContactsBinding.allContacts, R.layout.contact_item)
                    .subscribe(viewHolder -> {
                        ContactItemBinding b = viewHolder.getViewDataBinding();
                        if (viewHolder.getItem().getImage() == null) {
                            b.contactImage.setImageDrawable(getResources().getDrawable(R.drawable.default_image));
                        } else {
                            b.contactImage.setImageURI(Uri.parse(viewHolder.getItem().getImage()));
                        }
                        b.contactName.setText(viewHolder.getItem().getName());
                        b.contactNumber.setText(viewHolder.getItem().getNumber());
                    });

            fragmentContactsBinding.setProgressVisible(View.GONE);
            fragmentContactsBinding.setScrollVisible(View.VISIBLE);

            super.onPostExecute(contacts);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
