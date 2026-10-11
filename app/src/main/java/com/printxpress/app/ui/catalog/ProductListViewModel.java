package com.printxpress.app.ui.catalog;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.core.base.BaseViewModel;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.Product;
import com.printxpress.app.data.model.ProductFilter;
import com.printxpress.app.data.model.ProductOption;
import com.printxpress.app.data.repository.CatalogRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Product list state (S08, FR-2.2, FR-2.3): search text, option filters and sort, re-queried on
 * every change. Shared with the filter sheet (S09).
 *
 * @since 1.0
 */
public class ProductListViewModel extends BaseViewModel {

    /** Catalogue. */
    private final CatalogRepository catalog;
    /** Current criteria. */
    private final MutableLiveData<ProductFilter> filter = new MutableLiveData<>();
    /** Results. */
    private final MutableLiveData<Resource<List<Product>>> products = new MutableLiveData<>();
    /** Values available for filtering. */
    private final MutableLiveData<Resource<List<ProductOption>>> filterValues = new MutableLiveData<>();
    /** Whether {@link #start} has run. */
    private boolean started;

    /**
     * Creates the ViewModel.
     *
     * @param catalog catalogue repository
     */
    public ProductListViewModel(CatalogRepository catalog) {
        this.catalog = catalog;
    }

    /**
     * Initialises the criteria once from navigation arguments.
     *
     * @param categoryId the category, or 0
     * @param query      the initial search, may be null
     */
    public void start(long categoryId, String query) {
        if (started) {
            return;
        }
        started = true;
        load(catalog.getFilterValues(categoryId), filterValues);
        apply(ProductFilter.forCategory(categoryId).withQuery(query));
    }

    /**
     * Updates the search text.
     *
     * @param query the text
     */
    public void setQuery(String query) {
        apply(current().withQuery(query));
    }

    /**
     * Updates filters and sort from the sheet.
     *
     * @param values selected option values by type
     * @param sort   the sort order
     */
    public void setFilters(Map<String, Set<String>> values, ProductFilter.Sort sort) {
        apply(current().withOptions(values).withSort(sort));
    }

    /** Removes all option filters and resets the sort. */
    public void clearFilters() {
        apply(current().withOptions(null).withSort(ProductFilter.Sort.POPULAR));
    }

    /** Re-runs the current search. */
    public void retry() {
        apply(current());
    }

    /**
     * Stores and runs a filter.
     *
     * @param next the new criteria
     */
    private void apply(ProductFilter next) {
        filter.setValue(next);
        load(catalog.search(next), products);
    }

    /** @return the current criteria, never null */
    public ProductFilter current() {
        ProductFilter f = filter.getValue();
        return f == null ? ProductFilter.forCategory(0) : f;
    }

    /** @return the current criteria */
    public LiveData<ProductFilter> getFilter() {
        return filter;
    }

    /** @return the results */
    public LiveData<Resource<List<Product>>> getProducts() {
        return products;
    }

    /** @return the filterable values */
    public LiveData<Resource<List<ProductOption>>> getFilterValues() {
        return filterValues;
    }
}
