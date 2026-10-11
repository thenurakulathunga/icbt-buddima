package com.printxpress.app.ui.catalog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.core.base.BaseViewModel;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.Category;
import com.printxpress.app.data.repository.CatalogRepository;

import java.util.List;

/**
 * Catalog screen state (S07, FR-2.1): all categories with product counts.
 *
 * @since 1.0
 */
public class CatalogViewModel extends BaseViewModel {

    /** Catalogue. */
    private final CatalogRepository catalog;
    /** Categories. */
    private final MutableLiveData<Resource<List<Category>>> categories = new MutableLiveData<>();

    /**
     * Creates the ViewModel and loads categories.
     *
     * @param catalog catalogue repository
     */
    public CatalogViewModel(CatalogRepository catalog) {
        this.catalog = catalog;
        refresh();
    }

    /** Reloads categories. */
    public void refresh() {
        load(catalog.getCategories(), categories);
    }

    /** @return categories */
    public LiveData<Resource<List<Category>>> getCategories() {
        return categories;
    }
}
