package com.lh.painting;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.lh.painting.adapter.HomeAdapter;
import com.lh.painting.adapter.LikeAdapter;
import com.lh.painting.dao.TaskDao;
import com.lh.painting.databinding.ActivityImportBinding;
import com.lh.painting.databinding.ActivityMyFavotriteBinding;
import com.lh.painting.db.AppDatabase;
import com.lh.painting.entity.Favorite;
import com.lh.painting.manager.Keys;
import com.lh.painting.tool.MyItemSpace;

import java.util.ArrayList;
import java.util.List;

public class MyFavotrite extends AppCompatActivity {
    private AppDatabase db;
    private TaskDao dao;
    private ActivityMyFavotriteBinding binding;
    private List<Favorite> favoriteList= new ArrayList<>();
    private LikeAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMyFavotriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.imageviewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("6666666", "onClick: ");
                finish();
            }
        });


        db = AppDatabase.getDatabase(this);
        dao = db.taskDao();
        loadFavoriteImages();



    }
    private void loadFavoriteImages() {
        new Thread(new Runnable() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                List<Favorite> favorites = db.taskDao().getAllTasks();
                Log.d("3333333333", "run: "+favorites.size());
                 favoriteList.clear();
                favoriteList.addAll(favorites);
                Log.d("3333333333", "run: "+favoriteList.size());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (favorites.isEmpty()){
                            binding.likeBackground.setVisibility(View.VISIBLE);
                            binding.likeText.setVisibility(View.VISIBLE);
                        }else {
                            binding.likeBackground.setVisibility(View.GONE);
                            binding.likeText.setVisibility(View.GONE);
                        }
                    }
                });
                adapter = new LikeAdapter(MyFavotrite.this, favoriteList);
                Log.d("3333333333", "setRecyclerview: "+favoriteList.size());
                GridLayoutManager gridLayoutManager = new GridLayoutManager(MyFavotrite.this, 2);
                binding.likeRecyclerview.setLayoutManager(gridLayoutManager);
                MyItemSpace myItemSpace = new MyItemSpace( 16,20, 15);
                binding.likeRecyclerview.addItemDecoration(myItemSpace);
                binding.likeRecyclerview.setAdapter(adapter);
            }

        }).start();


    }
}