package com.lh.painting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.lh.painting.adapter.MyFragmentStateAdapter;
import com.lh.painting.databinding.ActivityHomeBinding;
import com.lh.painting.databinding.ActivityMainBinding;
import com.lh.painting.manager.Keys;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    List<Fragment> fragments = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        for (int i = 0; i < Keys.getAllDir().size(); i++) {
            fragments.add(PaintingFragment.newInstance(i));
        }
        for (int i = 0; i < fragments.size(); i++) {
            View tabView = LayoutInflater.from(this).inflate(R.layout.tab_item, null);
            TextView tabTextView = tabView.findViewById(R.id.tab_item);
            String dir = Keys.getAllDir().get(i);
            tabTextView.setText(dir.substring(dir.lastIndexOf("_") + 1));
            TabLayout.Tab tab = binding.tabLayout.newTab();
            tab.setCustomView(tabView);
            binding.tabLayout.addTab(tab);
        }
        binding.viewPager2.setAdapter(new MyFragmentStateAdapter(this, fragments));
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager2.setCurrentItem(tab.getPosition());
                tab.getCustomView().findViewById(R.id.tab_item).setSelected(true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
               // tab.getCustomView().setSelected(true);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }
        });
        binding.imageviewBack.setOnClickListener(v -> finish());
    }
}