package com.lh.painting;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.lh.painting.databinding.ActivityPrivacyBinding;
import com.lh.painting.databinding.ActivitySettingBinding;

public class PrivacyActivity extends AppCompatActivity {
    private ActivityPrivacyBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding =ActivityPrivacyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.back.setOnClickListener(view -> finish());
        binding.webview.loadUrl("file:///android_asset/privacy.html");

    }
}