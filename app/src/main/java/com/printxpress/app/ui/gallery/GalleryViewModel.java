package com.printxpress.app.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.core.base.BaseViewModel;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.Category;
import com.printxpress.app.data.model.GalleryItem;
import com.printxpress.app.data.repository.CatalogRepository;
import com.printxpress.app.data.repository.GalleryRepository;

import java.util.List;

/**
 * Design gallery state (S32–S33, FR-5.4): category filter and the matching samples.
 *
 * @since 1.0
 */
public class GalleryViewModel extends BaseViewModel {

    /** Categories for the filter. */
    private final CatalogRepository catalog;
    /** Samples. */
    private final GalleryRepository gallery;
    /** Filter categories. */
    private final MutableLiveData<Resource<List<Category>>> categories = new MutableLiveData<>();
    /** Matching samples. */
    private final MutableLiveData<Resource<List<GalleryItem>>> items = new MutableLiveData<>();
    /** Selected category, or 0 for all. */
    private long categoryId = -1;

    /**
     * Creates the ViewModel.
     *
     * @param catalog catalogue repository
     * @param gallery gallery repository
     */
    public GalleryViewModel(CatalogRepository catalog, GalleryRepository gallery) {
        this.catalog = catalog;
        this.gallery = gallery;
    }

    /** Loads the filter categories once. */
    public void loadCategories() {
        if (categories.getValue() == null) {
            load(catalog.getCategories(), categories);
        }
    }

    /**
     * Selects a category and loads its samples; selecting the current one does nothing.
     *
     * @param id the category, or 0 for all
     */
    public void selectCategory(long id) {
        if (id != categoryId) {
            categoryId = id;
            load(gallery.getGallery(id), items);
        }
    }

    /** Reloads the samples. */
    public void refresh() {
        load(gallery.getGallery(Math.max(0, categoryId)), items);
    }

    /** @return the selected category, or 0 for all */
    public long getCategoryId() {
        return Math.max(0, categoryId);
    }

    /** @return the filter categories */
    public LiveData<Resource<List<Category>>> getCategories() {
        return categories;
    }

    /** @return the matching samples */
    public LiveData<Resource<List<GalleryItem>>> getItems() {
        return items;
    }
}
