package com.lh.painting.manager;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Range;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.camera.core.Camera;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static final String one_AD = "fd0f2d1eca71b3a1";
    private static final String two_Ad = "f129bbb52cd84647";
    private static final String three_ad = "b42153354e619004";




    public static int getRange(Camera camera) {
        Range<Integer> exposureCompensationRange = camera.getCameraInfo().getExposureState().getExposureCompensationRange();
        Integer upper = exposureCompensationRange.getUpper();
        Integer lower = exposureCompensationRange.getLower();
        return upper;
    }

    public static void setStatusBarTextColor(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    public static Point getScreen(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        Point point = new Point();
        point.x = width;
        point.y = height;
        return point;
    }

    public static boolean checkPermission(Context context, String[] permissions) {
        boolean result = true;
        for (String per : permissions) {
            if (ActivityCompat.checkSelfPermission(context, per) != PackageManager.PERMISSION_GRANTED) {
                result = false;
            }
        }
        return result;

    }

    public static Bitmap loadImageFromAssets(Context context, String fileName) {
        Bitmap bitmap = null;
        AssetManager assetManager = context.getAssets();
        try (InputStream is = assetManager.open(fileName)) {
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            return bitmap;
        }

        return bitmap;

    }

    public static List<String> fileExistsInAssets(Context context, String dir) {

        List<String> pathList = new ArrayList<String>();
        try {
            AssetManager assetManager = context.getAssets();
            String[] files = assetManager.list(dir);

            if (files != null) {
                for (String name : files) {
                    pathList.add(dir + "/" + name);
                }
                return pathList;
            }
        } catch (IOException e) {

            return pathList;
        }
        return pathList;
    }


    public static List<Bitmap> getAnimalsName(Context context, String dir) {
        List<Bitmap> list = new ArrayList<>();
        for (int i = 0; i < 40; i++) {

            Bitmap bitmap = loadImageFromAssets(context, dir + "/" + (i + 1) + ".jpg");
            if (bitmap == null) {
                break;
            } else {
                list.add(bitmap);
            }

        }
        return list;
    }

    public static PackageInfo getInfo(Context context) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException exception) {

            return null;
        }
        return packageInfo;
    }


    public static float getDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

}
