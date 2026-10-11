package com.printxpress.app.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

/**
 * Container that lets the user pinch-zoom (1×–4×), pan and double-tap its single child (FR-5.4,
 * full-screen gallery viewer). While zoomed it keeps touches from the parent pager so panning
 * does not change page.
 *
 * @since 1.0
 */
public class ZoomLayout extends FrameLayout {

    /** Smallest scale. */
    private static final float MIN_SCALE = 1f;
    /** Largest scale. */
    private static final float MAX_SCALE = 4f;
    /** Scale applied by a double tap. */
    private static final float DOUBLE_TAP_SCALE = 2.5f;

    /** Pinch detector. */
    private final ScaleGestureDetector scaleDetector;
    /** Pan and double-tap detector. */
    private final GestureDetector gestureDetector;
    /** Current scale. */
    private float scale = MIN_SCALE;
    /** Current horizontal offset. */
    private float offsetX;
    /** Current vertical offset. */
    private float offsetY;

    /**
     * Creates the layout.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public ZoomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        scaleDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
                setScale(scale * detector.getScaleFactor());
                return true;
            }
        });
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(@NonNull MotionEvent e) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, @NonNull MotionEvent e2, float dx, float dy) {
                if (scale > MIN_SCALE) {
                    offsetX -= dx;
                    offsetY -= dy;
                    apply();
                }
                return scale > MIN_SCALE;
            }

            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
                return performClick();
            }

            @Override
            public boolean onDoubleTap(@NonNull MotionEvent e) {
                setScale(scale > MIN_SCALE ? MIN_SCALE : DOUBLE_TAP_SCALE);
                return true;
            }
        });
    }

    /**
     * Routes touches to the detectors.
     *
     * @param event the touch event
     * @return always true, the gestures are consumed here
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        boolean zoomed = scale > MIN_SCALE || event.getPointerCount() > 1;
        getParent().requestDisallowInterceptTouchEvent(zoomed);
        return true;
    }

    /**
     * Reports a single tap as a click so accessibility services see the same action.
     *
     * @return the result of the click handling
     */
    @Override
    public boolean performClick() {
        return super.performClick();
    }

    /**
     * Intercepts multi-touch so pinches reach this layout even over a clickable child.
     *
     * @param event the touch event
     * @return true for pinches and while zoomed
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return event.getPointerCount() > 1 || scale > MIN_SCALE;
    }

    /** Returns the child to its original size and position, e.g. when its page is reused. */
    public void reset() {
        offsetX = 0;
        offsetY = 0;
        setScale(MIN_SCALE);
    }

    /**
     * Sets the scale within the allowed range.
     *
     * @param value the requested scale
     */
    private void setScale(float value) {
        scale = Math.max(MIN_SCALE, Math.min(MAX_SCALE, value));
        if (scale == MIN_SCALE) {
            offsetX = 0;
            offsetY = 0;
        }
        apply();
    }

    /** Applies scale and a clamped offset to the child. */
    private void apply() {
        if (getChildCount() == 0) {
            return;
        }
        View child = getChildAt(0);
        float maxX = (child.getWidth() * scale - getWidth()) / 2f;
        float maxY = (child.getHeight() * scale - getHeight()) / 2f;
        offsetX = clamp(offsetX, Math.max(0, maxX));
        offsetY = clamp(offsetY, Math.max(0, maxY));
        child.setScaleX(scale);
        child.setScaleY(scale);
        child.setTranslationX(offsetX);
        child.setTranslationY(offsetY);
    }

    /**
     * Clamps a value to [-limit, limit].
     *
     * @param value the value
     * @param limit the positive limit
     * @return the clamped value
     */
    private static float clamp(float value, float limit) {
        return Math.max(-limit, Math.min(limit, value));
    }
}
