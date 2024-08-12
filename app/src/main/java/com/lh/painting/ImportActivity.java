package com.lh.painting;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.lh.painting.adapter.ImageAdapter;
import com.lh.painting.databinding.ActivityImportBinding;
import com.lh.painting.tool.MyItemSpace;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImportActivity extends AppCompatActivity {
    private ActivityImportBinding binding;
    RecyclerView recyclerView;
    private static final int PICK_IMAGE_REQUEST_CODE = 202; // 请求码，用于标识图片选择
    private ImageAdapter adapter;
    private List<String> imagePaths = new ArrayList<>(); // 图片路径列表


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityImportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.imageviewBack.setOnClickListener(v -> finish());
        recyclerView = binding.importRecyclerview;
        binding.importPhoto.setOnClickListener(v -> openGallery());
        loadImagePaths();
        setRecyclerView();
        Log.d("22222225555", "onCreate: " + imagePaths.size());
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 处理图片选择结果
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == this.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData(); // 获取选中的图片 URI
            if (selectedImageUri != null) {
                saveImageToInternalStorage(selectedImageUri); // 保存图片到内部存储
            }
            Log.d("22222222222222", "onActivityResult: " + this.getFilesDir());
        }
    }

    private void saveImageToInternalStorage(Uri uri) {
        try {
            // 从 URI 获取输入流
            InputStream inputStream = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream); // 解码图片

            if (bitmap == null) {
                Toast.makeText(this, "Unable to load image", Toast.LENGTH_SHORT).show();
                return;
            }
            // 将图片保存到内部存储
            File internalStorageDir = this.getFilesDir(); // 获取内部存储目录
            File imageFile = new File(internalStorageDir, "image_" + System.currentTimeMillis() + ".jpg");
            Log.d("3333333333333333", "saveImageToInternalStorage: " + imageFile);// 创建新图片文件
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); // 将图片压缩并保存
            outputStream.close();
            inputStream.close();

            String imagePath = imageFile.getAbsolutePath(); // 获取图片绝对路径
            // 检查图片是否已经存在
            new Thread(() -> {
                if (isImageAlreadyExists(imagePath)) {
                    this.runOnUiThread(() -> Toast.makeText(this, "The image already exists", Toast.LENGTH_SHORT).show());
                    imageFile.delete(); // 删除重复的文件
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imagePaths.add(imagePath);
                        Log.d("2332323", "run: "+imagePaths.size());
                        if (!imagePaths.isEmpty()){
                            binding.importBackground.setVisibility(View.GONE);
                            binding.importText.setVisibility(View.GONE);
                        }
                    }
                });
                // 添加图片路径到列表
                this.runOnUiThread(() -> adapter.notifyDataSetChanged()); // 更新 RecyclerView
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setRecyclerView() {
        MyItemSpace myItemSpace = new MyItemSpace(16, 20, 15);
        recyclerView.addItemDecoration(myItemSpace);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        Log.d("22222225555", "onCreate: " + imagePaths.size());
        adapter = new ImageAdapter(imagePaths, this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 检查图片是否已经存在。
     *
     * @param imagePath 图片路径
     * @return 图片是否已存在
     */
    private boolean isImageAlreadyExists(String imagePath) {
        File newImageFile = new File(imagePath);
        // 检查 imagePaths 列表
        for (String path : imagePaths) {
            File existingFile = new File(path);
            if (filesAreIdentical(existingFile, newImageFile)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 比较两个文件是否相同。
     *
     * @param file1 文件1
     * @param file2 文件2
     * @return 文件是否相同
     */
    private boolean filesAreIdentical(File file1, File file2) {
        if (file1.length() != file2.length()) {
            return false; // 文件长度不同，则文件不同
        }
        // 可以根据需求进一步比较文件内容（例如，通过 MD5 哈希值或逐字节比较）
        return true;
    }

    /**
     * 加载图片路径列表。
     */
    private void loadImagePaths() {
        imagePaths.clear();
        File file = this.getFilesDir();

        if (file.listFiles() == null) {
            return;
        } else {
            for (File f : file.listFiles()) {
                if (f.getName().startsWith("image")) {
                    imagePaths.add(f.getAbsolutePath());
                }
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!imagePaths.isEmpty()) {
                    binding.importBackground.setVisibility(View.GONE);
                    binding.importText.setVisibility(View.GONE);
                }else {
                    binding.importBackground.setVisibility(View.VISIBLE);
                    binding.importText.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}


