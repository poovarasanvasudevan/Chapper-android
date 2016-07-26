package com.poovarasanv.chapper.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.poovarasanv.chapper.R;
import com.poovarasanv.chapper.databinding.ActivityCameraBinding;

/**
 * Created by poovarasanv on 26/7/16.
 */
public class CameraActivity extends AppCompatActivity {

    ActivityCameraBinding activityCameraBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityCameraBinding = DataBindingUtil.setContentView(this, R.layout.activity_camera);
    }
}
