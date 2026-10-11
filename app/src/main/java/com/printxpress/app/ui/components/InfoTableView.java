package com.printxpress.app.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.divider.MaterialDivider;
import com.printxpress.app.R;

/**
 * Simple table of labelled rows with one or more value columns, used for product specifications,
 * the price guide and order line details.
 *
 * @since 1.0
 */
public class InfoTableView extends LinearLayout {

    /**
     * Creates the table.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public InfoTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    /** Removes every row. */
    public void clear() {
        removeAllViews();
    }

    /**
     * Adds a bold header row.
     *
     * @param cells the header cells
     */
    public void addHeader(String... cells) {
        addRowInternal(true, cells);
    }

    /**
     * Adds a row: first cell is the label, the rest are values (right-aligned).
     *
     * @param cells the cells
     */
    public void addRow(String... cells) {
        addRowInternal(false, cells);
    }

    /**
     * Builds a row.
     *
     * @param header whether it is a header
     * @param cells  the cells
     */
    private void addRowInternal(boolean header, String... cells) {
        if (getChildCount() > 0) {
            MaterialDivider divider = new MaterialDivider(getContext());
            divider.setDividerColor(getContext().getColor(R.color.px_outline_variant));
            addView(divider, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(HORIZONTAL);
        int pad = getResources().getDimensionPixelSize(R.dimen.space_sm);
        row.setPadding(0, pad, 0, pad);
        for (int i = 0; i < cells.length; i++) {
            TextView cell = new TextView(getContext());
            cell.setTextAppearance(header ? R.style.TextAppearance_PrintXpress_LabelSmallBold
                    : (i == 0 ? R.style.TextAppearance_PrintXpress_Body_Secondary
                    : R.style.TextAppearance_PrintXpress_BodyMedium));
            cell.setText(cells[i]);
            cell.setGravity(i == 0 ? Gravity.START : Gravity.END);
            row.addView(cell, new LayoutParams(0, LayoutParams.WRAP_CONTENT, i == 0 ? 1.2f : 1f));
        }
        addView(row, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }
}
