package net.runningcode.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 列表分割线
 */
public class RecycleViewItemDecoration extends RecyclerView.ItemDecoration {

    private int mOffset = -1;

    private boolean firstBlank = false;

    public RecycleViewItemDecoration(int offset) {
        this.mOffset = offset;
    }

    public RecycleViewItemDecoration(int offset, boolean firstBlank) {
        this.mOffset = offset;
        this.firstBlank = firstBlank;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOffset >= 0) {
            outRect.top = mOffset;
            outRect.right = mOffset;
        }

    }
}
