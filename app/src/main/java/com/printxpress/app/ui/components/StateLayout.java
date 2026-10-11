package com.printxpress.app.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.printxpress.app.R;

/**
 * Wraps a screen's content and switches between LOADING (shimmer skeleton), CONTENT, EMPTY and
 * ERROR (docs/05 §9 rule 3). The content is the single child declared in XML.
 *
 * @since 1.0
 */
public class StateLayout extends FrameLayout {

    /** Loading placeholder: shimmer skeleton or spinner. */
    private final View loadingView;
    /** Empty state. */
    private final EmptyStateView emptyView;
    /** Error state. */
    private final ErrorStateView errorView;
    /** The content child, found after inflation. */
    private View content;

    /**
     * Creates the layout.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public StateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StateLayout);
        int skeleton;
        try {
            skeleton = a.getResourceId(R.styleable.StateLayout_px_skeletonLayout, 0);
        } finally {
            a.recycle();
        }
        if (skeleton != 0) {
            ShimmerFrameLayout shimmer = new ShimmerFrameLayout(context);
            LayoutInflater.from(context).inflate(skeleton, shimmer, true);
            loadingView = shimmer;
        } else {
            CircularProgressIndicator spinner = new CircularProgressIndicator(context);
            spinner.setIndeterminate(true);
            FrameLayout box = new FrameLayout(context);
            box.addView(spinner, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
            loadingView = box;
        }
        emptyView = new EmptyStateView(context, null);
        errorView = new ErrorStateView(context, null);
    }

    /** Captures the XML content child and adds the state views on top. */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        content = getChildCount() > 0 ? getChildAt(0) : null;
        addView(loadingView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(emptyView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(errorView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        showContent();
    }

    /** Shows the loading placeholder. */
    public void showLoading() {
        show(loadingView);
        if (loadingView instanceof ShimmerFrameLayout) {
            ((ShimmerFrameLayout) loadingView).startShimmer();
        }
    }

    /** Shows the content. */
    public void showContent() {
        show(content);
    }

    /**
     * Shows the empty state.
     *
     * @param icon     illustration
     * @param title    title
     * @param message  message
     * @param action   action label, or 0
     * @param onAction action, may be null
     */
    public void showEmpty(@DrawableRes int icon, @StringRes int title, @StringRes int message, @StringRes int action,
                          Runnable onAction) {
        emptyView.bind(icon, title, message, action, onAction);
        show(emptyView);
    }

    /**
     * Shows the error state.
     *
     * @param messageRes the message, or 0 for the default
     * @param onRetry    retry callback
     */
    public void showError(@StringRes int messageRes, Runnable onRetry) {
        errorView.show(messageRes, onRetry);
        show(errorView);
    }

    /**
     * Makes one child visible and hides the rest.
     *
     * @param visible the child to show
     */
    private void show(View visible) {
        if (loadingView instanceof ShimmerFrameLayout && visible != loadingView) {
            ((ShimmerFrameLayout) loadingView).stopShimmer();
        }
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.setVisibility(child == visible ? VISIBLE : GONE);
        }
    }
}
