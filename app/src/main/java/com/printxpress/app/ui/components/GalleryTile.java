package com.printxpress.app.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.google.android.material.card.MaterialCardView;
import com.printxpress.app.R;
import com.printxpress.app.data.model.GalleryItem;
import com.printxpress.app.databinding.ViewGalleryTileBinding;

/**
 * Sample design tile with title, category and a Seasonal tag (FR-5.2): Gallery, Home strip,
 * Product detail samples.
 *
 * @since 1.0
 */
public class GalleryTile extends MaterialCardView {

    /** The inflated children. */
    private final ViewGalleryTileBinding binding;

    /**
     * Creates the tile.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public GalleryTile(Context context, AttributeSet attrs) {
        super(context, attrs, com.google.android.material.R.attr.materialCardViewStyle);
        binding = ViewGalleryTileBinding.inflate(LayoutInflater.from(context), this);
        setStrokeWidth(0);
        setShapeAppearanceModel(getShapeAppearanceModel().withCornerSize(getResources().getDimension(R.dimen.radius_xl)));
    }

    /**
     * Shows a gallery item.
     *
     * @param item the item
     */
    public void bind(GalleryItem item) {
        binding.imgArt.setArtwork(item.getImageRes(), ArtworkImageView.iconForCategory(item.getCategoryImageRes()));
        binding.txtTitle.setText(item.getTitle());
        binding.txtCategory.setText(item.getCategoryName());
        binding.txtSeasonal.setVisibility(item.isSeasonal() ? VISIBLE : GONE);
        setContentDescription(item.getTitle() + ", " + item.getCategoryName());
    }
}
