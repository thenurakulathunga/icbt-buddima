package com.printxpress.app.data.repository;

import androidx.lifecycle.LiveData;

import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.Category;
import com.printxpress.app.data.model.Product;
import com.printxpress.app.data.model.ProductFilter;
import com.printxpress.app.data.model.ProductOption;

import java.util.List;

/**
 * Categories, products and product options (FR-2.1–2.5).
 * <p>Pattern role: Repository.</p>
 *
 * @since 1.0
 */
public interface CatalogRepository {

    /** @return the categories with product counts */
    LiveData<Resource<List<Category>>> getCategories();

    /**
     * Lists highlighted products for the home screen.
     *
     * @param limit maximum number
     * @return the products
     */
    LiveData<Resource<List<Product>>> getPopularProducts(int limit);

    /**
     * Searches, filters and sorts products.
     *
     * @param filter the criteria
     * @return the matching products
     */
    LiveData<Resource<List<Product>>> search(ProductFilter filter);

    /**
     * Loads one product.
     *
     * @param productId the product
     * @return the product
     */
    LiveData<Resource<Product>> getProduct(long productId);

    /**
     * Lists a product's options.
     *
     * @param productId the product
     * @return the options in type order
     */
    LiveData<Resource<List<ProductOption>>> getOptions(long productId);

    /**
     * Lists distinct option values available as filters.
     *
     * @param categoryId the category, or 0 for all
     * @return option values carrying their type name
     */
    LiveData<Resource<List<ProductOption>>> getFilterValues(long categoryId);
}
