package com.printxpress.app.data.repository;

import androidx.lifecycle.LiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.core.concurrency.AppExecutors;
import com.printxpress.app.data.dao.CategoryDao;
import com.printxpress.app.data.dao.ProductDao;
import com.printxpress.app.data.dao.ProductOptionDao;
import com.printxpress.app.data.model.Category;
import com.printxpress.app.data.model.Product;
import com.printxpress.app.data.model.ProductFilter;
import com.printxpress.app.data.model.ProductOption;

import java.util.List;

/**
 * SQLite-backed {@link CatalogRepository}.
 *
 * @since 1.0
 */
public class CatalogRepositoryImpl extends BaseRepository implements CatalogRepository {

    /** Category access. */
    private final CategoryDao categoryDao;
    /** Product access. */
    private final ProductDao productDao;
    /** Option access. */
    private final ProductOptionDao optionDao;

    /**
     * Creates the repository.
     *
     * @param executors   background executors
     * @param categoryDao category DAO
     * @param productDao  product DAO
     * @param optionDao   option DAO
     */
    public CatalogRepositoryImpl(AppExecutors executors, CategoryDao categoryDao, ProductDao productDao,
                                 ProductOptionDao optionDao) {
        super(executors);
        this.categoryDao = categoryDao;
        this.productDao = productDao;
        this.optionDao = optionDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<List<Category>>> getCategories() {
        return execute(categoryDao::findAllWithCounts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<List<Product>>> getPopularProducts(int limit) {
        return execute(() -> productDao.findPopular(limit));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<List<Product>>> search(ProductFilter filter) {
        return execute(() -> productDao.search(filter));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Product>> getProduct(long productId) {
        return execute(() -> {
            Product product = productDao.findById(productId);
            require(product != null && product.isActive(), R.string.err_product_unavailable);
            return product;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<List<ProductOption>>> getOptions(long productId) {
        return execute(() -> optionDao.findByProduct(productId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<List<ProductOption>>> getFilterValues(long categoryId) {
        return execute(() -> optionDao.findFilterValues(categoryId));
    }
}
