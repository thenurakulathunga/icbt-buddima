package com.printxpress.app.ui.components;

import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.android.material.card.MaterialCardView;
import com.printxpress.app.R;
import com.printxpress.app.data.model.Faq;
import com.printxpress.app.databinding.ViewExpandableFaqBinding;

/**
 * FAQ accordion row with animated expand and collapse (FR-5.3).
 *
 * @since 1.0
 */
public class ExpandableFaqItem extends MaterialCardView {

    /** Chevron rotation when expanded. */
    private static final float EXPANDED_ROTATION = 180f;

    /** The inflated children. */
    private final ViewExpandableFaqBinding binding;
    /** Whether the answer is showing. */
    private boolean expanded;
    /** Expansion callback, may be null. */
    private java.util.function.Consumer<Boolean> onToggle;

    /**
     * Creates the row.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public ExpandableFaqItem(Context context, AttributeSet attrs) {
        super(context, attrs, com.google.android.material.R.attr.materialCardViewStyle);
        binding = ViewExpandableFaqBinding.inflate(LayoutInflater.from(context), this);
        binding.header.setOnClickListener(v -> {
            setExpanded(!expanded, true);
            if (onToggle != null) {
                onToggle.accept(expanded);
            }
        });
    }

    /**
     * Shows a FAQ.
     *
     * @param faq the FAQ
     */
    public void bind(Faq faq) {
        binding.txtQuestion.setText(faq.getQuestion());
        binding.txtAnswer.setText(faq.getAnswer());
    }

    /**
     * Expands or collapses the answer.
     *
     * @param value   true to expand
     * @param animate whether to animate
     */
    public void setExpanded(boolean value, boolean animate) {
        expanded = value;
        if (animate && getParent() instanceof ViewGroup) {
            AutoTransition transition = new AutoTransition();
            transition.setDuration(getResources().getInteger(R.integer.anim_medium));
            TransitionManager.beginDelayedTransition((ViewGroup) getRootView(), transition);
        }
        binding.txtAnswer.setVisibility(value ? VISIBLE : GONE);
        binding.imgChevron.setRotation(value ? EXPANDED_ROTATION : 0f);
        binding.header.setContentDescription(binding.txtQuestion.getText());
    }

    /**
     * Registers an expansion callback, used to remember state in lists.
     *
     * @param listener receives the new expanded state
     */
    public void setOnToggle(java.util.function.Consumer<Boolean> listener) {
        this.onToggle = listener;
    }
}
