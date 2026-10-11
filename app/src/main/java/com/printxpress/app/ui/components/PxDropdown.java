package com.printxpress.app.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import androidx.annotation.StringRes;

import com.printxpress.app.R;
import com.printxpress.app.databinding.ViewPxDropdownBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Labelled single-choice dropdown over any item type (district, branch, reason).
 * Items are rendered through a label function so the component never depends on model classes.
 *
 * @since 1.0
 */
public class PxDropdown extends LinearLayout {

    /** The inflated children. */
    private final ViewPxDropdownBinding binding;
    /** The current items. */
    private List<Object> items = Collections.emptyList();
    /** The selected index, or -1. */
    private int selectedIndex = -1;
    /** Selection callback, may be null. */
    private Consumer<Object> onSelected;

    /**
     * Creates the dropdown.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public PxDropdown(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        binding = ViewPxDropdownBinding.inflate(LayoutInflater.from(context), this);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PxDropdown);
        try {
            String label = a.getString(R.styleable.PxDropdown_px_label);
            binding.pxLabel.setText(label);
            binding.pxLabel.setVisibility(label == null ? GONE : VISIBLE);
            binding.pxAuto.setHint(a.getString(R.styleable.PxDropdown_px_hint));
            binding.pxAuto.setContentDescription(label);
            int icon = a.getResourceId(R.styleable.PxDropdown_px_startIcon, 0);
            if (icon != 0) {
                binding.pxLayout.setStartIconDrawable(icon);
            }
        } finally {
            a.recycle();
        }
        binding.pxAuto.setOnItemClickListener((parent, view, position, id) -> {
            selectedIndex = position;
            setError(0);
            if (onSelected != null) {
                onSelected.accept(items.get(position));
            }
        });
    }

    /**
     * Replaces the items.
     *
     * @param newItems the items
     * @param label    renders an item as text
     * @param <T>      the item type
     */
    public <T> void setItems(List<T> newItems, Function<T, String> label) {
        items = new ArrayList<>(newItems);
        List<String> labels = new ArrayList<>();
        for (T item : newItems) {
            labels.add(label.apply(item));
        }
        binding.pxAuto.setAdapter(new ArrayAdapter<>(getContext(), R.layout.item_dropdown, labels));
        if (selectedIndex >= items.size()) {
            selectedIndex = -1;
        }
    }

    /**
     * Selects the first item matching a condition.
     *
     * @param match the condition
     * @param <T>   the item type
     */
    @SuppressWarnings("unchecked")
    public <T> void selectWhere(Predicate<T> match) {
        for (int i = 0; i < items.size(); i++) {
            if (match.test((T) items.get(i))) {
                selectedIndex = i;
                binding.pxAuto.setText(String.valueOf(binding.pxAuto.getAdapter().getItem(i)), false);
                return;
            }
        }
    }

    /**
     * Returns the selected item.
     *
     * @param <T> the item type
     * @return the item, or null when nothing is selected
     */
    @SuppressWarnings("unchecked")
    public <T> T getSelected() {
        return selectedIndex < 0 ? null : (T) items.get(selectedIndex);
    }

    /**
     * Registers a selection callback.
     *
     * @param listener receives the selected item
     * @param <T>      the item type
     */
    @SuppressWarnings("unchecked")
    public <T> void setOnItemSelected(Consumer<T> listener) {
        this.onSelected = item -> listener.accept((T) item);
    }

    /**
     * Shows an error.
     *
     * @param messageRes the message, or 0 to clear
     */
    public void setError(@StringRes int messageRes) {
        binding.pxLayout.setError(messageRes == 0 ? null : getContext().getString(messageRes));
        binding.pxLayout.setErrorEnabled(messageRes != 0);
    }
}
