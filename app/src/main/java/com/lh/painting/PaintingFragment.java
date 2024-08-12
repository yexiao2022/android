package com.lh.painting;



import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lh.painting.adapter.HomeAdapter;
import com.lh.painting.databinding.FragmentPaintingBinding;

import com.lh.painting.manager.Keys;
import com.lh.painting.tool.MyItemSpace;

import java.util.List;

public class PaintingFragment extends Fragment {
    private FragmentPaintingBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private String dirStr;
    private int mParam1;

   // private EndlessRecyclerViewScrollListener scrollListener;


    public static PaintingFragment newInstance(int i) {
        PaintingFragment fragment = new PaintingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, i);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_painting, container, false);
        binding = FragmentPaintingBinding.bind(view);

        setRecyclerView();

        return view;
    }
    private void setRecyclerView() {
        //List<String> list = Utils.fileExistsInAssets(requireContext(), dirStr);
        MyItemSpace myItemSpace = new MyItemSpace( 16,20, 15);
        binding.recyclerview.addItemDecoration(myItemSpace);
        dirStr = Keys.getAllDir().get(mParam1);

        HomeAdapter preViewAdapter = new HomeAdapter(requireContext(), dirStr);
      //  binding.recyclerview.addOnScrollListener();
        binding.recyclerview.setAdapter(preViewAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        binding.recyclerview.setLayoutManager(gridLayoutManager);
    }
}