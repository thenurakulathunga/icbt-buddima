package com.printxpress.app.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.DrawableRes;

import com.google.android.material.card.MaterialCardView;
import com.printxpress.app.R;
import com.printxpress.app.data.model.Guideline;
import com.printxpress.app.data.model.enums.GuidelineTopic;
import com.printxpress.app.databinding.ViewGuidelineCardBinding;

/**
 * Print guideline summary card with a topic icon (FR-5.1): Guidelines list and design tips.
 *
 * @since 1.0
 */
public class GuidelineCard extends MaterialCardView {

    /** The inflated children. */
    private final ViewGuidelineCardBinding binding;

    /**
     * Creates the card.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public GuidelineCard(Context context, AttributeSet attrs) {
        super(context, attrs, com.google.android.material.R.attr.materialCardViewStyle);
        binding = ViewGuidelineCardBinding.inflate(LayoutInflater.from(context), this);
    }

    /**
     * Shows a guideline.
     *
     * @param guideline the guideline
     */
    public void bind(Guideline guideline) {
        binding.imgIcon.setImageResource(iconFor(guideline.getTopic()));
        binding.txtTitle.setText(guideline.getTitle());
        binding.txtSummary.setText(guideline.getContent());
        setContentDescription(guideline.getTitle());
    }

    /**
     * Maps a topic to its Hugeicon.
     *
     * @param topic the topic
     * @return the icon
     */
    @DrawableRes
    public static int iconFor(GuidelineTopic topic) {
        switch (topic) {
            case RESOLUTION:
                return R.drawable.ic_dpi;
            case COLOUR:
                return R.drawable.ic_colour;
            case BLEED:
                return R.drawable.ic_bleed;
            case FONTS:
                return R.drawable.ic_font;
            case FILE_FORMAT:
            default:
                return R.drawable.ic_pdf;
        }
    }
}
