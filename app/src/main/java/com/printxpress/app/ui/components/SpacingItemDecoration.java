package com.printxpress.app.ui.components;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Even gaps between RecyclerView items for grids and horizontal or vertical lists (4 dp grid,
 * 16 dp gap; docs/05 §6).
 *
 * @since 1.0
 */
public class SpacingItemDecoration extends RecyclerView.ItemDecoration {

    /** Gap between items in pixels. */
    private final int gap;

    /**
     * Creates the decoration.
     *
     * @param gapPx the gap in pixels
     */
    public SpacingItemDecoration(int gapPx) {
        this.gap = gapPx;
    }

    /**
     * Computes the offsets of one item.
     *
     * @param outRect the offsets to fill
     * @param view    the item view
     * @param parent  the RecyclerView
     * @param state   the layout state
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == RecyclerView.NO_POSITION) {
            return;
        }
        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            int span = ((GridLayoutManager) manager).getSpanCount();
            int column = position % span;
            outRect.left = column * gap / span;
            outRect.right = gap - (column + 1) * gap / span;
            outRect.top = position >= span ? gap : 0;
        } else if (manager instanceof LinearLayoutManager
                && ((LinearLayoutManager) manager).getOrientation() == RecyclerView.HORIZONTAL) {
            outRect.left = position == 0 ? 0 : gap / 2;
            outRect.right = gap / 2;
        } else {
            outRect.top = position == 0 ? 0 : gap / 2;
            outRect.bottom = gap / 2;
        }
    }
}
