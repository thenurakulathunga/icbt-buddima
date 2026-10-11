package com.printxpress.app.data.repository;

import androidx.lifecycle.LiveData;

import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.GalleryItem;

import java.util.List;

/**
 * Sample design gallery (FR-5.2).
 * <p>Pattern role: Repository.</p>
 *
 * @since 1.0
 */
public interface GalleryRepository {

    /**
     * Lists gallery items.
     *
     * @param categoryId the category, or 0 for all
     * @return the items, seasonal first
     */
    LiveData<Resource<List<GalleryItem>>> getGallery(long categoryId);
}
