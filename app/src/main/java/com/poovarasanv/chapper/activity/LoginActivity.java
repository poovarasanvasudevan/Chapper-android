package com.poovarasanv.chapper.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;
import com.poovarasanv.chapper.R;
import com.poovarasanv.chapper.app.Chapper;
import com.poovarasanv.chapper.databinding.ActivityLoginBinding;
import com.poovarasanv.chapper.pojo.MySettings;
import com.poovarasanv.chapper.singleton.ChapperSingleton;


import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding activityLoginBinding;
    Socket socket;

    //6379
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        activityLoginBinding.setLicenseError(View.GONE);
        activityLoginBinding.setMobileNumberError(View.GONE);
        socket = ChapperSingleton.getSocket(getApplicationContext());

        activityLoginBinding.loginBtn.setOnClickListener(view -> {
            boolean validationStatus = true;
            if (activityLoginBinding.mobileNumber.getText().toString().trim().length() < 10) {
                activityLoginBinding.setMobileNumberError(View.VISIBLE);
                validationStatus = false;
            } else {
                activityLoginBinding.setMobileNumberError(View.GONE);
            }
            if (activityLoginBinding.licenseBox.isChecked() == false) {
                activityLoginBinding.setLicenseError(View.VISIBLE);
                validationStatus = false;
            } else {
                activityLoginBinding.setLicenseError(View.GONE);
            }

            if (validationStatus) {

                ChapperSingleton.writeLogin(activityLoginBinding.mobileNumber.getText().toString().trim());
                socket.emit("phonenumber", activityLoginBinding.mobileNumber.getText().toString().trim());
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
