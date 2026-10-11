package com.printxpress.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.printxpress.app.core.base.BaseListAdapter;
import com.printxpress.app.data.model.GalleryItem;
import com.printxpress.app.databinding.ItemGalleryPageBinding;
import com.printxpress.app.ui.components.ArtworkImageView;

/**
 * Full-screen pages of the gallery viewer, each with a zoomable artwork.
 * <p>Pattern role: Adapter.</p>
 *
 * @since 1.0
 */
public class GalleryPageAdapter extends BaseListAdapter<GalleryItem, ItemGalleryPageBinding> {

    /** Creates the adapter. */
    public GalleryPageAdapter() {
        super(GalleryItem::getGalleryId, GalleryItem::getTitle);
    }

    /**
     * Inflates a page.
     *
     * @param inflater layout inflater
     * @param parent   the pager
     * @return the binding
     */
    @Override
    protected ItemGalleryPageBinding inflate(LayoutInflater inflater, ViewGroup parent) {
        return ItemGalleryPageBinding.inflate(inflater, parent, false);
    }

    /**
     * Binds a sample and resets any zoom left from a recycled page.
     *
     * @param binding  the page binding
     * @param item     the sample
     * @param position the position
     */
    @Override
    protected void bind(ItemGalleryPageBinding binding, GalleryItem item, int position) {
        binding.zoom.reset();
        binding.imgArt.setArtwork(item.getImageRes(), ArtworkImageView.iconForCategory(item.getCategoryImageRes()));
        binding.imgArt.setContentDescription(item.getTitle());
        binding.txtTitle.setText(item.getTitle());
        binding.txtDescription.setText(item.getDescription());
    }
}
