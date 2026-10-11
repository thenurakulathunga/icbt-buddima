package com.printxpress.app.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.google.android.material.card.MaterialCardView;
import com.printxpress.app.R;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.util.DateTimeUtils;
import com.printxpress.app.data.model.Design;
import com.printxpress.app.databinding.ViewDesignThumbCardBinding;

import java.util.Locale;

/**
 * Saved design tile (FR-1.7): image preview or PDF/text icon, type tag, title and date. It can
 * show a delete button (Saved designs) or a check mark when picked (Design input).
 *
 * @since 1.0
 */
public class DesignThumbCard extends MaterialCardView {

    /** The inflated children. */
    private final ViewDesignThumbCardBinding binding;

    /**
     * Creates the card.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public DesignThumbCard(Context context, AttributeSet attrs) {
        super(context, attrs, com.google.android.material.R.attr.materialCardViewStyle);
        binding = ViewDesignThumbCardBinding.inflate(LayoutInflater.from(context), this);
    }

    /**
     * Shows a design.
     *
     * @param design the design
     */
    public void bind(Design design) {
        String mime = design.getMimeType();
        boolean isPdf = Constants.MIME_PDF.equals(mime);
        boolean hasFile = design.getFilePath() != null;
        binding.imgPreview.setFile(design.getFilePath(), hasFile && !isPdf,
                hasFile ? (isPdf ? R.drawable.ic_pdf : R.drawable.ic_image) : R.drawable.ic_text);
        binding.txtType.setText(hasFile ? typeLabel(mime) : getContext().getString(R.string.design_custom_text));
        binding.txtTitle.setText(design.getTitle());
        binding.txtDate.setText(getContext().getString(R.string.design_saved_on,
                DateTimeUtils.formatDisplay(design.getCreatedAt() == null ? null : design.getCreatedAt().toLocalDate())));
        setContentDescription(design.getTitle());
    }

    /**
     * Shows or hides the pick check mark.
     *
     * @param selected true to show
     */
    public void setSelectedMark(boolean selected) {
        binding.imgCheck.setVisibility(selected ? VISIBLE : GONE);
        setStrokeColor(getContext().getColor(selected ? R.color.px_primary : R.color.px_outline_variant));
    }

    /**
     * Shows the delete button and registers its action.
     *
     * @param listener run on tap
     */
    public void setOnDelete(Runnable listener) {
        binding.btnDelete.setVisibility(VISIBLE);
        binding.btnDelete.setOnClickListener(v -> listener.run());
    }

    /**
     * Returns a short file type label.
     *
     * @param mime the MIME type
     * @return e.g. PDF, PNG, JPG
     */
    private static String typeLabel(String mime) {
        if (mime == null) {
            return "";
        }
        String sub = mime.substring(mime.indexOf('/') + 1).toUpperCase(Locale.US);
        return "JPEG".equals(sub) ? "JPG" : sub;
    }
}
