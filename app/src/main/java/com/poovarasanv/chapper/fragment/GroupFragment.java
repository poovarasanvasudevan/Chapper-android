package com.poovarasanv.chapper.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.poovarasanv.chapper.R;
import com.poovarasanv.chapper.databinding.FragmentGroupBinding;

/**
 * Created by poovarasanv on 15/7/16.
 */
public class GroupFragment extends Fragment {

    FragmentGroupBinding fragmentGroupBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentGroupBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_group, container, false);
        return fragmentGroupBinding.getRoot();
    }
}
