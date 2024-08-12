package com.lh.painting.adapter;


import static androidx.core.content.ContentProviderCompat.requireContext;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lh.painting.CameraActivity;
import com.lh.painting.dao.TaskDao;
import com.lh.painting.db.AppDatabase;
import com.lh.painting.entity.Favorite;
import com.lh.painting.manager.Keys;
import com.lh.painting.R;
import com.lh.painting.manager.Utils;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeVH> {

    private List<String> nameList = Keys.getAllDir();
    private Favorite favorite;
    private Context mCon;
    private TaskDao dao;
    private String dir;
    public static final  String dadgsfgsf = "app_database";
    public static final int aadasdasd = 1;
    public HomeAdapter(Context mCon, String dir) {
        this.mCon = mCon;
        this.dir = dir;
    }

    @NonNull
    @Override
    public HomeVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_home_vh, parent, false);
        return new HomeVH(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull HomeVH holder, @SuppressLint("RecyclerView") int position) {

        String filePath = dir + "/" + (position + 1) + ".jpg";
//Bitmap bitmap = Utils.loadImageFromAssets(mCon, filePath);
        initDb();
        Glide.with(mCon)
                .asDrawable()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .load("file:///android_asset/" + filePath)
                .into(holder.imPreview);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (dao.getTaskByUrl(filePath) != null) {
                    Log.d("2323232", "run: " + dao.getTaskByUrl(filePath));
                    ((Activity) mCon).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.collect.setBackgroundResource(R.drawable.like);
                        }
                    });
                }
            }
        }).start();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mCon, CameraActivity.class);
                intent.putExtra("filePath", filePath);
                mCon.startActivity(intent);
            }
        });


        holder.collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Favorite favorite1 = dao.getTaskByUrl(filePath);
                        if (favorite1 == null) {
                            favorite = new Favorite();
                            favorite.setPicture(filePath);
                            AppDatabase.getDatabase(mCon).taskDao().insertTask(favorite);
                            Log.d("4444444", "run: " + position);
                            Log.d("4444444", "run: " + filePath);
                            ((Activity) mCon).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    holder.collect.setBackgroundResource(R.drawable.like);
                                    Toast.makeText(mCon, "Successful collection", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            AppDatabase.getDatabase(mCon).taskDao().deleteTaskByUrl(filePath);
                            Log.d("4444444", "ru: " + filePath);
                            ((Activity) mCon).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    holder.collect.setBackgroundResource(R.drawable.collect);
                                    Toast.makeText(mCon, "cancel collection", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }

    private void initDb() {
        //初始化db
        AppDatabase db = AppDatabase.getDatabase(mCon);
        dao = db.taskDao();
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public static class HomeVH extends RecyclerView.ViewHolder {
        private ImageView imPreview;
        private ImageView collect;

        public HomeVH(@NonNull View itemView) {
            super(itemView);
            collect = itemView.findViewById(R.id.collect);
            imPreview = itemView.findViewById(R.id.imPreview);
        }
    }
}
