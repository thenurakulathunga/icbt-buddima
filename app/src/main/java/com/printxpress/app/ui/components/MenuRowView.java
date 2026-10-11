package com.printxpress.app.ui.components;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.printxpress.app.R;
import com.printxpress.app.databinding.ViewMenuRowBinding;

import java.util.function.Consumer;

/**
 * Tappable list row with icon, title, subtitle and a chevron or switch (Profile, Support hub,
 * Settings). A destructive row (Log out, Delete account) is drawn in the error colour.
 *
 * @since 1.0
 */
public class MenuRowView extends LinearLayout {

    /** The inflated children. */
    private final ViewMenuRowBinding binding;
    /** Switch callback, may be null. */
    private Consumer<Boolean> onCheckedChanged;
    /** Suppresses the switch callback while set programmatically. */
    private boolean silent;

    /**
     * Creates the row.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public MenuRowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.list_item_min));
        int pad = getResources().getDimensionPixelSize(R.dimen.space_md);
        setPadding(pad, getResources().getDimensionPixelSize(R.dimen.space_sm), pad,
                getResources().getDimensionPixelSize(R.dimen.space_sm));
        TypedValue ripple = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, ripple, true);
        setBackgroundResource(ripple.resourceId);
        setClickable(true);
        setFocusable(true);
        binding = ViewMenuRowBinding.inflate(LayoutInflater.from(context), this);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MenuRowView);
        try {
            binding.imgIcon.setImageResource(a.getResourceId(R.styleable.MenuRowView_px_icon, R.drawable.ic_info));
            binding.txtTitle.setText(a.getString(R.styleable.MenuRowView_px_title));
            setSubtitle(a.getString(R.styleable.MenuRowView_px_subtitle));
            boolean showSwitch = a.getBoolean(R.styleable.MenuRowView_px_showSwitch, false);
            binding.switchToggle.setVisibility(showSwitch ? VISIBLE : GONE);
            binding.imgChevron.setVisibility(!showSwitch && a.getBoolean(R.styleable.MenuRowView_px_showChevron, true)
                    ? VISIBLE : GONE);
            if (a.getBoolean(R.styleable.MenuRowView_px_destructive, false)) {
                applyDestructive();
            }
        } finally {
            a.recycle();
        }
        binding.switchToggle.setOnCheckedChangeListener((b, checked) -> {
            if (!silent && onCheckedChanged != null) {
                onCheckedChanged.accept(checked);
            }
        });
        if (binding.switchToggle.getVisibility() == VISIBLE) {
            setOnClickListener(v -> binding.switchToggle.toggle());
        }
    }

    /** Colours the row for destructive actions. */
    private void applyDestructive() {
        int error = getContext().getColor(R.color.px_error);
        binding.txtTitle.setTextColor(error);
        binding.imgIcon.setBackgroundResource(R.drawable.bg_icon_tile_error);
        binding.imgIcon.setImageTintList(ColorStateList.valueOf(getContext().getColor(R.color.px_on_error_container)));
    }

    /**
     * Sets the subtitle; null hides it.
     *
     * @param subtitle the subtitle
     */
    public void setSubtitle(CharSequence subtitle) {
        binding.txtSubtitle.setText(subtitle);
        binding.txtSubtitle.setVisibility(subtitle == null ? GONE : VISIBLE);
    }

    /**
     * Sets the switch without firing the callback.
     *
     * @param checked the state
     */
    public void setChecked(boolean checked) {
        silent = true;
        binding.switchToggle.setChecked(checked);
        silent = false;
    }

    /** @return the switch state */
    public boolean isChecked() {
        return binding.switchToggle.isChecked();
    }

    /**
     * Registers the switch callback.
     *
     * @param listener receives the new state
     */
    public void setOnCheckedChange(Consumer<Boolean> listener) {
        this.onCheckedChanged = listener;
    }
}
