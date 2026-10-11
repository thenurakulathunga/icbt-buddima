package com.printxpress.app.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;

import com.google.android.material.card.MaterialCardView;
import com.printxpress.app.R;
import com.printxpress.app.core.util.KeyboardUtils;
import com.printxpress.app.databinding.ViewSearchBarBinding;

import java.util.function.Consumer;

/**
 * Rounded search field with debounced query callbacks (300 ms), a clear button and an optional
 * filter button with an active-filter count (Home, Product list, FAQs).
 *
 * @since 1.0
 */
public class SearchBarView extends MaterialCardView {

    /** Delay before a typed query is reported. */
    private static final long DEBOUNCE_MS = 300;

    /** The inflated children. */
    private final ViewSearchBarBinding binding;
    /** Main-thread handler for debouncing. */
    private final Handler handler = new Handler(Looper.getMainLooper());
    /** Pending debounced report. */
    private Runnable pending;
    /** Query callback, may be null. */
    private Consumer<String> onQueryChanged;
    /** Submit callback, may be null. */
    private Consumer<String> onSubmit;
    /** Suppresses callbacks while text is set programmatically. */
    private boolean silent;

    /**
     * Creates the search bar.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public SearchBarView(Context context, AttributeSet attrs) {
        super(context, attrs, com.google.android.material.R.attr.materialCardViewStyle);
        binding = ViewSearchBarBinding.inflate(LayoutInflater.from(context), this);
        binding.editQuery.setSaveEnabled(false);
        setShapeAppearanceModel(getShapeAppearanceModel().withCornerSize(
                getResources().getDimension(R.dimen.radius_md)));
        setStrokeWidth(0);
        setCardBackgroundColor(getContext().getColor(R.color.px_surface_variant));
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SearchBarView);
        try {
            setHint(a.getString(R.styleable.SearchBarView_px_hint));
            binding.filterContainer.setVisibility(a.getBoolean(R.styleable.SearchBarView_px_showFilter, false)
                    ? VISIBLE : GONE);
        } finally {
            a.recycle();
        }
        wire();
    }

    /** Connects the watcher, clear button and IME action. */
    private void wire() {
        binding.editQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.btnClear.setVisibility(s.length() > 0 ? VISIBLE : GONE);
                if (silent) {
                    return;
                }
                if (pending != null) {
                    handler.removeCallbacks(pending);
                }
                String query = s.toString();
                pending = () -> {
                    if (onQueryChanged != null) {
                        onQueryChanged.accept(query.trim());
                    }
                };
                handler.postDelayed(pending, DEBOUNCE_MS);
            }
        });
        binding.btnClear.setOnClickListener(v -> binding.editQuery.setText(""));
        binding.editQuery.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                KeyboardUtils.hide(v);
                if (onSubmit != null) {
                    onSubmit.accept(getQuery());
                }
                return true;
            }
            return false;
        });
    }

    /**
     * Sets the hint.
     *
     * @param hint the hint
     */
    public void setHint(CharSequence hint) {
        binding.editQuery.setHint(hint);
        binding.editQuery.setContentDescription(hint);
    }

    /** @return the trimmed query */
    public String getQuery() {
        return binding.editQuery.getText().toString().trim();
    }

    /**
     * Sets the query without firing callbacks.
     *
     * @param query the query
     */
    public void setQuery(String query) {
        silent = true;
        binding.editQuery.setText(query);
        silent = false;
    }

    /**
     * Registers the debounced query callback.
     *
     * @param listener receives the trimmed query
     */
    public void setOnQueryChanged(Consumer<String> listener) {
        this.onQueryChanged = listener;
    }

    /**
     * Registers the keyboard search action callback.
     *
     * @param listener receives the trimmed query
     */
    public void setOnSubmit(Consumer<String> listener) {
        this.onSubmit = listener;
    }

    /**
     * Shows the filter button and registers its callback.
     *
     * @param listener run on tap
     */
    public void setOnFilterClick(Runnable listener) {
        binding.filterContainer.setVisibility(VISIBLE);
        binding.btnFilter.setOnClickListener(v -> listener.run());
    }

    /**
     * Shows the number of active filters on the filter button.
     *
     * @param count the count; 0 hides the badge
     */
    public void setFilterCount(int count) {
        binding.txtFilterBadge.setText(String.valueOf(count));
        binding.txtFilterBadge.setVisibility(count > 0 ? VISIBLE : GONE);
    }

    /**
     * Turns the bar into a button that opens the search screen (Home).
     *
     * @param listener run on tap
     */
    public void setClickOnly(Runnable listener) {
        binding.editQuery.setFocusable(false);
        binding.editQuery.setCursorVisible(false);
        binding.editQuery.setOnClickListener(v -> listener.run());
        setOnClickListener(v -> listener.run());
    }

    /** Focuses the query field and shows the keyboard. */
    public void focusInput() {
        KeyboardUtils.show(binding.editQuery);
    }

    /** Stops any pending debounced callback. */
    @Override
    protected void onDetachedFromWindow() {
        if (pending != null) {
            handler.removeCallbacks(pending);
        }
        super.onDetachedFromWindow();
    }
}
