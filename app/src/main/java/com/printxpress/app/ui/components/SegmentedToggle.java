package com.printxpress.app.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.StringRes;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.printxpress.app.R;

import java.util.function.IntConsumer;

/**
 * Two-to-four option switch on a grey pill track with a white selected segment (kit Content
 * Switcher): Email/Phone on login, Upload/Text on design input.
 *
 * @since 1.0
 */
public class SegmentedToggle extends MaterialButtonToggleGroup {

    /** Selection callback, may be null. */
    private IntConsumer onSelectionChanged;

    /**
     * Creates the toggle.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public SegmentedToggle(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSingleSelection(true);
        setSelectionRequired(true);
        setBackgroundResource(R.drawable.bg_segmented_track);
        int pad = getResources().getDimensionPixelSize(R.dimen.space_xxs);
        setPadding(pad, pad, pad, pad);
        addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked && onSelectionChanged != null) {
                onSelectionChanged.accept(indexOfChild(findViewById(checkedId)));
            }
        });
    }

    /**
     * Replaces the segments; the first becomes selected.
     *
     * @param labels the segment labels
     */
    public void setOptions(@StringRes int... labels) {
        removeAllViews();
        for (int label : labels) {
            MaterialButton button = (MaterialButton) LayoutInflater.from(getContext())
                    .inflate(R.layout.view_segment_button, this, false);
            button.setId(View.generateViewId());
            button.setText(label);
            addView(button, new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f));
        }
        if (getChildCount() > 0) {
            check(getChildAt(0).getId());
        }
    }

    /**
     * Selects a segment.
     *
     * @param index the segment index
     */
    public void setSelectedIndex(int index) {
        if (index >= 0 && index < getChildCount()) {
            check(getChildAt(index).getId());
        }
    }

    /** @return the selected segment index, or -1 */
    public int getSelectedIndex() {
        int id = getCheckedButtonId();
        return id == View.NO_ID ? -1 : indexOfChild(findViewById(id));
    }

    /**
     * Registers a selection callback.
     *
     * @param listener receives the selected index
     */
    public void setOnSelectionChanged(IntConsumer listener) {
        this.onSelectionChanged = listener;
    }
}
