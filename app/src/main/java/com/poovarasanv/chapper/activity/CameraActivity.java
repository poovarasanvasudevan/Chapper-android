package com.poovarasanv.chapper.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.poovarasanv.chapper.MainActivity;
import com.poovarasanv.chapper.R;
import com.poovarasanv.chapper.databinding.ActivityCameraBinding;
import com.poovarasanv.chapper.databinding.ActivityLoginBinding;
import com.ragnarok.rxcamera.RxCamera;
import com.ragnarok.rxcamera.RxCameraData;
import com.ragnarok.rxcamera.config.RxCameraConfig;
import com.ragnarok.rxcamera.config.RxCameraConfigChooser;
import com.ragnarok.rxcamera.request.Func;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class CameraActivity extends AppCompatActivity {

    ActivityCameraBinding activityCameraBinding;
    public static int ZOOMLEVEL = 0;
    public static boolean FLASH = true;
    public static boolean CLICKED = false;
    RxCamera camera;
    RxCameraConfig config;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCameraBinding = DataBindingUtil.setContentView(this, R.layout.activity_camera);

        setSupportActionBar(activityCameraBinding.toolbar);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);


        activityCameraBinding.zoomSeekBar.setMax(5);
        config = RxCameraConfigChooser.obtain().
                useBackCamera().
                setAutoFocus(true).
                setPreferPreviewFrameRate(15, 30).
                setPreferPreviewSize(new Point(640, 480), true).
                setHandleSurfaceEvent(true).
                get();

        RxCamera.open(this, config).flatMap(rxCamera -> {

            camera = rxCamera;
            return rxCamera.bindTexture(activityCameraBinding.cameraSurface);
        }).flatMap(rxCamera -> rxCamera.startPreview()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RxCamera>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(final RxCamera rxCamera) {
                        camera = rxCamera;
                    }
                });

        camera.action().flashAction(true);

        activityCameraBinding.flashBtn.setOnClickListener(view -> {
            if (FLASH) {
                FLASH = false;
                activityCameraBinding.flashBtn.setImageDrawable(null);
                activityCameraBinding.flashBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_off));
                camera.action().flashAction(true);
            } else {
                FLASH = true;
                activityCameraBinding.flashBtn.setImageDrawable(null);
                activityCameraBinding.flashBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_on));
                camera.action().flashAction(false);
            }
        });
        activityCameraBinding.captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (CLICKED == false) {
                    camera.request().takePictureRequest(false, new Func() {
                        @Override
                        public void call() {
                        }
                    }, 480, 640, ImageFormat.JPEG, true).subscribe(new Action1<RxCameraData>() {
                        @Override
                        public void call(RxCameraData rxCameraData) {
                            String path = Environment.getExternalStorageDirectory() + "/test.jpg";
                            File file = new File(path);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(rxCameraData.cameraData, 0, rxCameraData.cameraData.length);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                                    rxCameraData.rotateMatrix, false);
                            try {
                                file.createNewFile();
                                FileOutputStream fos = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            CLICKED = true;
                            activityCameraBinding.captureBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
                        }
                    });
                } else {

                }
            }
        });

        activityCameraBinding.zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ZOOMLEVEL <= 5) {
                    ZOOMLEVEL = ZOOMLEVEL + 1;
                    camera.action().smoothZoom(ZOOMLEVEL);
                    activityCameraBinding.zoomSeekBar.setProgress(ZOOMLEVEL);
                    activityCameraBinding.zoomLayout.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activityCameraBinding.zoomLayout.setVisibility(View.INVISIBLE);
                        }
                    }, 3000);
                }
            }
        });


        activityCameraBinding.zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ZOOMLEVEL >= 0) {
                    ZOOMLEVEL = ZOOMLEVEL - 1;
                    camera.action().smoothZoom(ZOOMLEVEL);
                    activityCameraBinding.zoomSeekBar.setProgress(ZOOMLEVEL);
                    activityCameraBinding.zoomLayout.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            activityCameraBinding.zoomLayout.setVisibility(View.INVISIBLE);
                        }
                    }, 3000);
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        camera.closeCamera();
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        camera.closeCamera();
        super.onDestroy();
    }
}