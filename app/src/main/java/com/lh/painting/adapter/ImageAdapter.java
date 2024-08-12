package com.lh.painting.adapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.lh.painting.R;
import com.lh.painting.dao.TaskDao;
import com.lh.painting.db.AppDatabase;
import com.lh.painting.entity.Favorite;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private TaskDao dao;
    private Context context;
    private Favorite favorite;
    private List<String> imagePaths = new ArrayList<>();
    public ImageAdapter( List<String> imagePaths , Context context) {
        this.imagePaths=imagePaths;
        this.context=context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_home_vh, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.d("22222225555", "onBindViewHolder: "+imagePaths.size());

        initDb();
        String imagePath = imagePaths.get(position);
        Glide.with(context)
                .load(imagePath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (dao.getTaskByUrl(imagePath) != null) {
                    Log.d("2323232", "run: " + dao.getTaskByUrl(imagePath));
                    ((Activity) context).runOnUiThread(new Runnable() {
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
                Intent intent = new Intent(context, CameraActivity.class);
                intent.putExtra("filePath", imagePath);
                context.startActivity(intent);
            }
        });

        holder.collect.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Favorite favorite1 = dao.getTaskByUrl(imagePath);
                        if (favorite1 == null) {
                            favorite = new Favorite();
                            favorite.setPicture(imagePath);
                            AppDatabase.getDatabase(context).taskDao().insertTask(favorite);
                            Log.d("4444444", "run: " + position);
                            Log.d("4444444", "run: " + imagePath);
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    holder.collect.setBackgroundResource(R.drawable.like);
                                    Toast.makeText(context, "Successful collection", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            AppDatabase.getDatabase(context).taskDao().deleteTaskByUrl(imagePath);
                            Log.d("4444444", "ru: " + imagePath);
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    holder.collect.setBackgroundResource(R.drawable.collect);
                                    Toast.makeText(context, "cancel collection", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                }).start();
            }
        });

    }

    @Override
    public int getItemCount() {
        Log.d("2332323", "getItemCount: "+imagePaths.size());
        return imagePaths.size();
    }

    private void initDb() {
        //初始化db
        AppDatabase db = AppDatabase.getDatabase(context);
        dao = db.taskDao();
    }


    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView collect;
        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imPreview);
            collect = itemView.findViewById(R.id.collect);
        }
    }
}

