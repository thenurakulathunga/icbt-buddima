package com.printxpress.app.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.printxpress.app.R;
import com.printxpress.app.databinding.ViewEmptyStateBinding;

/**
 * Friendly empty state: illustration, title, message and an optional action
 * (docs/05 §9 rule 3: every list has an empty state with a CTA).
 *
 * @since 1.0
 */
public class EmptyStateView extends LinearLayout {

    /** The inflated children. */
    protected final ViewEmptyStateBinding binding;

    /**
     * Creates the view.
     *
     * @param context the context
     * @param attrs   XML attributes, may be null
     */
    public EmptyStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        int pad = getResources().getDimensionPixelSize(R.dimen.space_xl);
        setPadding(pad, pad, pad, pad);
        binding = ViewEmptyStateBinding.inflate(LayoutInflater.from(context), this);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EmptyStateView);
        try {
            binding.imgIllustration.setImageResource(a.getResourceId(R.styleable.EmptyStateView_px_icon, R.drawable.ic_empty));
            binding.txtTitle.setText(a.getString(R.styleable.EmptyStateView_px_title));
            binding.txtMessage.setText(a.getString(R.styleable.EmptyStateView_px_message));
            String action = a.getString(R.styleable.EmptyStateView_px_actionText);
            if (action != null) {
                binding.btnAction.setText(action);
                binding.btnAction.setVisibility(VISIBLE);
            }
        } finally {
            a.recycle();
        }
    }

    /**
     * Fills the view.
     *
     * @param icon     illustration or icon
     * @param title    title
     * @param message  message
     * @param action   action label, or 0 for no button
     * @param onAction action callback, may be null
     */
    public void bind(@DrawableRes int icon, @StringRes int title, @StringRes int message, @StringRes int action,
                     Runnable onAction) {
        binding.imgIllustration.setImageResource(icon);
        binding.txtTitle.setText(title);
        binding.txtMessage.setText(message);
        if (action != 0 && onAction != null) {
            binding.btnAction.setText(action);
            binding.btnAction.setOnClickListener(v -> onAction.run());
            binding.btnAction.setVisibility(VISIBLE);
        } else {
            binding.btnAction.setVisibility(GONE);
        }
    }
}
