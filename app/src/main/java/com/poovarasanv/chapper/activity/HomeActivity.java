package com.poovarasanv.chapper.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.nkzawa.socketio.client.Socket;
import com.poovarasanv.chapper.R;
import com.poovarasanv.chapper.adapter.TabAdapter;
import com.poovarasanv.chapper.app.Chapper;
import com.poovarasanv.chapper.databinding.ActivityHomeBinding;
import com.poovarasanv.chapper.pojo.Message;
import com.poovarasanv.chapper.singleton.ChapperSingleton;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import iBoxDB.LocalServer.AutoBox;
import iBoxDB.LocalServer.Box;
import iBoxDB.LocalServer.CommitResult;
import iBoxDB.LocalServer.DB;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding activityHomeBinding;
    TabAdapter tabAdapter;
    Socket socket;

    @Override
    protected void onStart() {
        ChapperSingleton.setOnline();
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);


        setSupportActionBar(activityHomeBinding.toolbar);


        activityHomeBinding.tabs.addTab(activityHomeBinding.tabs.newTab().setText("Messages"));
        activityHomeBinding.tabs.addTab(activityHomeBinding.tabs.newTab().setText("Groups"));
        activityHomeBinding.tabs.addTab(activityHomeBinding.tabs.newTab().setText("Contacts"));

        socket = ChapperSingleton.getSocket(getApplicationContext());
        tabAdapter = new TabAdapter(getSupportFragmentManager(), activityHomeBinding.tabs.getTabCount());
        activityHomeBinding.viewpager.setAdapter(tabAdapter);
        activityHomeBinding.viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(activityHomeBinding.tabs));
        activityHomeBinding.tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                activityHomeBinding.viewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        activityHomeBinding.viewpager.setOffscreenPageLimit(3);

//        DB db = new DB(1);
//        db.getConfig().ensureTable("Message", Message.class, "ID");
//        DB.AutoBox box = db.open();
//        //box.insert("Message",new Message(1,"9789356631","9789356631","",new Date(),true));
//        Message o1 = box.get(Message.class, "Message", Long.parseLong("1"));
//
//        Log.i("Message-123",o1.getFromUser());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search: {
                JSONObject object = new JSONObject();
                try {
                    object.put("user", "9659359536");
                    object.put("content", "Hello 9659389536 ");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ChapperSingleton.saveOutgoingMessage("9659389536", "Hello 9659389536 ");
                socket.emit("message", object.toString());
                break;
            }

            case R.id.newGroup: {

                new MaterialDialog.Builder(this)
                        .title("New Group")
                        .customView(R.layout.dialog_new_group, true)
                        .show();

                break;
            }

            case R.id.settings: {
                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                this, item.getActionView(), "DetailActivity:image");
                Intent intent = new Intent(this, SettingsActivity.class);
                ActivityCompat.startActivity(this, intent, options.toBundle());

                break;
            }

            case R.id.help: {

                ActivityOptionsCompat options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                                this, item.getActionView(), "DetailActivity:image");
                Intent intent = new Intent(this, UserLocationActivity.class);
                ActivityCompat.startActivity(this, intent, options.toBundle());
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        ChapperSingleton.setOffline();
        super.onPause();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onResume() {
        ChapperSingleton.setOnline();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        ChapperSingleton.setOffline();
        super.onDestroy();
    }
}
