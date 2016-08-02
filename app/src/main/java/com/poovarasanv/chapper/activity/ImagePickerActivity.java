package com.poovarasanv.chapper.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.minimize.android.rxrecycleradapter.RxDataSource;
import com.poovarasanv.chapper.R;
import com.poovarasanv.chapper.databinding.ActivityImagePickerBinding;
import com.poovarasanv.chapper.databinding.ImageItemBinding;
import com.poovarasanv.chapper.singleton.ChapperSingleton;
import com.poovarasanv.chapper.util.RecyclerItemClickListener;
import com.rohitarya.picasso.facedetection.transformation.CenterFaceCrop;
import com.rohitarya.picasso.facedetection.transformation.core.PicassoFaceDetector;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

public class ImagePickerActivity extends AppCompatActivity {

    ActivityImagePickerBinding activityImagePickerBinding;
    Subscription subscription;
    List<String> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityImagePickerBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_picker);

        imageList = new ArrayList<>();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        activityImagePickerBinding.imageGrid.setHasFixedSize(true);
        activityImagePickerBinding.imageGrid.setLayoutManager(new GridLayoutManager(this, 3));
        subscription = Observable.just(ChapperSingleton.getAllShownImagesPath(this))
                .observeOn(Schedulers.io())
                .subscribe(strings -> {
                    RxDataSource<String> dataSource = new RxDataSource<String>(strings);

                    imageList = strings;
                    dataSource
                            .<ImageItemBinding>bindRecyclerView(activityImagePickerBinding.imageGrid, R.layout.image_item)
                            .subscribe(viewHolder -> {
                                Log.i("Items Picker", viewHolder.getItem());
                                ImageItemBinding b = viewHolder.getViewDataBinding();

                                Uri url = Uri.parse("file://" + viewHolder.getItem());

                                Picasso.with(this)
                                        .load(url)
                                        .fit() // use fit() and centerInside() for making it memory efficient.
                                        .centerInside()
                                        .transform(new CenterFaceCrop(130, 130)) //in pixels. You can also use CenterFaceCrop(width, height, unit) to provide width, height in DP.
                                        .into(b.imagePickerImage);


                            });
                });

        activityImagePickerBinding.imageGrid.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        ActivityOptionsCompat options =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        ImagePickerActivity.this, view, "DetailActivity:image");
                        Intent intent = new Intent(ImagePickerActivity.this, ImageViewerActivity.class);
                        intent.putExtra("image", imageList.get(position));
                        ActivityCompat.startActivity(ImagePickerActivity.this, intent, options.toBundle());

                    }
                })
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!subscription.isUnsubscribed())
            subscription.unsubscribe();

        PicassoFaceDetector.releaseDetector();
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
