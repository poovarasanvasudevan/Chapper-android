package com.poovarasanv.chapper.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.poovarasanv.chapper.fragment.ContactsFragment;
import com.poovarasanv.chapper.fragment.GroupFragment;
import com.poovarasanv.chapper.fragment.MessageFragment;

/**
 * Created by poovarasanv on 15/7/16.
 */
public class TabAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public TabAdapter(FragmentManager fm, int mNumOfTabs) {
        super(fm);
        this.mNumOfTabs = mNumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MessageFragment tab1 = new MessageFragment();
                return tab1;
            case 1:
                GroupFragment tab2 = new GroupFragment();
                return tab2;
            case 2:
                ContactsFragment tab3 = new ContactsFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
