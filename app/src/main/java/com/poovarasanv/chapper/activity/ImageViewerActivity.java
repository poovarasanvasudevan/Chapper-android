package com.poovarasanv.chapper.activity;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.poovarasanv.chapper.R;
import com.poovarasanv.chapper.databinding.ActivityImageViewerBinding;
import com.squareup.picasso.Picasso;

public class ImageViewerActivity extends AppCompatActivity {


    ActivityImageViewerBinding activityImageViewerBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityImageViewerBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_viewer);

        setSupportActionBar(activityImageViewerBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String image = getIntent().getStringExtra("image");


        Uri url = Uri.parse("file://" + image);

        Picasso.with(this)
                .load(url)
                .into(activityImageViewerBinding.imageFull);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
