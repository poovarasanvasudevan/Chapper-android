package com.poovarasanv.chapper.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.poovarasanv.chapper.R;
import com.poovarasanv.chapper.databinding.ActivityImageViewerBinding;

public class ImageViewerActivity extends AppCompatActivity {


    ActivityImageViewerBinding activityImageViewerBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityImageViewerBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_viewer);

        setSupportActionBar(activityImageViewerBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
