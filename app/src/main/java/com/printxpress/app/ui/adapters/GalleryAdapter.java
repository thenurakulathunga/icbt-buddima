package com.printxpress.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.viewbinding.ViewBinding;

import com.printxpress.app.core.base.BaseListAdapter;
import com.printxpress.app.data.model.GalleryItem;
import com.printxpress.app.databinding.ItemGalleryGridBinding;
import com.printxpress.app.databinding.ItemGalleryStripBinding;
import com.printxpress.app.ui.components.GalleryTile;

/**
 * Gallery tiles for the Gallery grid and the Home / Product detail strips.
 * <p>Pattern role: Adapter.</p>
 *
 * @since 1.0
 */
public class GalleryAdapter extends BaseListAdapter<GalleryItem, ViewBinding> {

    /** Whether items are fixed-size strip tiles. */
    private final boolean strip;

    /**
     * Creates the adapter.
     *
     * @param strip true for fixed-size strip tiles
     */
    public GalleryAdapter(boolean strip) {
        super(GalleryItem::getGalleryId, g -> g.getTitle() + g.isSeasonal());
        this.strip = strip;
    }

    /**
     * Inflates a tile.
     *
     * @param inflater layout inflater
     * @param parent   the list
     * @return the binding
     */
    @Override
    protected ViewBinding inflate(LayoutInflater inflater, ViewGroup parent) {
        return strip ? ItemGalleryStripBinding.inflate(inflater, parent, false)
                : ItemGalleryGridBinding.inflate(inflater, parent, false);
    }

    /**
     * Binds a gallery item.
     *
     * @param binding  the row binding
     * @param item     the item
     * @param position the position
     */
    @Override
    protected void bind(ViewBinding binding, GalleryItem item, int position) {
        ((GalleryTile) binding.getRoot()).bind(item);
    }
}
