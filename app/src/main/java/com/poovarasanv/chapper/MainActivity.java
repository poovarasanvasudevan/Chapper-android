package com.poovarasanv.chapper;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.poovarasanv.chapper.activity.HomeActivity;
import com.poovarasanv.chapper.activity.LoginActivity;
import com.poovarasanv.chapper.app.Chapper;
import com.poovarasanv.chapper.databinding.ActivityMainBinding;
import com.poovarasanv.chapper.pojo.Message;
import com.poovarasanv.chapper.pojo.MySettings;
import com.poovarasanv.chapper.singleton.ChapperSingleton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";
    ActivityMainBinding activityMainBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.setProgressStatus(View.VISIBLE);

        if (ChapperSingleton.isLoggedIn()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent i = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 2000);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 2000);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
