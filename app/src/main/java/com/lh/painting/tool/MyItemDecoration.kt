package com.lh.painting.tool

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class MyItemDecoration(
    private var context: Context,
    private var v: Int,
    private var h: Int,
    private var ex: Int
) :
    RecyclerView.ItemDecoration() {
    // item占满一行时，该item是否需要左右间距
    var mVerticalSpacing = true

    var mHorizontalSpaci = true

    init {

        v = dpToPx(v, context)
        h = dpToPx(h, context)
        ex = dpToPx(ex, context)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        var spanCount = 1
        var spanSize = 1
        var spanIndex = 0


        parent.layoutManager?.run {
            if (this is StaggeredGridLayoutManager) {
                spanCount = this.spanCount
                (view.layoutParams as StaggeredGridLayoutManager.LayoutParams)?.run {
                    if (isFullSpan) spanSize = spanCount
                    spanIndex = this.spanIndex
                }
            } else if (this is GridLayoutManager) {
                spanCount = this.spanCount
                spanSize = this.spanSizeLookup.getSpanSize(position)
                spanIndex = (view.layoutParams as GridLayoutManager.LayoutParams).spanIndex
            } else if (this is LinearLayoutManager) {
                outRect.left = v
                outRect.right = v
                outRect.bottom = h
            }

        }

        if (spanSize == spanCount) {
            outRect.left =
                if (mVerticalSpacing) v + ex else 0
            outRect.right =
                if (mVerticalSpacing) v + ex else 0
            outRect.bottom = if (mHorizontalSpaci) h else 0
        } else {
            val itemAllSpacing = (v * (spanCount + 1) + ex * 2) / spanCount
            val left = v * (spanIndex + 1) - itemAllSpacing * spanIndex + ex
            val right = itemAllSpacing - left
            outRect.left = left
            outRect.right = right
            outRect.bottom = h
        }

    }

    fun dpToPx(dp: Int, context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

}