package com.printxpress.app.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.printxpress.app.R;

/**
 * Pagination dots for onboarding, the promo carousel and the gallery viewer (kit Pagination Dots).
 * The active page is a wider pill.
 *
 * @since 1.0
 */
public class PageIndicator extends LinearLayout {

    /** Dot size. */
    private final int dot;
    /** Active pill width. */
    private final int activeWidth;
    /** Gap between dots. */
    private final int gap;

    /**
     * Creates the indicator.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
        dot = getResources().getDimensionPixelSize(R.dimen.page_dot);
        activeWidth = getResources().getDimensionPixelSize(R.dimen.page_dot_active);
        gap = getResources().getDimensionPixelSize(R.dimen.space_xxs);
    }

    /**
     * Follows a pager's page count and position.
     *
     * @param pager the pager with an adapter already set
     */
    public void attachTo(ViewPager2 pager) {
        RecyclerView.Adapter<?> adapter = pager.getAdapter();
        setCount(adapter == null ? 0 : adapter.getItemCount());
        setSelected(pager.getCurrentItem());
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setSelected(position);
            }
        });
        if (adapter != null) {
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    setCount(adapter.getItemCount());
                    setSelected(pager.getCurrentItem());
                }
            });
        }
    }

    /**
     * Rebuilds the dots.
     *
     * @param count the number of pages
     */
    public void setCount(int count) {
        removeAllViews();
        for (int i = 0; i < count; i++) {
            View v = new View(getContext());
            LayoutParams params = new LayoutParams(dot, dot);
            params.setMargins(gap, 0, gap, 0);
            v.setBackgroundResource(R.drawable.bg_page_dot);
            addView(v, params);
        }
        setVisibility(count > 1 ? VISIBLE : GONE);
    }

    /**
     * Highlights a page.
     *
     * @param position the page index
     */
    public void setSelected(int position) {
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            LayoutParams params = (LayoutParams) v.getLayoutParams();
            boolean active = i == position;
            params.width = active ? activeWidth : dot;
            v.setLayoutParams(params);
            v.setBackgroundResource(active ? R.drawable.bg_page_dot_active : R.drawable.bg_page_dot);
        }
    }
}
