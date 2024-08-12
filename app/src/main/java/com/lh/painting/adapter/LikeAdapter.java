package com.lh.painting.adapter;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lh.painting.CameraActivity;
import com.lh.painting.R;
import com.lh.painting.dao.TaskDao;
import com.lh.painting.db.AppDatabase;
import com.lh.painting.entity.Favorite;
import com.lh.painting.manager.Keys;
import com.lh.painting.manager.Utils;

import java.util.List;

public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.HomeVH> {

    private Favorite favorite;
    private Context mCon;
    private TaskDao dao;
    private List<Favorite> infoList;
    private Bitmap bitmap;

    public LikeAdapter(Context mCon, List<Favorite> infoList) {
        this.mCon = mCon;
        this.infoList = infoList;
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

        String filePath = infoList.get(position).getPicture();
        if (filePath.startsWith("/data/user/")) {
            bitmap = BitmapFactory.decodeFile(filePath);
        } else {
            bitmap = Utils.loadImageFromAssets(mCon, filePath);
        }
        initDb();
        Glide.with(mCon)
                .asDrawable()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .load(bitmap)
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
                            infoList.remove(holder.getAdapterPosition());

                            Log.d("4444444", "ru: " + filePath);

                            ((Activity) mCon).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    notifyDataSetChanged();

                                    Toast.makeText(mCon, "cancel collection", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }
   /* private void updateFavoriteButton(ImageAdapter.ImageViewHolder holder, String imagePath) {
        LiveData<Favorite> favoriteImageLiveData = dao.getTaskByUrl(imagePath);
        favoriteImageLiveData.observe((LifecycleOwner) mCon, new Observer<Favorite>() {
            @Override
            public void onChanged(Favorite favoriteImage) {
                boolean isFavorite = favoriteImage != null && favoriteImage.isFavorite();
                int iconResId = isFavorite ? R.drawable.like : R.drawable.collect;
                holder.collect.setImageResource(iconResId);
            }
        });
    }*/

    private void initDb() {
        //初始化db
        AppDatabase db = AppDatabase.getDatabase(mCon);
        dao = db.taskDao();
    }

    @Override
    public int getItemCount() {
        return infoList.size();
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
