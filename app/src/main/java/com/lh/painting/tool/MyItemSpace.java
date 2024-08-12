package com.lh.painting.tool;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyItemSpace extends RecyclerView.ItemDecoration {
    private int ex_space = 0;
    private int v_space = 0;
    private int h_space = 0;

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);
        int spanSize = 1;
        int spanIndex = 0;
        int spanCount = 1;

        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager layoutManager1 = (GridLayoutManager) layoutManager;
            GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
            spanCount = layoutManager1.getSpanCount();
            spanSize = layoutManager1.getSpanSizeLookup().getSpanSize(position);
            spanIndex = layoutParams.getSpanIndex();

        }
        if (spanSize == spanCount) {
            outRect.left = v_space + ex_space;
            outRect.right = v_space + ex_space;
            outRect.bottom = h_space;
        } else {
            int itemAllSpacing = (v_space * (spanCount + 1) + ex_space * 2) / spanCount;
            int left = v_space * (spanIndex + 1) - itemAllSpacing * spanIndex + ex_space;
            int right = itemAllSpacing - left;
            outRect.left = left;
            outRect.right = right;
            outRect.bottom = h_space;
        }

    }

    public MyItemSpace(int v_space, int h_space, int ex_space) {
        this.ex_space = ex_space;
        this.h_space = h_space;
        this.v_space = v_space;

    }
}
