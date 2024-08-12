package com.lh.painting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.lh.painting.manager.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class CameraActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {
    private CameraSelector cameraSelector;
    private String[] permissions;
    private int range;
    private Camera camera;
    private ImageCapture imageCapture;
    private PreviewView previewView;
    private ImageView imageView;
    private SeekBar seekBar;

    private Double d;
    private int MODE;//当前状态
    public static final int MODE_NONE = 0;//无操作
    public static final int MODE_DRAG = 1;//单指操作
    public static final int MODE_SCALE = 2;//双指操作

    private Matrix startMatrix;
    private Matrix endMatrix = new Matrix();//变化后的矩阵
    private PointF startPointF = new PointF();//初始坐标
    private float distance;//初始距离
    private float scaleMultiple;//缩放倍数

    private ImageView flashIm, /*imZoom,*/ imBack;
    private ImageView imPhoto;
    private Bitmap bitmap;
    private String curBitmapPath;
    private boolean hasPermission = false;
    private boolean isMain;
    private String ismain;
   // private static List<MaxInterstitialAd> adsList;
    private ActivityResultLauncher<Intent> intentActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Utils.setStatusBarTextColor(this, true);

        //adsList = Utils.getAllAd();
        Intent intent = getIntent();
       // isMain = intent.getBooleanExtra(PaintingApp.Key_ISMAIN, false);
       // showAd();
        previewView = findViewById(R.id.preview);
        imageView = findViewById(R.id.image);
        imBack = findViewById(R.id.back);

        seekBar = findViewById(R.id.seekbar);
        flashIm = findViewById(R.id.im_flash);
        imPhoto = findViewById(R.id.im_photo);
       // imZoom = findViewById(R.id.im_zoom);

        curBitmapPath = getIntent().getStringExtra("filePath");

        intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
//                Log.d("--------------tt","------bitmap-----width="+width+"-----height="+height);
                imageView.setImageURI(imageUri);
            }

        });

        init();
        initClick();
    }

   /* private void showAd() {
        MaxInterstitialAd cachedAd = Utils.onCache(adsList);
        if (cachedAd == null) {

        } else {
            Utils.setCallBcak(cachedAd, new Adcallback() {
                @Override
                public void onShowFail(MaxAd ad) {

                }

                @Override
                public void onAdHidden() {

                }
            });
            cachedAd.showAd(this);
        }
    }*/

    private void onInitIm(float imW, float imH) {
        Point screen = Utils.getScreen(this);
        float newX = screen.x / 2f - imW / 2;
        float newY = screen.y / 2f - imH / 2;
        startMatrix = new Matrix();
        startMatrix.postTranslate(newX, newY);
        imageView.setImageMatrix(startMatrix);

        Log.d("---------------tt", "------startMatrix-----x=" + newX + "------y=" + newY);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:

                startMatrix.set(imageView.getImageMatrix());
                endMatrix.set(imageView.getImageMatrix());

                startPointF.set(event.getX(), event.getY());


                Log.d("---------------tt", "------ACTION_DOWN-----x=" + event.getX() + "------y=" + event.getY());

                MODE = MODE_DRAG;

                break;
            case MotionEvent.ACTION_POINTER_DOWN:

                startMatrix.set(endMatrix);

                distance = Utils.getDistance(event);

                MODE = MODE_SCALE;

                break;
            case MotionEvent.ACTION_MOVE://滑动（单+双）
                if (MODE == MODE_DRAG) {//单指滑动时

                    endMatrix.set(startMatrix);
                    //向矩阵传入位移距离
                    endMatrix.postTranslate(event.getX() - startPointF.x, event.getY() - startPointF.y);
                    Log.d("---------------tt", "------ACTION_MOVE-----x=" + event.getX() + "------y=" + event.getY());
                } else if (MODE == MODE_SCALE) {//双指滑动时
                    //计算缩放倍数
                    scaleMultiple = Utils.getDistance(event) / distance;
                    //获取初始矩阵
                    endMatrix.set(startMatrix);
                    //向矩阵传入缩放倍数
                    endMatrix.postScale(scaleMultiple, scaleMultiple, startPointF.x, startPointF.y);
                }
                break;
            case MotionEvent.ACTION_UP://单指离开
            case MotionEvent.ACTION_POINTER_UP://双指离开
                //手指离开后，重置状态
                MODE = MODE_NONE;

                break;
        }
        //事件结束后，把矩阵的变化同步到ImageView上
        imageView.setImageMatrix(endMatrix);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(imBack)) {

            if (isMain) {
               // showAd();
                finish();
            } else {
                finish();
            }

        } else {
            if (!hasPermission) {
                showNoPermission();
                return;
            }
            if (v.equals(flashIm)) {
                boolean selected = flashIm.isSelected();
                flashIm.setSelected(!selected);
                camera.getCameraControl().enableTorch(!selected);
            } else if (v.equals(imPhoto)) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intentActivityResultLauncher.launch(intent);
            } /*else if (v.equals(imZoom)) {
                if (seekBar.getVisibility() == View.VISIBLE) {
                    seekBar.setVisibility(View.GONE);
                } else if (seekBar.getVisibility() == View.GONE) {
                    seekBar.setVisibility(View.VISIBLE);
                }
            }*/
        }
    }

    private void initClick() {
        imBack.setOnClickListener(this);
        flashIm.setOnClickListener(this);
        imPhoto.setOnClickListener(this);
     //   imZoom.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float v = (seekBar.getMax() - progress) / 10.0f;
                imageView.setAlpha(v);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
        permissions = new String[]{Manifest.permission.CAMERA};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
        if (!checkPermission()) {
            hasPermission = false;
            imageView.setOnTouchListener(null);
            ActivityCompat.requestPermissions(this, permissions, 0);
        } else {
            hasPermission = true;
            imageView.setOnTouchListener(this);
            startCamera();
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(this);
        future.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = future.get();

                Preview preview = new Preview.Builder().build();
                cameraProvider.unbindAll();
                ImageCapture.Builder builder1 = new ImageCapture.Builder();
                imageCapture = builder1.build();
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
                range = Utils.getRange(camera);
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                if (curBitmapPath != null&&curBitmapPath.startsWith("/data/user/")) {
                    Bitmap bitmap = BitmapFactory.decodeFile(curBitmapPath);
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        onInitIm(width, height);
                    }
                }else {
                    Bitmap bitmap = Utils.loadImageFromAssets(this, curBitmapPath);
                    imageView.setImageBitmap(bitmap);
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    onInitIm(width, height);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (checkPermission()) {
                hasPermission = true;
                imageView.setOnTouchListener(this);
                startCamera();
            } else {
                hasPermission = false;
                imageView.setOnTouchListener(null);
            }

        }
    }


    private boolean checkPermission() {
        boolean result = true;
        for (String per : permissions) {
            if (ActivityCompat.checkSelfPermission(this, per) != PackageManager.PERMISSION_GRANTED) {
                result = false;
            }
        }
        return result;

    }

    private void showNoPermission() {
        Toast.makeText(this, getString(R.string.permission_fail), Toast.LENGTH_SHORT).show();
    }
    private void displayImageFromStorage(String imagePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "Image loading failure", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isMain) {
           // showAd();
            finish();
        } else {
            finish();
        }
    }
}